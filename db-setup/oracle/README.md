# Oracle Database    


## Oracle Database 19c 이미지 준비              
> 참고 : https://github.com/oracle/docker-images/blob/main/OracleDatabase/SingleInstance/README.md

```bash
docker run --name oracle-19c-ee oracle/database:19.3.0-ee
```

> 로그 확인
```
#########################
DATABASE IS READY TO USE!
#########################
```


## Fast Start Image 생성         
> 초기 로딩 시간 절약을 위한 이미지 생성
```bash
docker commit oracle-19c-ee oracle/database:19.3.0-ee-faststart 
```

## Log Miner 활성화 및 계정 정보 초기화          

```bash
docker run \
--name oracle-19c-ee-faststart \
-v ./setup-logminer.sh:/opt/oracle/scripts/startup/990.setup-logminer.sh \
oracle/database:19.3.0-ee-faststart 
```

> 로그 확인
```
========================================================
================= TRY DEBEZIUM SETUP ===================
========================================================

...
...

========================================================
================= END DEBEZIUM SETUP ===================
========================================================
```

```bash
docker commit oracle-19c-ee-faststart oracle/database:19.3.0-ee-logminer
```

## 최종 이미지 확인     

```bash
➜ docker image ls | grep 19.3.0                                  
oracle/database                                  19.3.0-ee-logminer      c54d6bd1b9b5   39 seconds ago   15.3GB
oracle/database                                  19.3.0-ee-faststart     2011e982704d   5 days ago       11.2GB
oracle/database                                  19.3.0-ee               536ba08cb822   5 days ago       6.68GB
```


## 컨테이너 실행      
```bash
docker run -it --rm \
--name oracle \
-p 1521:1521 \
-e ORACLE_PWD=top_secret \
oracle/database:19.3.0-ee-logminer
```