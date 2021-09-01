# skcc-digital-swing-ttt
skcc-digital-swing-ttt

### DB Connection 정보 setting
```
legacy-customer-server, new-customer-server의 
application-realdb.yml 에 db접속 정보 채움
```

### java source build
#### (intellij에서 열 때는 루트 프로젝트를 열지 말고 개별 폴더 별로 열어야 함)
```
# linux or mac
chmod 764 build_all.bash
./build_all.bash

# windows
build_all.bat
```

### docker image build
```
docker compose build
```

### docker container run
```
docker compose up new-customer legacy-customer gateway -d
```

### 고객 정보 등록
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
http://localhost:8080/legacy-customer/swing/api/v1/customers
```

### 고객 번호로 고객 정보 조회 
```
GET
http://localhost:8080/legacy-customer/swing/api/v1/customers/1
```

### 고객명, 생일로 고객 정보 조회 
```
GET
http://localhost:8080/legacy-customer/swing/api/v1/customers?custNm=강인수&birthDt=1998-01-01
```

### 고객 정보 조합 API (고객번호로 고객정보조회 + 해당 고객의 고객명/생일과 같은 고객 목록 조회)
```
GET
http://localhost:8080/composite/swing/api/v1/composite-api/1
```
