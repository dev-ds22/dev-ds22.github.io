이번글은 메시지 큐에 대한 개념과 여러가지 미들웨어를 훑어보기 위한 글 입니다.
웹 서버를 구성하게 되면 성능에 대한 고려는 빼먹을 수 없습니다. 데이터 처리를 하다보면 
너무 많은 처리로 인해 대기하는 요청이 쌓이게 됩니다. 그리곤 서버의 성능이 저하되는데, 
최악의 경우에는 서버가 다운되는 상황까지 직면하게 됩니다. (많이 안타까운 상황이죠...ㅠ)

이런 상황을 방지하기 위해 서버사이드에서는 로드밸런싱도 하고, DB사이드에서는 H/A, A/A 방식으로 
구성도 하고 합니다. 하지만 여러가지 측면에서 볼 때, 비용도 많이 들고 DB사이드에서의 구성은 
쉽지도 않습니다. 또한 DB 접속에 대한 한계도 있기 때문에 다른 방법을 택하게 될지도 모릅니다. 
그래서 그나마 빠르고 좀 더 원활한 서비스(?)를 위해 비동기 메시지 처리 방식을 구성하게 됩니다.
간단하게 메시지큐를 설명하기에 앞서 그와 관련된 개념들을 정리하고자 합니다.

- 메시지 지향 미들웨어(Message Oriented Middleware : MOM) 
: 분산 시스템 간 메시지를 주고 받는 기능을 지원하는 소프트웨어나 하드웨어 인프라
 
- 메시지 큐(Message Queue : MQ)
: MOM을 구현한 시스템
 
- 브로커(Broker)
: Message Queue 시스템
 
- AMQP(Advanced Message Queueing Protocol)
: 메시지 지향 미들웨어를 위한 프로토콜

기본적인 원리는 이렇습니다.

Producer(생산자)가 Message를 Queue에 넣어두면, Consumer가 Message를 가져와 처리하는 방식입니다.
굳이 이런 시스템이 필요할까..? 한번 더 거치게 되면 더 느려지는게 아닌가? 라는 의문이 들 수 있습니다. 
위에서도 언급한바 있지만 다시 한번 말씀드리자면, Client와 동기방식으로 많은 데이터 통신을 하게 되면 
병목현상이 생기게되고, 서버의 성능이 저하됩니다. 
이런 현상을 막고자하여 또 하나의 미들웨어에 메시지를 위임하여 순차적으로 처리를 하는 것 입니다.
그래서 메시지큐가 짱이다..?!
사실상, 무조건적인 것은 없다고 봅니다. 

단점으로 보자면, 즉각적인 서비스를 불가능합니다. 최대한 텀을 줄이기 위해 MQ를 튜닝 한다던지, 
Consumer를 늘린다던지(일부 MQ에 해당), Consumer에서 병목현상을 찾고 리팩토링으로 좀 더 빠른 서비스를 
제공 할 순 있습니다. 반대로 Consumer 역할에서 서버사이드나 DB사이드에서 제대로 받춰주지 못한다면, 
브로커 자체 성능이 저하될 수 있습니다.

메시지큐를 지원하는 API와 미들웨어들을 간략하게 정리 해보겠습니다.

Spring Intergration

- 스프링 기반의 메시지 처리 모듈

장점
1. 스프링 기반이기 때문에, 스프링에 쉽게 적용 가능
2. TaskExcutor를 이용한 단순한 구조

단점
1. 확장성 부족(로드밸런싱, 클러스터링 불가능)
2. 시스템 에러시, 데이터 유실
3. 모니터링 도구 없음

JMS
- J2EE에서 지원하는 메시지 처리 API

단점
1. JAVA 전용(AMQP 지원 하지 않음)
2. 모니터링 도구 없음

ActiveMQ
- JAVA로 만든 오픈 소스 메시지 브로커(JMS 확장형?)

장점
1. 다양한 언어 지원
2. STOMP를 통해서도 접근 가능
3. JDBC를 사용하여 매우 빠른 Persitence 지원
4. 클러스터링 가능
5. REST API를 통해 웹기반 메시징 지원
6. AJAX를 통해 순수한 DHTML를 사용한  웹스트리밍 지원

단점
1. 모니터링 도구 없음


