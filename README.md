# skcc-digital-swing-ttt
skcc-digital-swing-ttt

### java source build
```
./gradlew build
```

### docker image build
```
docker compose build
```

### docker container run
```
docker compose up new-customer legacy-customer gateway eureka -d
```

### 고객 등록
```
POST
{
    "custNm": "강인수",
    "birthDt": "1998-01-01",
    "custTypCd": {
        "key": "C01",
        "value": "개인"
    }
}
```
```
http://localhost:8080/legacy-customer/swing/api/v1/customers
```
