---
layout: single
title: "[tech] Spring Boot Kafka Template 사용 방법"
excerpt: "kafka"

categories:
  - tech
tags:
  - [kafka]

toc: false
toc_sticky: true

date: 2023-02-03
last_modified_at: 2023-02-03
---

# 1. Sample 1

### Spring Boot Kafka Template 사용 방법 - Kafka Header & Message
- Spring Boot Kafka Template 을 이용 Kafka 에 Multi Topic 을 Produce(=Send) 하다고 Consume(=Receive) 하는 방법.

- 참조 : https://github.com/jjeaby/kafkaSample.
- Spring 버전에 맞는 kafka 라이브러리의 버전.

| Spring for Apache Kafka Version | Spring Integration for Apache Kafka Version | kafka-clients       | Spring Boot                           |
|---------------------------------|---------------------------------------------|---------------------|---------------------------------------|
| 2.6.0                           | 5.3.x or 5.4.0-SNAPSHOT (pre-release)       | 2.6.0               | 2.3.x or 2.4.0-SNAPSHOT (pre-release) |
| 2.5.x                           | 3.3.x                                       | 2.5.0               | 2.3.x                                 |
| 2.4.x                           | 3.2.x                                       | 2.4.1               | 2.2.x                                 |
| 2.3.x                           | 3.2.x                                       | 2.3.1               | 2.2.x                                 |
| 2.2.x                           | 3.1.x                                       | 2.0.1, 2.1.x, 2.2.x | 2.1.x                                 |
| 2.1.x                           | 3.0.x                                       | 1.0.2               | 2.0.x (End of Life)                   |
| 1.3.x                           | 2.3.x                                       | 0.11.0.x, 1.0.x     | 1.5.x (End of Life)                   |

## 1-1. Kafka 라이브러리 설정

- pom.xml 

```xml
<!-- KAFKA -->
  <dependency>
     <groupId>org.apache.kafka</groupId>
     <artifactId>kafka-clients</artifactId>
     <version>2.1.1</version>
  </dependency>
  <dependency>
     <groupId>org.apache.kafka</groupId>
     <artifactId>kafka-streams</artifactId>
     <version>2.1.1</version>
  </dependency>
  <dependency>
     <groupId>org.apache.kafka</groupId>
     <artifactId>kafka_2.12</artifactId>
     <version>2.1.1</version>
  </dependency>
  <dependency>
     <groupId>org.springframework.kafka</groupId>
     <artifactId>spring-kafka</artifactId>
     <version>2.2.12.RELEASE</version>
  </dependency>
```

## 1-2. Topic 정보 등록

- 일반적으로 사용할 API 별로 호출 하는 Produce(=Send), Consume(=Recv) 생성, API 가 10 개 라면 Topic 은 20개.
- topic 은 호출하는 from(시스템 이름)-to(시스템 이름)-topic 으로 정의.
- group-id 와 topic-id 가 동일하면 Consume(=Recv) 가 되지 않으니 다르게 설정해야 함.
- Topic 을 Produce 할때 Kafka Header 에 Message Key 를 넣어 이를 이용 API 호출 가능

```bash
    ### application.properties

    # kafka 서버 주소
    spring.kafka.bootstrap-servers=127.0.0.1:9092
    # consumer 에서 사용하는 group id
    spring.kafka.consumer.medium-sendid-group-id=medium-sendid-group
    spring.kafka.consumer.company-sendid-group-id=company-sendid-group
    # 사용하는 topic
    spring.kafka.template.top-a-topic=medium-sendid-topic
    spring.kafka.template.top-b-topic=company-sendid-topic

    # kafka 에서 메세지를 받고 자동으로 ACK 를 전송 여부 설정(true = 자동으로, false = 별도로 코드 구성 필요)
    spring.kafka.consumer.enable-auto-commit=true
    # kafka 에서 메세지를 가져오는 consumer의 offset정보가 존재하지 않는 경우의 처리 방법(- latest : 가장 마지막 offset부터, earliest : 가장 처음 offset부터, none : offset 없다면 에러 발생
    spring.kafka.consumer.auto-offset-reset=latest
    spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
    spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
    spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
    spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
    spring.kafka.consumer.max-poll-records=1000
```

