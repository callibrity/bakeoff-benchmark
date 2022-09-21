#!/bin/bash

function runSimulation() {
  echo "Running simulation for image $1..."
  mkdir -p "target/bakeoff-results/$2"
  kubectl top pod -n bakeoff > "target/bakeoff-results/$2/top-before.txt"
  kubectl apply -f src/k8s/bakeoff-namespace.yml
  kubectl delete -f src/k8s/bakeoff-db-deployment.yml
  kubectl delete -f src/k8s/bakeoff-db-service.yml
  kubectl delete -f src/k8s/bakeoff-db-config.yml
  kubectl delete -f src/k8s/bakeoff-api-deployment.yml
  kubectl delete -f src/k8s/bakeoff-api-ingress.yml
  kubectl delete -f src/k8s/bakeoff-api-service.yml
  kubectl apply -f src/k8s/bakeoff-db-config.yml
  kubectl apply -f src/k8s/bakeoff-db-service.yml
  kubectl apply -f src/k8s/bakeoff-db-deployment.yml
  kubectl -n bakeoff rollout status deployment/bakeoff-db
  kubectl apply -f src/k8s/bakeoff-api-service.yml
  kubectl apply -f src/k8s/bakeoff-api-ingress.yml
  git restore src/k8s/bakeoff-api-deployment.yml
  sed -i -E -e "s|\[IMAGE\]|$1|g" src/k8s/bakeoff-api-deployment.yml
  kubectl apply -f src/k8s/bakeoff-api-deployment.yml
  kubectl -n bakeoff rollout status deployment/bakeoff-api
  git restore src/k8s/bakeoff-api-deployment.yml

  mvn gatling:test -Dgatling.runDescription=$2
  kubectl top pod -n bakeoff > "target/bakeoff-results/$2/top-after.txt"
}

mvn clean
runSimulation "docker.io/callibrity/bakeoff-dapper-netcore:1.0.0" "bakeoff-dapper-netcore"
#runSimulation "docker.io/callibrity/bakeoff-grails:1.0.0" "bakeoff-grails"
#runSimulation "docker.io/callibrity/bakeoff-go-gin:1.0.2" "bakeoff-go-gin"
#runSimulation "docker.io/callibrity/bakeoff-node-express:latest" "bakeoff-node-express"
#runSimulation "docker.io/callibrity/bakeoff-quarkus:1.0.2-native" "bakeoff-quarkus-native"
#runSimulation "docker.io/callibrity/bakeoff-quarkus:1.0.2-jvm" "bakeoff-quarkus-jvm"
#runSimulation "docker.io/callibrity/bakeoff-kotlin-micronaut:1.0.0-jvm" "bakeoff-kotlin-micronaut-jvm"
#runSimulation "docker.io/callibrity/bakeoff-kotlin-micronaut:1.0.0-native" "bakeoff-kotlin-micronaut-native"
#runSimulation "docker.io/callibrity/bakeoff-kotlin-ktor:0.0.1" "bakeoff-kotlin-ktor"
#runSimulation "docker.io/callibrity/bakeoff-spring-mvc:1.0.0-native" "bakeoff-spring-mvc-native"
#runSimulation "docker.io/callibrity/bakeoff-spring-mvc:1.0.0-jvm" "bakeoff-spring-mvc-jvm"
#runSimulation "docker.io/callibrity/bakeoff-python-django:1.0.0" "bakeoff-python-django"