RabbitMq
- AMQP를 지원하는 오픈소스 메시징 시스템

장점
1. 실시간 모니터링 및 관리 용이
2. 다양한 언어 지원
3. 클러스터링 가능

단점
1. Window OS 시, Erlang, OpenSSL 설치 필요

항목	RabbitMQ	ActiveMQ
TOPIC/QUEUE 방식	O	O
RPC 방식	O	O
JMS 또는 AMQP	O	O
Binding 기능	O	X
MQTT	O	O
Embedded Broker	X	O
모니터링	Very Good	Bad
웹컨트롤	Very Good	Bad
Publisher Flow-Control	O	O(설정시)
Broker Clustering	O	O
Installation	윈도우 설치시 OpenSSL, Erlang 필요	Java Wrapper 사용
<RabbitMQ 와 ActiceMQ 비교(출처 : http://blog.ryanjin.net/blog/rabbitmq/)>


Kafka
: 대용량 실시간 로그 처리에 특화되어 설계된 메시지 시스템

장점
1. AMQP나 JMS를 사용하지 않고 단순 메시지 헤더를 지닌 TCP 통신
2. 개별 전송이 아닌 다수 전송 가능(Batch 처리 가능)
3. 파일 시스템에 저장(데이터의 영속성 보장)
4. 대기 중인 메시지로 인한 시스템 성능 감소 줄임
5. 분산 시스템이 기본적으로 설계

단점
1. 큐의 기능은 기존 JMS나 Broker 보다 부족
※ 대용량 CEP엔진에서는 Kafka를 사용하고, 일반적으로는 ActiveMQ, RabbitMQ급의 미들웨어로만 으로도 성능은 충분합니다.

참고
- http://zzong.net/post/3
- http://epicdevs.com/17
출처: https://heowc.tistory.com/35 [허원철의 개발 블로그]


KAFKA
1. Kafka란?
  Kakfka는 대용량 분산 Queue로써 대용량(초당 10만건 이상)에 대한 Topioc을 처리할 수 있음
  Kafka를 분산 Cluster를 구성하기 위해서는 Zookeeper 라는 코디네이션이 필요

2. Zookeeper란?
  분산 애플리케이션 관리를 위한 안정적 코디네이션 애플리케이션
  각 애플리케이션의 정보를 중앙에 집중하고 구성 관리, 그룹 관리 네이밍, 동기화 등의 서비스 제공
  직접 개발하기보다 안정적이라고 검증된 주키퍼를 많이 사용
  카프카, 스톰, hbase, Nifi 등에서 사용됨
  znode : 데이터를 저장하기 위한 공간 이름, 폴더 개념
  주키퍼 데이터는 메모리에 저장되어 처리량이 매우 크고 속도 또한 빠름
  앙상블(클러스터)라는 호스트 세트를 구성해 살아있는 노드 수가 과반수 이상 유지되면 지속적 서비스가 가능
    과반수 방식으로 운영되어 홀수로 서버를 구성
    3대 : 최대 초당 약 80,000 request 처리
    5대 : 최대 초당 약 140,000 request 처리
  로그
    로그는 별도의 디렉토리에 저장
    znode에 변경사항이 발생하면 트랜잭션 로그에 추가됨
    로그가 어느정도 커지면 모든 znode의 상태 스냅샷이 파일시스템에 저장
  myid
    주키퍼 노드를 구분하기 위한 ID
    각 클러스터에 다른 값 설정
  환경설정
    zoo.cfg
    공식 문서 참고
    
3. Kafka Cluster 설치 
1) jdk 설치 :  kafka 2.x  버젼이상을 경우 JDK1.8 버젼 설치
  $yum install jdk-1.8.0*
  $alternatives --config java : 1.8.0 Java버젼으로 path 변경
  $java -version   : 확인

