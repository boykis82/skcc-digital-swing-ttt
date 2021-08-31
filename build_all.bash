#!/usr/bin/env bash

cd util
./gradlew build
cd ..

cd microservices

cd legacy-customer-service

cd legacy-customer-client
./gradlew build
cd ..

cd legacy-customer-server
./gradlew build
cd ..

cd ..

cd new-customer-server
./gradlew build
cd ..

cd spring-cloud

cd gateway
./gradlew build
cd ..

cd eureka
./gradlew build
cd ..

cd ..