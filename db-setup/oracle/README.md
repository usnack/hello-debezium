# 오라클 19c 이미지 배포 가이드

<!-- TOC -->
* [Oracle 19c Image](#oracle-19c-image)
  * [0. 파일 다운로드](#0-파일-다운로드)
  * [1. Oracle OpenSource Clone](#1-oracle-opensource-clone)
  * [2. 필요 모듈 다운로드](#2-필요-모듈-다운로드)
  * [3. 이미지 빌드 스크립트 실행](#3-이미지-빌드-스크립트-실행)
  * [4. 이미지 확인](#4-이미지-확인)
  * [5. lite 이미지](#5-lite-이미지)
    * [5.1. 기본 이미지 실행  `20분 가량 소요`](#51-기본-이미지-실행-20분-가량-소요)
    * [5.2. lite 이미지 빌드](#52-lite-이미지-빌드)
    * [5.3. lite 이미지 실행](#53-lite-이미지-실행)
  * [6. AMIS 레포지토리에 이미지 배포](#6-amis-레포지토리에-이미지-배포)
  * [7. 이미지 사용](#7-이미지-사용)
* [Logminer](#logminer)
  * [0. 파일 다운로드](#0-파일-다운로드-1)
  * [1. logminer 임시 컨테이너 실행](#1-logminer-임시-컨테이너-실행)
  * [2. logminer 이미지 빌드](#2-logminer-이미지-빌드)
  * [3. logminer 이미지 실행](#3-logminer-이미지-실행)
  * [4. AMIS 레포지토리에 이미지 배포](#4-amis-레포지토리에-이미지-배포)
  * [5. 이미지 사용](#5-이미지-사용)
<!-- TOC -->

# Oracle 19c Image

💡 아래 가이드는 [공식 문서](https://github.com/oracle/docker-images/tree/main/OracleDatabase/SingleInstance)를 토대로 작성되었습니다.

## 1. Oracle OpenSource Clone

```bash
git clone https://github.com/oracle/docker-images.git 
```

## 2. 필요 모듈 다운로드

***x86_64*** `**windows, macbook intel**`
-  [LINUX.X64_193000_db_home.zip](https://www.oracle.com/database/technologies/oracle19c-linux-downloads.html#license-lightbox)
-  [LINUX.X64_193000_grid_home.zip](https://www.oracle.com/database/technologies/oracle19c-linux-downloads.html#license-lightbox)

***arm64***  `**macbook m1/m2**`
-  [LINUX.ARM64_1919000_db_home.zip](https://www.oracle.com/database/technologies/oracle19c-linux-arm64-downloads.html#license-lightbox)
-  [LINUX.ARM64_1919000_grid_home.zip](https://www.oracle.com/database/technologies/oracle19c-linux-arm64-downloads.html#license-lightbox)

저장 경로 :  `docker-images/OracleDatabase/SingleInstance/dockerfiles/19.3.0`

## 3. 이미지 빌드 스크립트 실행

```bash
docker-images/OracleDatabase/SingleInstance/buildContainerImage.sh -v 19.3.0 -e
```

## 4. 이미지 확인

```bash
➜ docker image ls | grep 19.3.0-ee
oracle/database          19.3.0-ee         50486553a961   About a minute ago   6.68GB
```

## 5. lite 이미지

<aside>
💡 `**oracle/database:19.3.0-ee**`를 컨테이너 기동 시 초기 설정 시간이 과도하게 소요됨. (20분)         
이를 해소하고자 lite 이미지를 빌드.
단, docker commit을 활용하기 때문에 PDB(ORCLPDB1), CDB(ORCLCDB)의 값은 변경 불가능함.

</aside>

### 5.1. 기본 이미지 실행  `20분 가량 소요`

```bash
docker run --name oracle-base oracle/database:19.3.0-ee
```

### 5.2. lite 이미지 빌드

```bash
01.oracle-lithe/buildImage.sh

# 이미지 확인
➜ docker image ls | grep 19.3.0-ee-lite
oracle/database                                   19.3.0-ee-lite       f3ec80dd79eb   5 minutes ago   11.2GB
```

### 5.3. lite 이미지 실행

```bash
docker run -d --name oracle-lite \
 -p 1521:1521 \
 -e ORACLE_PWD=oracle-user-password \
 -e DEFAULT_USER=oracle-default-username \
 -e DEFAULT_PWD=oracle-default-user-password \
oracle/database:19.3.0-ee-lite

Parameters:
 -e ORACLE_PWD:          SYS, SYSTEM and PDBADMIN 계정의 password. (Default: secret)
 -e DEFAULT_USER:        Default 계정의 Username. 미설정 시 User를 생성하지 않음. (Optional)
 -e DEFAULT_PWD:        Default 계정의 Password. 미설정 시 User를 생성하지 않음. (Optional)
```


# Logminer

## 1. logminer 임시 컨테이너 실행

```bash
cd 02.oracle-logminer &&
docker run --rm \
--name oracle-logminer-snapshot \
-v ./scripts/setUpLogminer.sh:/opt/oracle/scripts/startup/Z.100.setUpLogminer.sh \
oracle/database:19.3.0-ee-lite
```

## 2. logminer 이미지 빌드

```bash
02.oracle-logminer/buildImage.sh

# 이미지 확인
➜  docker image ls | grep 19.3.0-ee-logminer
oracle/database                                   19.3.0-ee-logminer            c975caa011f2   3 minutes ago    15.3GB
```

## 3. logminer 이미지 실행

```bash
docker run -d --name oracle-logminer \
 -p 1521:1521 \
 -e ORACLE_PWD=oracle-user-password \
 -e DEFAULT_USER=oracle-default-username \
 -e DEFAULT_PWD=oracle-default-user-password \
 -e DEFAULT_LOGMINER_USER=oracle-default-logminer-username \
 -e DEFAULT_LOGMINER_PWD=oracle-default-logminer-user-password \
oracle/database:19.3.0-ee-logminer

Parameters:
 -e ORACLE_PWD:          SYS, SYSTEM and PDBADMIN 계정의 password. (Default: secret)
 -e DEFAULT_USER:        Default 계정의 Username. 미설정 시 User를 생성하지 않음. (Optional)
 -e DEFAULT_PWD:        Default 계정의 Password. 미설정 시 User를 생성하지 않음. (Optional)
 -e DEFAULT_LOGMINER_USER:        Default Logminer 계정의 Username. 미설정 시 User를 생성하지 않음. ❗️ c## 으로 시작하는 값이어야 함. (Optional)
 -e DEFAULT_LOGMINER_PWD:        Default Logminer 계정의 Password. 미설정 시 User를 생성하지 않음. (Optional)
```
