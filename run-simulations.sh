#!/bin/bash

function runSimulation() {
  kubectl apply -f src/k8s/bakeoff-namespace.yml
  kubectl delete -f src/k8s/bakeoff-db-config.yml
  kubectl delete -f src/k8s/bakeoff-db-deployment.yml
  kubectl delete -f src/k8s/bakeoff-db-service.yml
  kubectl delete -f src/k8s/bakeoff-api-deployment.yml
  kubectl delete -f src/k8s/bakeoff-api-service.yml
  kubectl delete -f src/k8s/bakeoff-api-ingress.yml
  git restore src/k8s/bake-api-deployment.yml
  sed -i '' -E -e "s|\[IMAGE\]|$1|g" src/k8s/bakeoff-api-deployment.yml
  kubectl apply -f src/k8s/bakeoff-db-config.yml
  kubectl apply -f src/k8s/bakeoff-db-deployment.yml
  kubectl apply -f src/k8s/bakeoff-db-service.yml
  kubectl apply -f src/k8s/bakeoff-api-deployment.yml
  kubectl apply -f src/k8s/bakeoff-api-service.yml
  kubectl apply -f src/k8s/bakeoff-api-ingress.yml
  kubectl -n bakeoff rollout status deployment/bakeoff-api
  git restore src/k8s/bake-api-deployment.yml
  mvn clean gatling:test
}

runSimulation "docker.io/callibrity/bakeoff-spring-mvc:0.0.1-SNAPSHOT"