## 1-3. 사용 Class 작성

- 이하 4개 클래스 작성
- KafkaProducerConfig : Kafaka Topic(=Message) 를 Produce(=Send) 하는 설정 클래스
- KafkaProducer : Kafaka Topic(=Message) 를 Produce(=Send) 구현체
- KafkaConsumerConfig : Kafaka Topic(=Message) 를 Consume(=Recv) 하는 설정 클래스
- KafkaConsumer : Kafaka Topic(=Message) 를 Consume(=Recv) 하는 구현체

### 1-3-1. kafkaProducerConfig
- kafkaProducerConfig 클래스를 만들고 kafka 접속 주소, Serializer, Deserializer 설정.

```java
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.kafka.consumer.value-deserializer}")
    private String keyDeSerializer;
    @Value("${spring.kafka.consumer.value-deserializer}")
    private String valueDeSerializer;
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String offsetReset;
    @Value("${spring.kafka.consumer.max-poll-records}")
    private String maxPollRecords;
    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private String enableAutoCommit;

    @Bean
    public ConsumerFactory<String, String> topicAConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeSerializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeSerializer);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = "topicAKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String>
    topicAKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(topicAConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> topicBConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeSerializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeSerializer);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = "topicBKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String>
    topicBKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(topicBConsumerFactory());
        return factory;
    }
}
```

### 1-3-2. KafkaConsumer
- KafkaProducer 를 생성하고 Kafka 로 Topic 을 Produce(=Send) 
    - kafkaTemplate.send 를 호출 간단하게 구현.
- setHeader(KafkaHeaders.MESSAGE_KEY, messageKey) 코드를 이용 kafka Header 에 Message Key 를 설정
    - 해당방법 이용 Header 에 Custom Key 설정가능

```java
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    @KafkaListener(topics = "${spring.kafka.template.medium-sendid-topic}", containerFactory = "topicAKafkaListenerContainerFactory", groupId = "${spring.kafka.consumer.medium-sendid-group-id}")
    public void listentopicATopic(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String messageKey) throws Exception {
        log.info("Topic: [medium-sendid-topic] messageKey Message: [" + messageKey + "]");
        log.info("Topic: [medium-sendid-topic] Received Message: [" + message + "] from partition: [" + partition + "]");
    }

    @KafkaListener(topics = "${spring.kafka.template.company-sendid-topic}", containerFactory = "topicBKafkaListenerContainerFactory", groupId = "${spring.kafka.consumer.company-sendid-group-id}")
    public void listentopicBTopic(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String messageKey) throws Exception {
        log.info("Topic: [company-sendid-topic] messageKey Message: [" + messageKey + "]");
        log.info("Topic: [company-sendid-topic] Received Message: [" + message + "] from partition: [" + partition + "]");
    }
}
```

### 1-3-3. KafkaConsumerConfig
- Topic 을 Consume(=Recv) .
- kafkaConsumerConfig 클래스를 만들고 kafka 접속 주소, Serializer, Deserializer, offsetReset 등 설정.

- Multi Topic 을 지원을 위해 ConsumerFactory, ConcurrentKafkaListenerContainerFactory 를 지원하는 Muti Topic 의 갯수와 동일하게 생성필요. 여기서는 top-a-topic, top-b-topic 을 사용 하므로 각 Topic 마다 ConsumerFactory, ConcurrentKafkaListenerContainerFactor  생성.


```java
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableKafka
public class KafkaConsumerConfig {
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServer;
    @Value("${spring.kafka.consumer.value-deserializer}")
    private String keyDeSerializer;
    @Value("${spring.kafka.consumer.value-deserializer}")
    private String valueDeSerializer;
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String offsetReset;
    @Value("${spring.kafka.consumer.max-poll-records}")
    private String maxPollRecords;
    @Value("${spring.kafka.consumer.enable-auto-commit}")
    private String enableAutoCommit;

    @Bean
    public ConsumerFactory<String, String> topicAConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeSerializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeSerializer);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = "topicAKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String>
    topicAKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(topicAConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> topicBConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeSerializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeSerializer);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = "topicBKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String>
    topicBKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(topicBConsumerFactory());
        return factory;
    }
}
```

