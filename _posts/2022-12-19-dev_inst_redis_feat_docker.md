---
layout: single
title: "[MSSQL] Docker 환경에서 Redis 설치 및 기본설정"
excerpt: "Docker 환경에서 Redis 설치 및 기본설정"

categories:
  - tech
tags:
  - [tech, redis, docker]

toc: false
toc_sticky: true

date: 2022-12-19
last_modified_at: 2022-12-19
---
# Docker 환경에서 Redis 설치 및 기본설정하기

- 출처 : https://oingdaddy.tistory.com/285

## Docker 설치는 각자

## Docker MSSQL 설치

- Docker설치가 완료되었다면 windows terminal에서 다음 명령어를 순서대로 입력. 

- 1. 도커 이미지 pull
```powershell
  $ sudo docker pull redis
```

# 설정파일 디렉토리 생성 및 설정파일 다운로드
```powershell
  $ mkdir -p /etc/redis
  $ wget http://download.redis.io/redis-stable/redis.conf -O /etc/redis/redis.conf
```
# redis-net이라는 브리지를 사용
```powershell
  $ sudo docker network create redis-net
```

# 컨테이너 실행
```powershell
  $ sudo docker run --name myredis -p 6379:6379 --network redis-net -d -v /etc/redis/redis.conf:/usr/local/etc/redis/redis.conf redis redis-server --appendonly yes
```
# redis-cli 로 실행
```powershell
  $ sudo docker run -it --network redis-net --rm redis redis-cli -h myredis
```

# 컨테이너 접속
```powershell
  $ sudo docker exec -it myredis /bin/bash
```

# 컨테이너 로그


<details>
  <summary>Exp.</summary>  
  <pre>

### 참조

  </pre>
</details>