#!/usr/bin/env bash

cd util
chmod 764 gradlew
./gradlew build
cd ..

cd microservices

cd legacy-customer-service

cd legacy-customer-client
chmod 764 gradlew
./gradlew build
cd ..

cd legacy-customer-server
chmod 764 gradlew
./gradlew build
cd ..

cd ..

cd new-customer-server
chmod 764 gradlew
./gradlew build
cd ..

cd ..

cd spring-cloud

cd gateway
chmod 764 gradlew
./gradlew build
cd ..

cd eureka
chmod 764 gradlew
./gradlew build
cd ..

cd ..