### 1-3-4. KafkaListener
- Topic 을 Consum(=Recv) 하는 KafkaListener 작성. 
- KafkaListener 는 @kafkaListner 어노테이션을 이용하여 작성. 
- @kafkaListner Annotation으로 자동으로 Kafka 에서 Topic 을 Polling 하므로 별도 코드/설정 필요없음.
- setHeader(KafkaHeaders.MESSAGE_KEY, messageKey) 로 Produce 에 설정한 kafka Header 에 Message Key 를 @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String messageKey 를 수신

```java
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.kafka.annotation.KafkaListener;
  import org.springframework.kafka.support.KafkaHeaders;
  import org.springframework.messaging.handler.annotation.Header;
  import org.springframework.messaging.handler.annotation.Payload;
  import org.springframework.stereotype.Component;

  @Slf4j
  @Component
  public class KafkaConsumer {

    @KafkaListener(topics = "${spring.kafka.template.top-a-topic}", containerFactory = "topicAKafkaListenerContainerFactory", groupId = "${spring.kafka.consumer.top-a-group-id}")
    public void listentopicATopic(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String messageKey) throws Exception {
        log.info("Topic: [top-a-topic] messageKey Message: [" + messageKey + "]");
        log.info("Topic: [top-a-topic] Received Message: [" + message + "] from partition: [" + partition + "]");
    }

    @KafkaListener(topics = "${spring.kafka.template.top-b-topic}", containerFactory = "topicBKafkaListenerContainerFactory", groupId = "${spring.kafka.consumer.top-b-group-id}")
    public void listentopicBTopic(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String messageKey) throws Exception {
        log.info("Topic: [top-b-topic] messageKey Message: [" + messageKey + "]");
        log.info("Topic: [top-b-topic] Received Message: [" + message + "] from partition: [" + partition + "]");
    }
  }
```

### 1-3-5. 테스트 코드 작성
- Topic 을 Producer(=Send) 하여 Consume(=Recv) 를 테스트 하기 위해 GET /send Rest API 에서 kafkaProducer.sendMessage 를 호출하도록 추가

```java
  @Autowired
  KafkaProducer kafkaProducer;

  @RequestMapping(method = RequestMethod.GET, path = "/send")
  String send() {
    kafkaProducer.sendMessage("medium-sendid-topic", "message key medium", "medium -> sendid message");
    kafkaProducer.sendMessage("company-sendid-topic", "message key company", "company -> sendid message");
    return "Kafka Produce!!!";
  }
```

- 서버가 실행, localhost:8080/send 를 호출.
- "medium-sendid-topic", "company-sendid-topic” Topic 이 Produce(=Send) 되고 Consume(=Recv) 서버 로그 확인.
  
  
---
---
---
  

# 2. Sample 2

## 2-1. pom.xml 에 의존성 등록

```xml

  <!-- KAFKA -->
  <parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.1.6.RELEASE</version>
  </parent>
  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.build.outputEncoding>UTF-8</project.build.outputEncoding>
    <java.version>1.8</java.version>
  </properties>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter</artifactId>
      <version>2.2.5.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>org.springframework.kafka</groupId>
      <artifactId>spring-kafka</artifactId>
      <version>2.2.7.RELEASE</version>
    </dependency>
    <dependency>
      <groupId>com.fasterxml.jackson.core</groupId>
      <artifactId>jackson-databind</artifactId>
    </dependency>
  </dependencies>

​```

## 2-2. application.properties 설정 추가

- kafka의 설정 파일을 application.properties 파일에 추가.

```bash
  kafka.bootstrapAddress=localhost:9092
  message.topic.name=mytopic
  greeting.topic.name=greeting
  filtered.topic.name=filtered
  partitioned.topic.name=partitioned
```

## 2-3. KafkaTopicConfig 생성

