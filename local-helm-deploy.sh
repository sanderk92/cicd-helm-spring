#!/bin/bash

# Remove any running deployment
helm uninstall demo

# Redeploy as NodePort to make app accessible
helm install demo ./helm/ --set service.type=NodePort

# Serve the exposed endpoints of this app
minikube service demo