2) Kafka/Zookeeper 설치
  wget http://mirror.navercorp.com/apache/zookeeper/zookeeper-3.4.13/zookeeper-3.4.13.tar.gz tar zxf zookeeper-3.4.13.tar.gz
  
  심볼릭 링크 설정 : 주키퍼 버전을 올릴 경우 심볼릭 링크가 없으면 모두 변경해야 함
    ln -s zookeeper-3.4.13 zookeeper
    // 확인하고 싶은 경우
    ls -la zookeeper
    >>> lrwxrwxrwx 1 byeon byeon 16 Jul 24 05:11 zookeeper -> zookeeper-3.4.13
  
  데이터 디렉토리 생성
    mkdir data
  
  myid 설정
    cd data
    vi myid
    // 내용은 1
    // 다른 클러스터(서버)에도 data/myid에 2, 3 작성 후 저장

  zoo.cfg 설정
    cd /usr/local/zookeeper/conf
    vi zoo.cfg

  zoo.cfg 파일
    분산일 경우 server.1 밑부분을 작성하고 standalone일 경우엔 비움

  // zoo.cfg
    tickTime=2000
    initLimit=10
    syncLimit=5
    dataDir=/usr/local/data
    clientPort=2181
    server.1=0.0.0.0:2888:3888
    server.2={머신2의 ip}:2888:3888
    server.3={머신3의 ip}:2888:3888
    
  실행
    /usr/local/zookeeper/bin/zkServer.sh start
    
  중지
    /usr/local/zookeeper/bin/zkServer.sh stop
    
  systemd service 파일 작성
    관리를 효율적으로 하기 위해 등록
    예기치 않게 서버의 오작동으로 리부팅된 경우 특정 프로세스는 자동으로 시작, 특정 프로세스는 수동 시작일 경우가 있음
    
    vi /etc/systemd/system/zookeeper-server.service
    
  [Unit]
    Description=zookeeper-server
    After=network.target

  [Service]
    Type=forking
    User=root
    Group=root
    SyslogIdentifier=zookeeper-server
    WorkingDirectory=/usr/local/zookeeper
    Restart=on-failure
    RestartSec=0s
    ExecStart=/usr/local/zookeeper/bin/zkServer.sh start
    ExecStop=/usr/local/zookeeper/bin/zkServer.sh stop
    
  systemd service 등록 및 실행
    systemctl daemon-reload
    systemctl enable zookeeper-server.service
    systemctl start zookeeper-server.service
    // 중지는
    // systemctl stop zookeeper-server.service
    // 상태 확인은
    // systemctl status zookeeper-server.service
    
  Cli 모드로 접속
    /usr/local/zookeeper/bin/zkCli.sh -server localhost:2181
    
아파치 카프카
  설치
    cd /usr/local/
    wget http://apache.mirror.cdnetworks.com/kafka/1.0.0/kafka_2.11-1.0.0.tgz
    tar zxf kafka_2.11-1.0.0.tgz
    
  심볼릭 링크 설정
    ln -s kafka_2.11-1.0.0 kafka
    
  저장 디렉토리 준비
    컨슈머가 메세지를 가져가더라도 저장된 데이터를 임시로 보관
    디렉토리를 하나만 구성하거나 여러 디렉토리로 구성 가능
    디스크가 여러개인 서버는 디스크 수만큼 디렉토리 만들어야 디스크별로 IO 분산 가능
    
  mkdir kafka-data1
  mkdir kafka-data2
  
  카프카 브로커 서버들과 주키퍼 서버와 통신 가능 유무 확인
    nc -v IP주소 Port 번호
    // Connected to 1.1.1.1:2181이 뜨면 성공
    // 포트가 막혀있으면 포트 설정
    
  환경 설정
    vi /usr/local/kafka/config/server.properties
    broker.id=1
    log.dirs=/usr/local/kafka-data1, /usr/local/kafka-data2
    // zookeeper.connect={호스트1:2181, 호스트2:2181, 호스트3:2181}/zzsza-kafka
    // 여기선 단일로 진행할거라 설정 그대로 사용
    zookeeper.connect=localhost:2181/zzsza-kafka
    
  카프카 실행
    /usr/local/kafka/bin/kafka-server-start.sh /usr/local/kafka/config/server.properties
    
  systemd service 파일 작성
    vi /etc/systemd/system/kafka-server.service
    
  [Unit]
  Description=kafka-server
  After=network.target

  [Service]
  Type=simple
  User=root
  Group=root
  SyslogIdentifier=kafka-server
  WorkingDirectory=/usr/local/kafka
  Restart=no
  RestartSec=0s
  ExecStart=/usr/local/kafka/bin/kafka-server-start.sh /usr/local/kafka/config/server.properties
  ExecStop=/usr/local/kafka/bin/kafka-server-stop.sh
  
  systemd service 등록 및 실행
    systemctl daemon-reload
    systemctl enable kafka-server.service
    systemctl start kafka-server.service
    // 중지는
    // systemctl stop kafka-server.service
    // 상태 확인은
    // systemctl status kafka-server.service
    
  카프카 상태 확인
  TCP 포트 확인
  주키퍼
    netstat -ntlp | grep 2181
    >>> tcp6 0 0 :::2181 :::* LISTEN  1954/java
    
  카프카
    netstat -ntlp | grep 9092
    >>> tcp6 0 0 :::9092 :::* LISTEN  2205/java
    
