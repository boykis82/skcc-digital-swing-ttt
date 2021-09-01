@echo off

echo 1. build util...
cd util
CALL ./gradlew build
cd ..

cd microservices

cd legacy-customer-service

echo 2. build legacy-customer-client...
cd legacy-customer-client
CALL ./gradlew build
cd ..

echo 3. build legacy-customer-server...
cd legacy-customer-server
CALL ./gradlew build
cd ..

cd ..

echo 4. build new-customer-server...
cd new-customer-server
CALL ./gradlew build
cd ..

cd ..

cd spring-cloud

echo 5. build gateway...
cd gateway
CALL ./gradlew build
cd ..

cd ..