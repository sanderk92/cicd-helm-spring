#!/bin/bash

# Build the jar
./gradlew clean build

# Build the Docker image
docker build -t demo .

# Load the image into minikube
minikube image load demo