zookeeper znode를 이용한 카프카 정보 확인
    /usr/local/zookeeper/bin/zkCli.sh
    ls /
    >>> [zookeeper, zzsza-kafka]
    ls /zzsza-kafka/brokers/ids
    >>> [1]
    
카프카 로그 확인
  cat /usr/local/kafka/logs/server.log
  
카프카 Topic 생성
  /usr/local/kafka/bin/kafka-topics.sh --zookeeper localhost:2181/zzsza-kafka --replication-factor 1 --partitions 1 --topic hi-topic --create
  
메세지 생성
  /usr/local/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic hi-topic
  > This is a message
  > This is anotehr message
  // ctrl + C
  
메세지 가져오기
  /usr/local/kafka/bin/kafka-console-consumer.sh --bootstrap-server localhost:

[별건참고]
Topic 생성
  kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic [토픽명]

Topic 조회
  kafka-topics.sh --list --zookeeper localhost

Consumer 실행
  kafka-console-consumer.sh --bootstrap-server localhost:9092 --from-beginning --topic  [토픽명]

Production 실행
  kafka-topics.sh --create  --zookeeper localhost:2181 --replication-factor 1   --partitions 1 --topic [토픽명]




레빗엠큐와 카프카를 비교합니다.
1. 분산 및 병렬 처리
레빗엠큐는 다 수의 소비자로 스케일 아웃할 수 있고 각각의 큐가 복수의 소비자를 가진다는 뜻입니다. 이에, 메시지를 소비하기 위해 서로 경쟁하기 때문에 경쟁 소비자라 불리며, 모든 활성 상태의 
소비자에게 진행중인 업무가 분산되어 처리되지만 여전히 메시지 소비는 단 한번 처리될 뿐입니다. 
카프카에서는 파편화된 토픽에 소비자를 분산하는 식으로 처리하며, 소비자 그룹에서 각 소비자는 하나의 파티션을 전담하게 됩니다. 
각각의 파티션에 다른 세트의 메시지(사용자 아이디, 위치 등)를 비즈니스 키로 전송할 수 있습니다.
2. 고가용성
레빗엠큐와 카프카 모두 고가용성 솔루션입니다. 카프카의 경우 주키퍼를 사용하여 클러스터 상태를 관리하고 클러스터의 리더 또한 고가용성이며 분산 시스템이 될 수 있어 좀 더 고가용성을 제공한다고 할 수 있습니다.
 3. 성능
카프카는 순차 디스크 I/O의 강점을 최대한 활용하며 하드웨어는 적게 사용하는 데 이런 것들이 높은 처리량을 만들 수 있습니다. 적은 수의 노드로 초당 몇 만건의 메시지를 처리할 수 있습니다.
레빗엠큐도 초당 몇 만건의 메시지를 처리할 수 있으나 30개 이상의 노드가 필요하게 됩니다.
4. 복제
카프카는 설계상으로 브로커를 복제해 왔으며, 마스터 브로커가 죽으면 자동적으로 모든 작업이 다른 브로커로 전달되며 이 브로커는 메시지 분실 없이 죽은 브로커의 완전한 복제본이 됩니다.
5. 복수의 구독자
카프카의 메시지는 다양한 형태의 복 수의 소비자들에 의해 구독될 수 있습니다. 레빗엠큐에서는 한번 소비되고 나면 큐에서 메시지가 사라지고 더 이상 접근이 불가하게 됩니다.
6. 메시지 순서
카프카는 파티션들을 가지고 있기 때문에 메시지간 순서를 줄 수 있습니다. 레빗엠큐에서는 어려운 부분입니다.
7. 메시지 프로토콜
레빗엠큐는 AMQP,HTTP, MQTT, STOMP 등의 표준 프로토콜을 지원하나 카프카는 기본형 (int8, int16, int32, int64, string, arrays)과 바이너리 메시지를 지원합니다.
8. 메시지 생명주기
카프카는 메시지가 항상 로그 형태로 보존되며, 보존기간은 정책에 따라 조정될 수 있습니다. 
레빗엠큐는 큐로 메시지를 관리하며, 승인 메시지가 도착하면 관련 메시지는 소비되어 큐에서 사라지게 된다.설정으로 큐를 오래 남도록 하고 메시지가 영속하도록 만들 수 있습니다.
10. 토픽/큐로의 유연한 라우팅
카프카는 메시지를 전송할 토픽을 키로 지정해야 하나 레빗엠큐는 와일드카드나 정규표현식으로 전달되어야 할 목적지인 토픽이나 큐를 라우팅할 수 있습니다.
11. 메시지 우선순위
레빗엠큐는 메시지에 우선순위를 매겨 선처리하도록 지정할 수 있으나 카프카는 힘듭니다.


