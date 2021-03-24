#!/bin/bash

DOCKER_PREFIX=docker.io/flomesh
GATEWAY_IMG=${DOCKER_PREFIX}/samples-api-gateway:latest
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
docker build -t ${CONFIG_IMG}  ./config-service
docker push ${CONFIG_IMG}
printf "\n"

echo "-------------------------------------------------------------------"
echo "Build & Push ${DISCOVERY_IMG}"
echo "-------------------------------------------------------------------"
printf "\n"
docker build -t ${DISCOVERY_IMG}  ./discovery-server
docker push ${DISCOVERY_IMG}
printf "\n"

echo "-------------------------------------------------------------------"
echo "Build & Push ${GATEWAY_IMG}"
echo "-------------------------------------------------------------------"
printf "\n"
docker build -t ${GATEWAY_IMG}  ./api-gateway
docker push ${GATEWAY_IMG}
printf "\n"

echo "-------------------------------------------------------------------"
echo "Build & Push ${RATINGS_IMG}"
echo "-------------------------------------------------------------------"
printf "\n"
docker build -t ${RATINGS_IMG} ./ratings
docker push ${RATINGS_IMG}
printf "\n"

echo "-------------------------------------------------------------------"
echo "Build & Push ${REVIEWS_IMG}"
echo "-------------------------------------------------------------------"
printf "\n"
docker build -t ${REVIEWS_IMG} ./reviews
docker push ${REVIEWS_IMG}
printf "\n"

echo "-------------------------------------------------------------------"
echo "Build & Push ${DETAILS_IMG}"
echo "-------------------------------------------------------------------"
printf "\n"
docker build -t ${DETAILS_IMG}  ./details
docker push ${DETAILS_IMG}
printf "\n"

echo "-------------------------------------------------------------------"
echo "Build & Push ${PRODUCT_PAGE_IMG}"
echo "-------------------------------------------------------------------"
printf "\n"
docker build -t ${PRODUCT_PAGE_IMG} ./productpage
docker push ${PRODUCT_PAGE_IMG}