- Topic 설정
​
```java
  import org.apache.kafka.clients.admin.AdminClientConfig;
  import org.apache.kafka.clients.admin.NewTopic;
  import org.apache.kafka.common.internals.Topic;
  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.kafka.core.KafkaAdmin;
  import java.util.HashMap;
  import java.util.Map;

  @Configuration
  public class KafkaTopicConfig {
    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value(value = "${message.topic.name}")
    private String topicName;

    @Value(value = "${partitioned.topic.name}")
    private String partionedTopicName;

    @Value(value = "${filtered.topic.name}")
    private String filteredTopicName;

    @Value(value = "${greeting.topic.name}")
    private String greetingTopicName;

    @Bean
    public KafkaAdmin kafkaAdmin() {
      Map<String, Object> configs = new HashMap<>();
      configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
      return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic1() {
      return new NewTopic(topicName,1,(short)1);
    }

    @Bean
    public NewTopic topic2() {
      return new NewTopic(partionedTopicName, 6, (short) 1);
    }

    @Bean
    public NewTopic topic3() {
      return new NewTopic(filteredTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic topic4() {
      return new NewTopic(greetingTopicName, 1, (short) 1);
    }
  }
​```

- KafkaAdmin 타입의 생성자를 통해, Kafka 설정정보도 주입 가능.
- Kafka-Spring 에서는 위의 코드를 통해서, 코드를 이용해서 프로그래밍 적으로 메시지 큐의 토픽 생성가능.
- 스프링 부트(Kafka-Spring)에서는, 위와 같이 토픽을 생성해주는 함수를 만들고 Bean으로 등록해주면 자동으로 토픽을 생성해서 주입.

​
​## 2-4. KafkaTopicConfig

- 실제로 메시지를 발행하는 Producer에 관한 설정.

```java
  import com.example.kafkaexample.Greeting;
  import org.apache.kafka.clients.producer.ProducerConfig;
  import org.apache.kafka.common.serialization.StringSerializer;
  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.kafka.core.DefaultKafkaProducerFactory;
  import org.springframework.kafka.core.KafkaTemplate;
  import org.springframework.kafka.core.ProducerFactory;
  import org.springframework.kafka.support.serializer.JsonSerializer;
  import java.util.HashMap;
  import java.util.Map;

  @Configuration
  public class KafkaProducerConfig {
    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
      Map<String, Object> configProps = new HashMap<>();
      configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
      configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
      configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
      return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
      return new KafkaTemplate<String, String>(producerFactory());
    }

    public ProducerFactory<String, Greeting> greetingProducerFactory() {
      Map<String, Object> configProps = new HashMap<>();
      configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
      configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
      configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
      return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Greeting> greetingKafkaTemplate() {
      return new KafkaTemplate<>(greetingProducerFactory());
    }
  }
```
​
- ProducerFactory 객체를 이용, 각 메시지 종류별로 메시지를 어디에 보내고, 어떠한 방식으로 처리할것인지 설정.
- 실제 메시지는 KafkaTemplate 이라는 객체에 담겨서 전송.
​- Socket 프로그래밍에 비유하면 Producer 객체는 Socket 디스크립터고, ProducerFactory는 Socket 디스크립터를 만들어주는 팩토리 메서드.
- 위의 예제에서는, 2가지 종류의 메시지 정의.

​## 2-5. KafkaConsumerConfig

- 실제로 메시지를 가져오는 부분.

