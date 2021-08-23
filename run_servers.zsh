#!/usr/bin/env zsh

java -jar spring-cloud/gateway/build/libs/*.jar &
java -jar spring-cloud/eureka-server/build/libs/*.jar &
java -jar microservices/legacy-customer-service/legacy-customer-server/build/libs/*.jar &
java -jar microservices/plm-service/plm-server/build/libs/*.jar &

