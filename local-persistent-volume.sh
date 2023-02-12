#!/bin/bash

# Create the required folder if not existing
mkdir -p local-storage

# Mount a local dir to minikube
minikube mount local-storage:/var/influxdb
