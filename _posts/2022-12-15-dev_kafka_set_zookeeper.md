---
layout: single
title: "[KAFKA] Zookeeper"
excerpt: "KAFKA - Zookeeper"

categories:
  - tech
tags:
  - [tech, kafka, zookeeper]

toc: false
toc_sticky: true

date: 2022-12-15
last_modified_at: 2022-12-15
---
# Zookeeper
- 분산 코디네이션 서비스를 제공하는 오픈소스 프로젝트로 직접 어플리케이션 작업을 조율하는 것을 쉽게 개발할 수 있도록 도와주는 도구. 
  - 분산 코디네이션 서비스 : 분산 시스템에서 시스템 간의 정보 공유, 상태 체크, 서버들 간의 동기화를 위한 락 등을 처리해주는 서비스
- API를 이용해 동기화나 마스터 선출 등의 작업을 쉽게 구현가능.
- zookeeper는 Apache의 오픈 소스 프로젝트 중 하나이며, 공개 분산형 구성 서비스, 동기 서비스 및 대용량 분산 시스템을 위한 NameSpace 레지스트리를 제공함.
- znode(주키퍼 노드)는 NameSpace 안에 데이터를 저장하며, 클라이언트는 znode를 통해 데이터를 읽거나 씀.
- Leader 노드가 존재하며, Leader를 제외한 노드들은 Leader로부터 데이터를 동기화함.
- Leader에 장애가 발생하게 되면 다른 노드가 Leader의 역할을 맡아 안정성이 뛰어남.
- 일반적인 파일 시스템과 달리 zookeeper 데이터는 메모리에 보관되므로 높은 처리량과 낮은 대기 시간을 갖음.

![kafka_zookeeper](./../../images/tech/inst_zookeeper_01.png)
(위의 그림에서 Server는 Zookeeper, Client는 Kafka)

## Zookeeper 설정
### zookeeper.properties

```powershell
# snapshot 데이터를 저장할 경로를 지정
dataDir=C:/daiso/kafka/kafka_2.13-3.3.1/data/zookeeper

# 클라이언트가 connect할 port 번호 지정
clientPort=2181

# 하나의 클라이언트에서 동시 접속하는 개수 제한, 기본값은 60이며, 0은 무제한
maxClientCnxns=0

# port 충돌을 방지하려면 admin server 비활성화(false)
admin.enableServer=false
# admin.serverPort=8080

# 멀티 서버 설정
# server.id=host:port:port
server.1=localhost:2888:3888
# server.2=server_host_1:2888:3888
# server.3=server_host_2:2888:3888
 
# 멀티 서버 설정시 각 서버의 dataDir 밑에 myid 파일이 있어야함.
# echo 1 > myid
 
# 리더 서버에 연결해서 동기화하는 시간, [멀티서버옵션]
#initLimit=5
 
# 리더 서버를 제외한 노드 서버가 리더와 동기화하는 시간, [멀티서버옵션]
#syncLimit=2
```

<details>
  <summary>Exp.</summary>  
  <pre>

### 참조

  </pre>
</details>