​```java
  import com.example.kafkaexample.Greeting;
  import org.springframework.kafka.support.serializer.JsonDeserializer;
  import org.apache.kafka.clients.consumer.ConsumerConfig;
  import org.apache.kafka.common.serialization.StringDeserializer;
  import org.springframework.beans.factory.annotation.Value;
  import org.springframework.context.annotation.Bean;
  import org.springframework.context.annotation.Configuration;
  import org.springframework.kafka.annotation.EnableKafka;
  import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
  import org.springframework.kafka.core.ConsumerFactory;
  import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
  import java.util.HashMap;
  import java.util.Map;

  @EnableKafka
  @Configuration
  public class KafkaConsumerConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    public ConsumerFactory<String, String> consumerFactory(String groupId){
      Map<String, Object> props = new HashMap<>();
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
      props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
      props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
      props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
      return new DefaultKafkaConsumerFactory<>(props);
    }

    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(String groupId) {
      ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
      factory.setConsumerFactory(consumerFactory(groupId));
      return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> fooKafkaListenerContainerFactory() {
      return kafkaListenerContainerFactory("foo");
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> barKafkaListenerContainerFactory() {
      return kafkaListenerContainerFactory("bar");
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> headersKafkaListenerContainerFactory() {
      return kafkaListenerContainerFactory("headers");
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> partitionsKafkaListenerContainerFactory() {
      return kafkaListenerContainerFactory("partitions");
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,String> filterKafkaListenerContainerFactory() {
      ConcurrentKafkaListenerContainerFactory<String, String> factory = kafkaListenerContainerFactory("filter");
      factory.setRecordFilterStrategy(record -> record.value().contains("world"));
      return factory;
    }


    public ConsumerFactory<String, Greeting> greetingConsumerFactory() {
      Map<String, Object> props = new HashMap<>();
      props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
      props.put(ConsumerConfig.GROUP_ID_CONFIG, "greeting");
      return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), new JsonDeserializer<>(Greeting.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Greeting> greetingKafkaListenerContainerFactory() {
      ConcurrentKafkaListenerContainerFactory<String, Greeting> factory = new ConcurrentKafkaListenerContainerFactory<String, Greeting>();
      factory.setConsumerFactory(greetingConsumerFactory());
      return factory;
    }
  }
```​

- ConsumerFactory 를 이용, 각 메시지 종류별로 메시지를 어디에서 받고, 어떠한 방식으로 처리할것인지 설정.
- 설정한 각 Topic 별로 메시지를 어디서/어떻게 받을지를 설정.

​마지막으로, 위에서 작성한 설정을 기반으로 아래와 같이 메시지 큐에 데이터를 넣고 빼보도록 합시다.

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class KafkaExampleApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(KafkaExampleApplication.class, args);

        MessageProducer producer = context.getBean(MessageProducer.class);
        MessageListener listener = context.getBean(MessageListener.class);

        producer.sendMessage("Hello, World!");
        listener.latch.await(10, TimeUnit.SECONDS);


        for (int i = 0; i < 5; i++) {
            producer.sendMessageToPartion("Hello To Partioned Topic!", i);
        }
        listener.partitionLatch.await(10, TimeUnit.SECONDS);


        producer.sendMessageToFiltered("Hello Baeldung!");
        producer.sendMessageToFiltered("Hello World!");
        listener.filterLatch.await(10, TimeUnit.SECONDS);


        producer.sendGreetingMessage(new Greeting("Greetings", "World!"));
        listener.greetingLatch.await(10, TimeUnit.SECONDS);

        context.close();
    }

    @Bean
    public MessageProducer messageProducer() {
        return new MessageProducer();
    }

    @Bean
    public MessageListener messageListener() {
        return new MessageListener();
    }

    public static class MessageProducer {

        @Autowired
        private KafkaTemplate<String, String> kafkaTemplate;

        @Autowired
        private KafkaTemplate<String, Greeting> greetingKafkaTemplate;

        @Value(value = "${message.topic.name}")
        private String topicName;

        @Value(value = "${partitioned.topic.name}")
        private String partionedTopicName;

        @Value(value = "${filtered.topic.name}")
        private String filteredTopicName;

        @Value(value = "${greeting.topic.name}")
        private String greetingTopicName;

        public void sendMessage(String message) {

            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);

            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

                @Override
                public void onSuccess(SendResult<String, String> result) {
                    System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata()
                            .offset() + "]");
                }

                @Override
                public void onFailure(Throwable ex) {
                    System.out.println("Unable to send message=[" + message + "] due to : " + ex.getMessage());
                }
            });
        }

        public void sendMessageToPartion(String message, int partition) {
            kafkaTemplate.send(partionedTopicName, partition, null, message);
        }

        public void sendMessageToFiltered(String message) {
            kafkaTemplate.send(filteredTopicName, message);
        }

        public void sendGreetingMessage(Greeting greeting) {
            greetingKafkaTemplate.send(greetingTopicName, greeting);
        }
    }

    public static class MessageListener {

        private CountDownLatch latch = new CountDownLatch(3);

        private CountDownLatch partitionLatch = new CountDownLatch(2);

        private CountDownLatch filterLatch = new CountDownLatch(2);

        private CountDownLatch greetingLatch = new CountDownLatch(1);

        @KafkaListener(topics = "${message.topic.name}", groupId = "foo", containerFactory = "fooKafkaListenerContainerFactory")
        public void listenGroupFoo(String message) {
            System.out.println("Received Messasge in group 'foo': " + message);
            latch.countDown();
        }

        @KafkaListener(topics = "${message.topic.name}", groupId = "bar", containerFactory = "barKafkaListenerContainerFactory")
        public void listenGroupBar(String message) {
            System.out.println("Received Messasge in group 'bar': " + message);
            latch.countDown();
        }

        @KafkaListener(topics = "${message.topic.name}", containerFactory = "headersKafkaListenerContainerFactory")
        public void listenWithHeaders(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
            System.out.println("Received Messasge: " + message + " from partition: " + partition);
            latch.countDown();
        }

        @KafkaListener(topicPartitions = @TopicPartition(topic = "${partitioned.topic.name}", partitions = { "0", "3" }), containerFactory = "partitionsKafkaListenerContainerFactory")
        public void listenToParition(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
            System.out.println("Received Message: " + message + " from partition: " + partition);
            this.partitionLatch.countDown();
        }

        @KafkaListener(topics = "${filtered.topic.name}", containerFactory = "filterKafkaListenerContainerFactory")
        public void listenWithFilter(String message) {
            System.out.println("Recieved Message in filtered listener: " + message);
            this.filterLatch.countDown();
        }

        @KafkaListener(topics = "${greeting.topic.name}", containerFactory = "greetingKafkaListenerContainerFactory")
        public void greetingListener(Greeting greeting) {
            System.out.println("Recieved greeting message: " + greeting);
            this.greetingLatch.countDown();
        }

    }
}
```

코드의 양이 많으니 하나씩 뜯어서 보도록 합시다.

​

먼저, 메시지를 생성하는 부분입니다.

​
```java
public static class MessageProducer {

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  private KafkaTemplate<String, Greeting> greetingKafkaTemplate;