결론
레빗엠큐는 적은 데이타 트래픽의 간단한 유스케이스에 적합하고 우선순위 큐와 유연한 라우팅 옵션같은 확실한 이점을 가지고 있습니다. 
하지만 고용량 데이타 및 높은 처리량이 요구될 때는 논쟁의 여지 없이 카프카를 사용합니다.
만약에 동일한 메시지에 대해 복수개의 소비자가
필요하거나 로그를 남겨야 한다면 레빗엠큐는 지원하지 않으니 카프카를 사용하도록 합니다.



annotation을 이용한 kafka로 로그 전송 기능 구현

KafkaAppender를 이용한 로그 전송
https://gyrfalcon.tistory.com/entry/Kafka-Kafka%EB%A1%9C-Log%EB%A5%BC-%EB%B3%B4%EB%82%B4%EB%8A%94-%EB%B0%A9%EB%B2%95%EB%93%A4

kafka로 전송하기 위한 Appender
logback의 Appender를 확장하여 사용하기 때문에 일반적인 logback의 사용방법과 동일


사용방법
dependecy 추가

<dependency>
    <groupId>com.github.danielwegener</groupId>
    <artifactId>logback-kafka-appender</artifactId>
    <version>0.1.0</version>
</dependency>
 
<dependency>
    <groupId>net.logstash.logback</groupId>
    <artifactId>logstash-logback-encoder</artifactId>
    <version>4.8</version>
</dependency>

logback-kafka-appender : log를 kafka로 전달할 때 사용

logstash-logback-encoder : logstash 레이아웃으로 메시지 생성
logback.xml에 appender 선언

<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</Pattern>
        </layout>
    </appender>
 
    <!-- kafkaAppender -->
    <appender name="kafkaAppender" class="com.github.danielwegener.logback.kafka.KafkaAppender">
        <encoder class="com.github.danielwegener.logback.kafka.encoding.LayoutKafkaMessageEncoder">
            <layout class="ch.qos.logback.classic.PatternLayout">
                <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            </layout>
        </encoder>
        <topic>test-logback</topic>
        <producerConfig>bootstrap.servers=localhost:9092</producerConfig>
    </appender>
 
    <!-- kafkaAppender with Logstash -->
    <appender name="logstashKafkaAppender" class="com.github.danielwegener.logback.kafka.KafkaAppender">
        <encoder class="com.github.danielwegener.logback.kafka.encoding.PatternLayoutKafkaMessageEncoder">
            <layout class="net.logstash.logback.layout.LogstashLayout" />
        </encoder>
        <topic>test-logback</topic>
        <producerConfig>bootstrap.servers=localhost:9092</producerConfig>
    </appender>
 
 
    <!-- logger -->
    <logger name="org.apache.kafka" level="ERROR"/>
    <logger name="com.minsub.java.logger.kafka" level="DEBUG">
        <appender-ref ref="kafkaAppender" />
        <!--
        <appender-ref ref="logstashKafkaAppender" />
        -->
    </logger>
 
    <root level="DEBUG">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>


