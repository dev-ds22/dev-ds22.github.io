---
layout: single
title: "[KAFKA] Kafka & Zookeeper"
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

- 다운로드 : https://kafka.apache.org/downloads
- tar -xzf kafka_2.13-2.6.0.tgz

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

## Zookeeper 실행하기

```powershell
  $ ./bin/windows/zookeeper-server-start.bat ./config/zookeeper.properties
```

- zookeeper-server-start.bat
  - 윈도우에서 zookeeper 서버를 실행하는 파일
- zookeeper.properties
  - zookeeper 서버 설정 파일
  - 포트 바인딩 등 설정 가능 (default = 2181포트)
  - 추가적인 설정은 공식문서를 확인

- zookeeper-server-start.bat 파일은 인자 값으로 zookeeper.properties를 넘겨주어야 한다.
- zookeeper를 실행하면 위에서 설정한 dataDir 경로에 log, snapshot 데이터가 저장되는 것을 확인할 수 있다.
- zookeeper는 필요한 설정이 있을 경우 공식문서를 참고해 properties를 추가하는 방식으로 사용

```log
  [2022-12-15 15:50:44,948] INFO binding to port 0.0.0.0/0.0.0.0:2181 (org.apache.zookeeper.server.NIOServerCnxnFactory)
  [2022-12-15 15:50:44,969] INFO Using org.apache.zookeeper.server.watch.WatchManager as watch manager (org.apache.zookeeper.server.watch.WatchManagerFactory)
  [2022-12-15 15:50:44,970] INFO Using org.apache.zookeeper.server.watch.WatchManager as watch manager (org.apache.zookeeper.server.watch.WatchManagerFactory)
  [2022-12-15 15:50:44,971] INFO zookeeper.snapshotSizeFactor = 0.33 (org.apache.zookeeper.server.ZKDatabase)
  [2022-12-15 15:50:44,971] INFO zookeeper.commitLogCount=500 (org.apache.zookeeper.server.ZKDatabase)
  [2022-12-15 15:50:44,974] INFO zookeeper.snapshot.compression.method = CHECKED (org.apache.zookeeper.server.persistence.SnapStream)
  [2022-12-15 15:50:44,980] INFO Reading snapshot \tmp\zookeeper\version-2\snapshot.0 (org.apache.zookeeper.server.persistence.FileSnap)
  [2022-12-15 15:50:44,983] INFO The digest value is empty in snapshot (org.apache.zookeeper.server.DataTree)
  [2022-12-15 15:50:45,019] INFO ZooKeeper audit is disabled. (org.apache.zookeeper.audit.ZKAuditProvider)
  [2022-12-15 15:50:45,020] INFO 160 txns loaded in 28 ms (org.apache.zookeeper.server.persistence.FileTxnSnapLog)
  [2022-12-15 15:50:45,021] INFO Snapshot loaded in 50 ms, highest zxid is 0xa0, digest is 277975764531 (org.apache.zookeeper.server.ZKDatabase)
  [2022-12-15 15:50:45,021] INFO Snapshotting: 0xa0 to \tmp\zookeeper\version-2\snapshot.a0 (org.apache.zookeeper.server.persistence.FileTxnSnapLog)
  [2022-12-15 15:50:45,024] INFO Snapshot taken in 4 ms (org.apache.zookeeper.server.ZooKeeperServer)
  [2022-12-15 15:50:45,034] INFO PrepRequestProcessor (sid:0) started, reconfigEnabled=false (org.apache.zookeeper.server.PrepRequestProcessor)
  [2022-12-15 15:50:45,035] INFO zookeeper.request_throttler.shutdownTimeout = 10000 (org.apache.zookeeper.server.RequestThrottler)
  [2022-12-15 15:50:45,047] INFO Using checkIntervalMs=60000 maxPerMinute=10000 maxNeverUsedIntervalMs=0 (org.apache.zookeeper.server.ContainerManager)
```

## Kafka 설정

### server.properties
- Kafka 관련 설정 파일
- 자세한 내용은 공식문서를 확인

