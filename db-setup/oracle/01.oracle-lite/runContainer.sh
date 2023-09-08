#!/bin/bash

docker run -d --name oracle-lite \
 -p 1521:1521 \
 -e ORACLE_PWD=my-secret \
 -e DEFAULT_USER=lite \
 -e DEFAULT_PWD=lite \
oracle/database:19.3.0-ee-lite

#Parameters:
# -e ORACLE_PWD:          SYS, SYSTEM and PDBADMIN 계정의 password. (Default: secret)
# -e DEFAULT_USER:        Default 계정의 Username. 미설정 시 User를 생성하지 않음. (Optional)
# -e DEFAULT_PWD:        Default 계정의 Password. 미설정 시 User를 생성하지 않음. (Optional)