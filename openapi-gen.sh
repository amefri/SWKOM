#!/bin/bash
#
# Spring Generator Configuration-Parameters reference, see https://openapi-generator.tech/docs/generators/spring/
#

# Delete old generated sources if they already exist to avoid residual files
rm -rf ./out/

MSYS_NO_PATHCONV=1 docker run \
  --name openapi-gen \
  -v ${PWD}:/local \
  openapitools/openapi-generator-cli generate \
  -i /local/openapi.yaml \
  -g spring \
  -p pocoModels=true \
  -p useSeperateModelProject=true \
  --package-name at.fhtw.swkom.paperless \
  --api-package at.fhtw.swkom.paperless.controller \
  --model-package at.fhtw.swkom.paperless.services.dto \
  --additional-properties configPackage=at.fhtw.swkom.paperless.config \
  --additional-properties basePackage=at.fhtw.swkom.paperless \
  --additional-properties useSpringBoot3=true \
  --additional-properties useJakartaEe=true \
  -o /tmp/out/

docker cp openapi-gen:/tmp/out/ ./out/

docker rm -f openapi-gen
