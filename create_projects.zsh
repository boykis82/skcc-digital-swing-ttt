#!/usr/bin/env zsh

mkdir microservices
cd microservices

mkdir service-service
cd service-service

spring init \
--boot-version=2.3.2.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=service-server \
--package-name=org.caltech.miniswing.serviceserver \
--groupId=org.caltech.miniswing.serviceserver \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
service-server

spring init \
--boot-version=2.3.2.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=service-client \
--package-name=org.caltech.miniswing.serviceclient \
--groupId=org.caltech.miniswing.serviceclient \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
service-client

cd ..
mkdir customer-service
cd customer-service

spring init \
--boot-version=2.3.2.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=customer-server \
--package-name=org.caltech.miniswing.customerserver \
--groupId=org.caltech.miniswing.customerserver \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
customer-server

spring init \
--boot-version=2.3.2.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=customer-client \
--package-name=org.caltech.miniswing.customerclient \
--groupId=org.caltech.miniswing.customerclient \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
customer-client

cd ..
mkdir product-service
cd product-service

spring init \
--boot-version=2.3.2.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=product-server \
--package-name=org.caltech.miniswing.productserver \
--groupId=org.caltech.miniswing.productserver \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
product-server

spring init \
--boot-version=2.3.2.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=product-client \
--package-name=org.caltech.miniswing.productclient \
--groupId=org.caltech.miniswing.productclient \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
product-client

cd ..
mkdir plm-service
cd plm-service

spring init \
--boot-version=2.3.2.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=plm-server \
--package-name=org.caltech.miniswing.plmserver \
--groupId=org.caltech.miniswing.plmserver \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
plm-server

spring init \
--boot-version=2.3.2.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=plm-client \
--package-name=org.caltech.miniswing.plmclient \
--groupId=org.caltech.miniswing.plmclient \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
plm-client

cd ..
spring init \
--boot-version=2.3.2.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=util \
--package-name=org.caltech.miniswing.util \
--groupId=org.caltech.miniswing.util \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
util

mkdir billing-service
cd billing-service

spring init \
--boot-version=2.3.2.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=plm-client \
--package-name=org.caltech.miniswing.billingclient \
--groupId=org.caltech.miniswing.billingclient \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
billing-client

cd ..

mkdir spring-cloud
cd spring-cloud
spring init \
--boot-version=2.3.2.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=util \
--package-name=org.caltech.miniswing.eurekaserver \
--groupId=org.caltech.miniswing.eurekaserver \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
eureka-server

spring init \
--boot-version=2.3.2.RELEASE \
--build=gradle \
--java-version=1.8 \
--packaging=jar \
--name=util \
--package-name=org.caltech.miniswing.gateway \
--groupId=org.caltech.miniswing.gateway \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
gateway

cd ..