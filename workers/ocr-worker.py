#!/usr/bin/env python3
import json
import pika, sys, os
import logging
from minio import Minio
from minio.error import S3Error
import pytesseract
from PIL import Image
from pdf2image import convert_from_path


def get_env(env_name, default_value):
    """
    Retrieve an environment variable by name, or return the default value if not set.

    :param env_name: Name of the environment variable to retrieve.
    :param default_value: Value to return if the environment variable is not set.
    :return: The value of the environment variable or the default value.
    """
    return os.getenv(env_name, default_value)


rabbitmq_credentials = pika.PlainCredentials("user", "password")
minio_client = Minio(access_key="admin", secret_key="password", endpoint=get_env("MINIO_HOST","localhost:9000"), secure=False)

logging.basicConfig(
    level=logging.INFO,
    format="%(asctime)s - %(levelname)s - %(message)s",
    handlers=[
        logging.FileHandler("minio_download.log"),  # Log to a file
        logging.StreamHandler()  # Log to console
    ]
)

logger = logging.getLogger(__name__)


def write_content_to_rabbitmq(minio_file_name: str, content: str) -> None:
    connection = pika.BlockingConnection(pika.ConnectionParameters(host=get_env("RABBITMQ_HOST",'localhost'), credentials=rabbitmq_credentials))
    channel = connection.channel()

    channel.queue_declare(queue='ocr.fromWorker', durable=True)

    ocr_message = json.dumps({'filename': minio_file_name, 'content': content})
    channel.basic_publish(exchange='', routing_key='ocr.fromWorker', body=ocr_message)
    connection.close()


def get_content_from_file(file_path: str, temp_dir: str = "/tmp") -> str:
    try:
        # Open the image file
        logging.info("Opening file: %s", file_path)
        images = convert_from_path(file_path, output_folder=temp_dir, fmt="png")

        # Perform OCR
        logging.info("Performing OCR...")
        extracted_text = ""
        for i, image in enumerate(images):
            logging.info("Performing OCR on page %d...", i + 1)
            text = pytesseract.image_to_string(image)
            extracted_text += f"--- Page {i + 1} ---\n{text}\n"

        # Print the extracted text
        logging.info("OCR completed. Extracted text: %s", extracted_text)

        return extracted_text

    except Exception as e:
        logging.error("An error occurred during OCR: %s", e)


def download_content_to_disk(minio_file_name: str) -> str:
    bucket_name: str = "documents"
    file_path: str = "/tmp/" + minio_file_name

    try:
        # Fetch the object from MinIO
        response = minio_client.get_object(bucket_name, minio_file_name)

        # Save the object to disk
        with open(file_path, "wb") as file_data:
            for chunk in response.stream(32 * 1024):  # Read in chunks of 32 KB
                file_data.write(chunk)
        logger.info(f"File saved successfully to {file_path}")

        # Close the response stream
        response.close()
        response.release_conn()

    except S3Error as e:
        logger.error(f"Error occurred: {e}")

    return file_path


def listen_for_files_to_process():
    connection = pika.BlockingConnection(pika.ConnectionParameters(host=get_env("RABBITMQ_HOST",'localhost'), credentials=rabbitmq_credentials))
    channel = connection.channel()

    channel.queue_declare(queue='ocr.toWorker', durable=True)

    def callback(ch, method, properties, body):
        file_on_disk: str = download_content_to_disk(str(body))
        content: str = get_content_from_file(file_on_disk)
        write_content_to_rabbitmq(str(body), content)

    channel.basic_consume(queue='ocr.toWorker', on_message_callback=callback, auto_ack=True)

    print(' [*] Waiting for messages. To exit press CTRL+C')
    channel.start_consuming()


if __name__ == '__main__':
    try:
        listen_for_files_to_process()
        #write_content_to_rabbitmq("/tmp/blabla", "Hallo Welt")
    except KeyboardInterrupt:
        print('Interrupted')
        try:
            sys.exit(0)
        except SystemExit:
            os._exit(0)
