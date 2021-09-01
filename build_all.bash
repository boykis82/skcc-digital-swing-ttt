#!/usr/bin/env bash

echo "1. build util..."
cd util
chmod 764 gradlew
./gradlew build
cd ..

cd microservices

cd legacy-customer-service

echo "2. build legacy-customer-client..."
cd legacy-customer-client
chmod 764 gradlew
./gradlew build
cd ..

echo "3. build legacy-customer-server..."
cd legacy-customer-server
chmod 764 gradlew
./gradlew build
cd ..

cd ..

echo "4. build new-customer-server..."
cd new-customer-server
chmod 764 gradlew
./gradlew build
cd ..

cd ..

cd spring-cloud

echo "5. build gateway..."
cd gateway
chmod 764 gradlew
./gradlew build
cd ..

cd ..