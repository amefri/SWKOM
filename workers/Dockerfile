FROM docker.io/archlinux/archlinux:base-devel

RUN pacman -Syu --noconfirm tesseract uv tesseract-data-eng tesseract-data-deu python poppler

COPY workers .

RUN uv lock

CMD ["uv", "run", "ocr-worker.py"]