  @Value(value = "${message.topic.name}")
  private String topicName;

  @Value(value = "${partitioned.topic.name}")
  private String partitionedTopicName;

  @Value(value = "${filtered.topic.name}")
  private String filteredTopicName;

  @Value(value = "${greeting.topic.name}")
  private String greetingTopicName;

  public void sendMessage(String message) {

    ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);

    future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {

      @Override
      public void onSuccess(SendResult<String, String> result) {
        System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata()
                           .offset() + "]");
      }

      @Override
      public void onFailure(Throwable ex) {
        System.out.println("Unable to send message=[" + message + "] due to : " + ex.getMessage());
      }
    });
  }

  public void sendMessageToPartion(String message, int partition) {
    kafkaTemplate.send(partitionedTopicName, partition, null, message);
  }

  public void sendMessageToFiltered(String message) {
    kafkaTemplate.send(filteredTopicName, message);
  }

  public void sendGreetingMessage(Greeting greeting) {
    greetingKafkaTemplate.send(greetingTopicName, greeting);
  }
}
​```

먼저, 2가지 종류의 KafkaTemplate을 정의하였습니다.

(해당 KafkaTemplate 들은 위에서 만든 KafkaProducerConfig에 있는 빈 객체가 대입되게 됩니다.)

​

다음으로, kafkaTemplate를 이용해서 메시지를 전송합니다. 사실 메시지 큐 방식으로 통신하는 경우 필연적으로 비동기 방식으로 통신하기 때문에(메시지가 언제올지 알 수가 없으므로..) 콜백 함수를 등록하게 됩니다.

​

(여담으로 rabbitMQ도 그렇지만 보통 메시지 큐에서는 저렇게 요청-응답 을 쌍으로 받으려면 보통 수신 큐, 송신 큐 2개를 둬서 통신을 하게 됩니다. 이러한 임시 큐들을 카프카와 같은 메시지 브로커 서비스에서 자동으로 만들어주게 됩니다.)

​

​

​

다음으로, 메시지를 받는 부분입니다.

​
```java
public static class MessageListener {

  private CountDownLatch latch = new CountDownLatch(3);

