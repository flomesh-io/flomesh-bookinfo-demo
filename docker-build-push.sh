#!/bin/bash

DOCKER_PREFIX=docker.io/flomesh
CONFIG_IMG=${DOCKER_PREFIX}/samples-config-service:latest
DISCOVERY_IMG=${DOCKER_PREFIX}/samples-discovery-server:latest
RATINGS_IMG=${DOCKER_PREFIX}/samples-bookinfo-ratings:latest
REVIEWS_IMG=${DOCKER_PREFIX}/samples-bookinfo-reviews:latest
DETAILS_IMG=${DOCKER_PREFIX}/samples-bookinfo-details:latest
PRODUCT_PAGE_IMG=${DOCKER_PREFIX}/samples-bookinfo-productpage:latest

echo "-------------------------------------------------------------------"
echo "Build & Push ${CONFIG_IMG}"
echo "-------------------------------------------------------------------"
printf "\n"
cd config-service
docker build -t ${CONFIG_IMG}  .
docker push ${CONFIG_IMG}
printf "\n"

echo "-------------------------------------------------------------------"
echo "Build & Push ${DISCOVERY_IMG}"
echo "-------------------------------------------------------------------"
printf "\n"
cd ../discovery-server
docker build -t ${DISCOVERY_IMG}  .
docker push ${DISCOVERY_IMG}
printf "\n"

echo "-------------------------------------------------------------------"
echo "Build & Push ${RATINGS_IMG}"
echo "-------------------------------------------------------------------"
printf "\n"
cd ../ratings
docker build -t ${RATINGS_IMG} .
docker push ${RATINGS_IMG}
printf "\n"

echo "-------------------------------------------------------------------"
echo "Build & Push ${REVIEWS_IMG}"
echo "-------------------------------------------------------------------"
printf "\n"
cd ../reviews
docker build -t ${REVIEWS_IMG} .
docker push ${REVIEWS_IMG}
printf "\n"

echo "-------------------------------------------------------------------"
echo "Build & Push ${DETAILS_IMG}"
echo "-------------------------------------------------------------------"
printf "\n"
cd ../details
docker build -t ${DETAILS_IMG}  .
docker push ${DETAILS_IMG}
printf "\n"

echo "-------------------------------------------------------------------"
echo "Build & Push ${PRODUCT_PAGE_IMG}"
echo "-------------------------------------------------------------------"
printf "\n"
cd ../productpage
docker build -t ${PRODUCT_PAGE_IMG} .
docker push ${PRODUCT_PAGE_IMG}