결론
장점
기존의 logback과 사용방법이 다르지 않아 쉽게 적용
단점
Log Level에 따라 전송여부가 결정됨
logger name=com.minsub.java.logger.kafka인 경우
해당 패키지의 DEBUG 이상인 로그만 KafkaAppender를 사용하여 kafka로 전송
로그 레벨과 상관없이 전송하지 못함.
결국 logging framework와는 별도로 전송 모듈을 만들어야 함!




package com.lotteon.ec.core.bus.kafka.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface KafkaSenderReturn {
  String type() default "";
  String tag() default "";
  String tagAction() default "";
  String action() default "";
  String topic() default "";
}



package com.lotteon.ec.core.bus.kafka.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
public @interface KafkaSenderParameter {
  String type() default "";
  String tag() default "";
  String tagAction() default "";
  String action() default "";
  String topic() default "";
}



package com.lotteon.ec.core.bus.kafka;

import com.lotteon.ec.core.bus.kafka.annotation.KafkaSenderParameter;
import com.lotteon.ec.core.bus.kafka.annotation.KafkaSenderReturn;
import com.lotteon.ec.core.bus.message.MessageBaseModel;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class KafkaMessageAspect
{
  private static final Logger log = LoggerFactory.getLogger(KafkaMessageAspect.class);
  
  @Autowired
  KafkaUtils kafkaUtils;
  
  @Before("@annotation(com.lotteon.ec.core.bus.kafka.annotation.KafkaSenderParameter) && @ annotation(kafkaSenderParameter)")
  public void kafkaAspectParameter(JoinPoint pjp, KafkaSenderParameter kafkaSenderParameter) throws Throwable {
    try {
      if (pjp.getArgs().length == 0)
        throw new KafkaMessageException("Message Check : Parameter is length zeno"); 
      if (pjp.getArgs()[0] instanceof com.lotteon.ec.core.bus.message.MessageBase) {
        MessageBaseModel messageBaseModel = (MessageBaseModel)pjp.getArgs()[0];
        
        String type = kafkaSenderParameter.type();
        String topic = kafkaSenderParameter.topic();
        String tag = kafkaSenderParameter.tag();
        String tagAction = kafkaSenderParameter.tagAction();
        String action = kafkaSenderParameter.action();        
        this.kafkaUtils.kafkaSender(messageBaseModel, type, topic, tag, tagAction, action);

      } else {
        throw new KafkaMessageException("Message Check : Massege type -> Message Parameter 0");

      }
    } catch (KafkaMessageException ex) {
      log.error(ex.toString());
    } catch (Exception ex) {
      log.error(ex.toString());
    } 
  }
  
  @AfterReturning(value = "@annotation(com.lotteon.ec.core.bus.kafka.annotation.KafkaSenderReturn) && @ annotation(kafkaSenderReturn)", returning = "resultMessage")
  public void kafkaAspectReturn(JoinPoint pjp, KafkaSenderReturn kafkaSenderReturn, Object resultMessage) throws Throwable {
    try {
      if (resultMessage instanceof com.lotteon.ec.core.bus.message.MessageBase) {
        MessageBaseModel messageBaseModel = (MessageBaseModel)resultMessage;     
        String type = kafkaSenderReturn.type();
        String topic = kafkaSenderReturn.topic();
        String tag = kafkaSenderReturn.tag();
        String tagAction = kafkaSenderReturn.tagAction();
        String action = kafkaSenderReturn.action();
        this.kafkaUtils.kafkaSender(messageBaseModel, type, topic, tag, tagAction, action);

      } else {
        throw new KafkaMessageException("Message Check : Massege type -> Message return type");

      }
    } catch (KafkaMessageException ex) {
      log.error(ex.toString());
    } catch (Exception ex) {
      log.error(ex.toString());
    } 
  }
}



package com.lotteon.ec.core.bus.kafka;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;