  private CountDownLatch partitionLatch = new CountDownLatch(2);

  private CountDownLatch filterLatch = new CountDownLatch(2);

  private CountDownLatch greetingLatch = new CountDownLatch(1);

  @KafkaListener(topics = "${message.topic.name}", groupId = "foo", containerFactory = "fooKafkaListenerContainerFactory")
  public void listenGroupFoo(String message) {
    System.out.println("Received Messasge in group 'foo': " + message);
    latch.countDown();
  }

  @KafkaListener(topics = "${message.topic.name}", groupId = "bar", containerFactory = "barKafkaListenerContainerFactory")
  public void listenGroupBar(String message) {
    System.out.println("Received Messasge in group 'bar': " + message);
    latch.countDown();
  }

  @KafkaListener(topics = "${message.topic.name}", containerFactory = "headersKafkaListenerContainerFactory")
  public void listenWithHeaders(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
    System.out.println("Received Messasge: " + message + " from partition: " + partition);
    latch.countDown();
  }

  @KafkaListener(topicPartitions = @TopicPartition(topic = "${partitioned.topic.name}", partitions = { "0", "3" }), containerFactory = "partitionsKafkaListenerContainerFactory")
  public void listenToParition(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
    System.out.println("Received Message: " + message + " from partition: " + partition);
    this.partitionLatch.countDown();
  }

  @KafkaListener(topics = "${filtered.topic.name}", containerFactory = "filterKafkaListenerContainerFactory")
  public void listenWithFilter(String message) {
    System.out.println("Recieved Message in filtered listener: " + message);
    this.filterLatch.countDown();
  }

  @KafkaListener(topics = "${greeting.topic.name}", containerFactory = "greetingKafkaListenerContainerFactory")
  public void greetingListener(Greeting greeting) {
    System.out.println("Recieved greeting message: " + greeting);
    this.greetingLatch.countDown();
  }

}
​```

먼저, 어떠한 Topic의 메시지를 어떠한 방식으로 받을지를 @KafkaListener를 이용해서 지정해줍니다.

그리고, KafkaListener를 통해서 특정 파티션의 메시지를 받거나 특정 그룹의 메시지를 받거나 하는등의 설정도 가능합니다.

​

그리고, Consumer와 같은 경우, 병렬로 메시지를 처리하는 경우도 있기때문에, 동시접근으로 인한 Race Condition과 같은 경우를 막기 위해서 CountDownLatch 라는 함수를 이용해서 접근을 제한하게 됩니다.

(일종의 세마포어라고 보시면 됩니다.)

​

마지막으로 위에서 정의한 수신/송신 부분을 실제로 사용하는 곳입니다.

​
```java
public class KafkaExampleApplication {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(KafkaExampleApplication.class, args);

        MessageProducer producer = context.getBean(MessageProducer.class);
        MessageListener listener = context.getBean(MessageListener.class);

        producer.sendMessage("Hello, World!");
        listener.latch.await(10, TimeUnit.SECONDS);


        for (int i = 0; i < 5; i++) {
            producer.sendMessageToPartion("Hello To Partioned Topic!", i);
        }
        listener.partitionLatch.await(10, TimeUnit.SECONDS);


        producer.sendMessageToFiltered("Hello Baeldung!");
        producer.sendMessageToFiltered("Hello World!");
        listener.filterLatch.await(10, TimeUnit.SECONDS);


        producer.sendGreetingMessage(new Greeting("Greetings", "World!"));
        listener.greetingLatch.await(10, TimeUnit.SECONDS);

        context.close();
    }

  ...
}
​```

​
- 실행시 로그확인.

​
```log
  Sent message=[Hello, World!] with offset=[1]
  Received Messasge in group 'bar': Hello, World!
  Received Messasge in group 'foo': Hello, World!
  Received Messasge: Hello, World! from partition: 0
  Received Message: Hello To Partioned Topic! from partition: 0
  Received Message: Hello To Partioned Topic! from partition: 3
  Recieved Message in filtered listener: Hello Baeldung!
  Recieved Message in filtered listener: Hello World!
```

---

<details>
  <summary>Exp.</summary>  
  <pre>

  </pre>
</details>