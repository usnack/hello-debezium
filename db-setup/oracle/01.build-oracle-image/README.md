# build oracle image (19c)       

[공식 문서](https://github.com/oracle/docker-images/blob/main/OracleDatabase/SingleInstance/README.md)를 참고하여 진행   

<!-- TOC -->
* [1. 오픈소스 Clone](#1-오픈소스-clone-)
* [2. 필요 모듈 다운로드](#2-필요-모듈-다운로드-)
* [3. 이미지 빌드 스크립트 실행](#3-이미지-빌드-스크립트-실행)
* [4. 이미지 확인](#4-이미지-확인)
* [5. fast 이미지 빌드](#5-fast-이미지-빌드-)
    * [5.1. 기본 이미지 실행](#51-기본-이미지-실행-)
    * [5.2. 초기 설정 완료된 이미지 커밋](#52-초기-설정-완료된-이미지-커밋-)
    * [5.3. snapshot 이미지를 기반으로 fast 이미지 빌드](#53-snapshot-이미지를-기반으로-fast-이미지-빌드-)
<!-- TOC -->

## 1. 오픈소스 Clone      

```shell
git clone https://github.com/oracle/docker-images.git 
```

## 2. 필요 모듈 다운로드       
> `docker-images/OracleDatabase/SingleInstance/dockerfiles/19.3.0/` 디렉토리에 저장

`windows, macbook intel`    
-  [LINUX.X64_193000_db_home.zip](https://www.oracle.com/database/technologies/oracle19c-linux-downloads.html#license-lightbox)    
-  [LINUX.X64_193000_grid_home.zip](https://www.oracle.com/database/technologies/oracle19c-linux-downloads.html#license-lightbox)

`macbook m1/m2`     
-  [LINUX.ARM64_1919000_db_home.zip](https://www.oracle.com/database/technologies/oracle19c-linux-arm64-downloads.html#license-lightbox)
-  [LINUX.ARM64_1919000_grid_home.zip](https://www.oracle.com/database/technologies/oracle19c-linux-arm64-downloads.html#license-lightbox)


## 3. 이미지 빌드 스크립트 실행

```shell
docker-images/OracleDatabase/SingleInstance/buildContainerImage.sh -v 19.3.0 -e
```

## 4. 이미지 확인

```
➜ docker image ls | grep 19.3.0-ee
oracle/database                                  19.3.0-ee               536ba08cb822   6 days ago     6.68GB
```

## 5. fast 이미지 빌드        
`oracle/database:19.3.0-ee`를 컨테이너 기동 시 초기 setup 시간이 과중하게 소요됨.         
이를 위해 fast 이미지 생성하면 매우 편리함.        

#### 5.1. 기본 이미지 실행          
```shell
docker run --name oracle-base oracle/database:19.3.0-ee
```

#### 5.2. 초기 설정 완료된 이미지 커밋        
```shell
docker commit oracle-base oracle/database:19.3.0-ee-snapshot
```

#### 5.3. snapshot 이미지를 기반으로 fast 이미지 빌드         
```shell
cd fast
docker build -t oracle/databse:19.3.0-ee-fast .
```

이미지 확인
```
➜ docker image ls | grep 19.3.0-ee-fast
oracle/databse                                   19.3.0-ee-fast          7a0e49351c0a   23 seconds ago   11.2GB
```