@EnableKafka
@Configuration
public class KafkaConsumerConfig<T>
  extends Object {
  private static final Logger log = LoggerFactory.getLogger(KafkaConsumerConfig.class);
  
  @Value("${spring.kafka.bootstrapAddress:10.118.245.53:9092}")
  private String bootstrapAddress;
  
  @Value("${spring.kafka.groupId:lotteon}")
  private String groupId;
  
  @Bean
  @Profile({"kafka"})
  public ConsumerFactory<String, String> consumerFactory() { 
    return new DefaultKafkaConsumerFactory(consumerConfigs()); 
  }
  
  @Bean
  @Profile({"kafka"})
  KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
    factory.setConsumerFactory(consumerFactory());
    factory.setConcurrency(Integer.valueOf(3));
    factory.getContainerProperties().setPollTimeout(3000L);
    factory.setBatchListener(Boolean.valueOf(true));
    return factory;
  }
  
  @Bean
  @Profile({"kafka"})
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> propsMap = new HashMap<String, Object>();
    propsMap.put("bootstrap.servers", this.bootstrapAddress);
    propsMap.put("group.id", this.groupId);  
    propsMap.put("key.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class);
    propsMap.put("value.deserializer", org.apache.kafka.common.serialization.StringDeserializer.class);
    propsMap.put("max.poll.records", "50");
    return propsMap;
  }
}



package com.lotteon.ec.core.bus.kafka;

public class KafkaMessageException
  extends RuntimeException {
  private static final long serialVersionUID = -1L;  
  public KafkaMessageException(String message) { super(message); }
}




package com.lotteon.ec.core.bus.kafka;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig<T>
  extends Object {
  private static final Logger log = LoggerFactory.getLogger(KafkaProducerConfig.class);
  
  @Value("${spring.kafka.bootstrapAddress:10.118.245.53:9092}")
  private String bootstrapAddress;
  
  @Bean
  @Profile({"kafka"})
  public ProducerFactory<String, T> producerFactory() {
    Map<String, Object> configProps = new HashMap<String, Object>();
    configProps.put("bootstrap.servers", this.bootstrapAddress);
    configProps.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
    configProps.put("value.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
    return new DefaultKafkaProducerFactory(configProps);
  }
  
  @Bean
  @Profile({"kafka"})
  public KafkaTemplate<String, T> kafkaTemplate() { return new KafkaTemplate(producerFactory()); }
}




package com.lotteon.ec.core.bus.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaAdmin;

public class KafkaTopicConfig
{
  private static final Logger log = LoggerFactory.getLogger(KafkaTopicConfig.class);
  
  @Value("${spring.kafka.bootstrapAddress:10.118.245.53:9092}")
  private String bootstrapAddress;
  
  @Value("${spring.kafka.topicName:lotteon}")
  private String topic;
  
  @Bean
  @Profile({"kafka"})
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<String, Object>();
    configs.put("bootstrap.servers", this.bootstrapAddress);
    return new KafkaAdmin(configs);
  }
  
  @Bean
  @Profile({"kafka"})
  public NewTopic topic() { return new NewTopic(this.topic, 1, (short)1); }
}



package com.lotteon.ec.core.bus.kafka;

import com.lotteon.ec.core.bus.message.MessageBaseModel;
import com.lotteon.ec.core.info.ServiceInfo;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class KafkaUtils
{
  private static final Logger log = LoggerFactory.getLogger(KafkaUtils.class);
  
  @Autowired
  ServiceInfo serviceInfo;

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  public void kafkaSender(MessageBaseModel messageBaseModel, String type, String topic, String tag, String tagAction, String action) throws Throwable {
    try {
      if (topic.equals("")) throw new KafkaMessageException("Kafka topic is needed"); 
      if (tag.equals("")) throw new KafkaMessageException("Kafka tag is needed");
      String traceid = messageBaseModel.getTraceId();
      if (traceid.equals("")) traceid = UUID.randomUUID().toString();
      String messageBody = messageBaseModel.getMessage();
      if (type.equals("KAFKA_SENDER")) {
        Message<String> sendMessage = MessageBuilder.withPayload(messageBody).setHeader("kafka_topic", topic).setHeader("kafka_messageKey", "1000").setHeader("kafka_partitionId", Integer.valueOf(0)).setHeader("X-Trace-Id", traceid).setHeader("X-Tag", tag).setHeader("X-Tag-Action", tagAction).setHeader("X-Action", action).setHeader("X-Service-Src", this.serviceInfo.getServiceName()).build();
        this.kafkaTemplate.send(sendMessage);
      } 
    } catch (KafkaMessageException ex) {
      log.error("KafkaMessageException" + ex.toString());
    } catch (Exception ex) {
      log.error("Exception" + ex.toString());
    } 
  }
}