```powershell
  # config/server.properties
  ############################# Server Basics #############################
  
  # Broker의 ID로 Cluster내 Broker를 구분하기 위해 사용(Unique 값)
  broker.id=0
  
  ############################# Socket Server Settings #############################
  # Broker가 사용하는 호스트와 포트를 지정, 형식은 PLAINTEXT://your.host.name:port 을 사용
  listeners=PLAINTEXT://:9092
  
  # Producer와 Consumer가 접근할 호스트와 포트를 지정, 기본값은 listeners를 사용
  advertised.listeners=PLAINTEXT://localhost:9092
  
  # 네트워크 요청을 처리하는 Thread의 개수, 기본값 3
  num.network.threads=3
  
  # I/O가 생길때 마다 생성되는 Thread의 개수, 기본값 8
  num.io.threads=8
  
  # socket 서버가 사용하는 송수신 버퍼 (SO_SNDBUF, SO_RCVBUF) 사이즈, 기본값 102400
  socket.send.buffer.bytes=102400
  socket.receive.buffer.bytes=102400
  
  # 서버가 받을 수 있는 최대 요청 사이즈이며, 서버 메모리가 고갈 되는 것 방지
  # JAVA의 Heap 보다 작게 설정해야 함, 기본값 104857600
  socket.request.max.bytes=104857600
  
  ############################# Log Basics #############################
  
  # 로그 파일을 저장할 디렉터리의 쉼표로 구분할 수 있음
  log.dirs=C:/dev/kafka_2.13-2.6.0/logs
  
  # 토픽당 파티션의 수를 의미, 
  # 입력한 수만큼 병렬처리 가능, 데이터 파일도 그만큼 늘어남
  num.partitions=1
  
  # 시작 시 log 복구 및 종료 시 flushing에 사용할 데이터 directory당 Thread 개수
  # 이 값은 RAID 배열에 데이터 directory에 대해 증가하도록 권장 됨
  num.recovery.threads.per.data.dir=1
  
  ############################# Internal Topic Settings #############################
  # 내부 Topic인 "_consumer_offsets", "_transaction_state"에 대한 replication factor
  # 개발환경 : 1, 운영할 경우 가용성 보장을 위해 1 이상 권장(3 정도)
  offsets.topic.replication.factor=1
  transaction.state.log.replication.factor=1
  transaction.state.log.min.isr=1
  
  ############################# Log Retention Policy #############################
  
  # 세그먼트 파일의 삭제 주기, 기본값 hours, 168시간(7일)
  # 옵션 [ bytes, ms, minutes, hours ] 
  log.retention.hours=168
  
  # 토픽별로 수집한 데이터를 보관하는 파일
  # 세그먼트 파일의 최대 크기, 기본값 1GB
  # 세그먼트 파일의 용량이 차면 새로운 파일을 생성
  log.segment.bytes=1073741824
  
  # 세그먼트 파일의 삭제 여부를 체크하는 주기, 기본값 5분(보존 정책)
  log.retention.check.interval.ms=300000
  
  ############################# Zookeeper #############################
  
  # 주키퍼의 접속 정보
  # 쉼표(,)로 많은 연결 서버 포트 설정 가능
  # 모든 kafka znode의 Root directory
  zookeeper.connect=localhost:2181
  
  # 주키퍼 접속 시도 제한시간(time out)
  zookeeper.connection.timeout.ms=18000
  
  
  ############################# Group Coordinator Settings #############################
  
  # GroupCoordinator 설정 - 컨슈머 rebalance를 지연시키는 시간
  # 개발환경 : 테스트 편리를 위해 0으로 정의
  # 운영환경 : 3초의 기본값을 설정하는게 좋음
  group.initial.rebalance.delay.ms=0

```  

## Kafka 실행
- 새로운 터미널을 열어서 Kafka 실행.
- kafka 압축을 푼 base 폴더로 이동한 후 다음 명령어 입력.

```powershell
  $ bin/windows/kafka-server-start.bat config/server.properties
```

- kafka-server-start.bat
  - 윈도우 OS에서 kafka 서버 실행 파일

```log
  [2022-12-15 15:15:18,930] INFO [KafkaServer id=0] started (kafka.server.KafkaServer)
  [2022-12-15 15:15:18,993] INFO [BrokerToControllerChannelManager broker=0 name=forwarding]: Recorded new controller, from now on will use broker localhost:9092 (id: 0 rack: null) (kafka.server.BrokerToControllerRequestThread)
  [2022-12-15 15:15:19,009] INFO [BrokerToControllerChannelManager broker=0 name=alterPartition]: Recorded new controller, from now on will use broker localhost:9092 (id: 0 rack: null) (kafka.server.BrokerToControllerRequestThread)
```  

## 실행 확인
### 1. Topic 생성하기
- localhost:9092 카프카 서버에 quickstart-events란 토픽을 생성.

```powershell
  $ bin/windows/kafka-topics.bat --create --topic quickstart-events --bootstrap-server localhost:9092
```

```log
  Created topic quickstart-events.
```

- 현재 만들어져 있는 토픽 확인

```powershell
  $ bin/windows/kafka-topics.bat --list --bootstrap-server localhost:9092
```

```log
  quickstart-events
```

- 특정 토픽의 설정 확인

```powershell
  $ bin/windows/kafka-topics.bat --describe --topic quickstart-events --bootstrap-server localhost:9092
```

```log
  Topic: quickstart-events        TopicId: lljYqgc-RSiuhsbi9-YttA PartitionCount: 1       ReplicationFactor: 1    Configs: segment.bytes=1073741824
          Topic: quickstart-events        Partition: 0    Leader: 0       Replicas: 0     Isr: 0
```

### 2. Producer, Consumer 실행하기
- 콘솔에서 Producer와 Consumer를 실행하여 실시간으로 토픽에 event를 추가하고 받을 수 있다.
- 터미널을 분할로 띄워서 진행.

#### Producer

```powershell
  bin/windows/kafka-console-producer.bat --topic quickstart-events --bootstrap-server localhost:9092
```

```log
  >send test~~~~~
  >
```

#### Consumer

```powershell
  bin/windows/kafka-console-consumer.bat --topic quickstart-events --from-beginning --bootstrap-server localhost:9092
```

```log
  send test~~~~~
```

스프링 구현관련해서 이하 사이트 정리필요
https://sup2is.github.io/2020/06/03/spring-boot-with-kafka-cluster.html

https://semtax.tistory.com/83



<details>
  <summary>Exp.</summary>  
  <pre>

### 참조

  </pre>
</details>