#!/bin/bash

docker run -d --name oracle-logminer \
-p 1521:1521 \
-e ORACLE_PWD=my-secret \
-e DEFAULT_USER=lite \
-e DEFAULT_PWD=lite \
-e DEFAULT_LOGMINER_USER=c##logminer \
-e DEFAULT_LOGMINER_PWD=logminer \
oracle/database:19.3.0-ee-logminer

#Parameters:
# -e ORACLE_PWD:          SYS, SYSTEM and PDBADMIN 계정의 password. (Default: secret)
# -e DEFAULT_USER:        Default 계정의 Username. 미설정 시 User를 생성하지 않음. (Optional)
# -e DEFAULT_PWD:        Default 계정의 Password. 미설정 시 User를 생성하지 않음. (Optional)
# -e DEFAULT_LOGMINER_USER:        Default Logminer 계정의 Username. 미설정 시 User를 생성하지 않음. ❗️ c## 으로 시작하는 값이어야 함. (Optional)
# -e DEFAULT_LOGMINER_PWD:        Default Logminer 계정의 Password. 미설정 시 User를 생성하지 않음. (Optional)