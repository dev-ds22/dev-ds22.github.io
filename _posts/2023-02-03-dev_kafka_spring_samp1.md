---
layout: single
title: "[tech] Spring Boot Kafka Template 사용 방법
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

# 1. SAmple 1

## Spring Boot Kafka Template 사용 방법 - Kafka Header & Message
얼마전에 Spring Boot With Kafka Single Broker 에서 Kafka 를 Docker Compose 를 이용하여 구성하고 Spring Boot 에서 사용하는 방법을 정리 했었다. 프로젝트에서 정리했던 것처럼 간단하게 쓸려고 했으나 하나의 Topic 만을 사용하는게 아니라 Multi Topic 을 이용해야 되서 Spring Boot Kafka Template 을 이용하여 Kafka 에 Multi Topic 을 Produce(=Send) 하다고 Consume(=Receive) 하는 방법을 찾아보았다.

전체 코드는 https://github.com/jjeaby/kafkaSample 에 있다.
Kafka 를 Docker Compose 로 실행하는 것은 여기(Spring Boot With Kafka Single Broker)를 참고 하시면 됩니다. :)
당연하게도 Spring Boot 에서 사용하기 위해선 pom.xml 에 아래와 같은 kafka 관련 Dependency 를 추가해야 합니다. 이때, 주의 할 점은 Spring 버전에 맞는 kafka client 버전이 있다는 것입니다. 사용하고 있는 Spring Boot 버전에 맞도록 kafka 라이브러리의 버전을 잘 맞추어야 합니다.


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
pom.xml 설정이 되었다면, application.properties 에 kafka 에서 사용할 설정 값들을 정의 합니다.

일반적으로 kafka 를 사용할 때는 사용할 API 별로 호출 하는 Produce(=Send), Consume(=Recv) 하는 Topic 만들게 됩니다. API 가 10 개 라면 Topic 은 20개가 되는거죠. 하지만, 이렇게 사용하는것은 API 가 명시적으로 정의가 잘 된 경우입니다.

하지만, 개발 하는 중에는 이렇게 잘 설계를 하는 경우가 드뭅니다.. 저도 그래서, Kafka 에 Topic 을 Produce 할때 Kafka Header 에 Message Key 를 넣어서 이를 이용해서 API 호출을 하였습니다. :)

그런 이유로 topic 은 호출하는 from(시스템 이름)-to(시스템 이름)-topic 으로 정의 하였습니다. 여기서 주의할 부분은 group-id 와 topic-id 가 동일하면 Consume(=Recv) 가 되지 않으니 다르게 설정해야 합니다.

# kafka 서버 주소
spring.kafka.bootstrap-servers=127.0.0.1:9092
# consumer 에서 사용하는 group id
spring.kafka.consumer.medium-jjeaby-group-id=medium-jjeaby-group
spring.kafka.consumer.company-jjeaby-group-id=company-jjeaby-group
# 사용하는 topic
spring.kafka.template.medium-jjeaby-topic=medium-jjeaby-topic
spring.kafka.template.company-jjeaby-topic=company-jjeaby-topic

# kafka 에서 메세지를 받고 자동으로 ACK 를 전송 여부 설정(true = 자동으로, false = 별도로 코드 구성 필요)
spring.kafka.consumer.enable-auto-commit=true
# kafka 에서 메세지를 가져오는 consumer의 offset정보가 존재하지 않는 경우의 처리 방법(- latest : 가장 마지막 offset부터, earliest : 가장 처음 offset부터, none : offset 없다면 에러 발생
spring.kafka.consumer.auto-offset-reset=latest
spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.producer.value-serializer=org.apache.kafka.common.serialization.StringSerializer
spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer
spring.kafka.consumer.max-poll-records=1000
작성할 코드는 아래의 4개 클래스 입니다.

KafkaProducerConfig
: Kafaka Topic(=Message) 를 Produce(=Send) 하는 설정 클래스
KafkaProducer
: Kafaka Topic(=Message) 를 Produce(=Send) 구현체
KafkaConsumerConfig
: Kafaka Topic(=Message) 를 Consume(=Recv) 하는 설정 클래스
KafkaConsumer
: Kafaka Topic(=Message) 를 Consume(=Recv) 하는 구현체

이제 kafka 에 Topic 을 Produce(=Send) 하는 부분을 작성하겠습니다.

1. kafkaProducerConfig 클래스를 만들고 아래와 같이 kafka 접속 주소, Serializer, Deserializer 를 설정합니다.

package ml.jjeaby.kafka;

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
    public ConsumerFactory<String, String> meJJConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeSerializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeSerializer);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = "meJJKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String>
    meJJKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(meJJConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> diJJConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeSerializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeSerializer);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = "diJJKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String>
    diJJKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(diJJConsumerFactory());
        return factory;
    }
}
2. KafkaProducer 를 생성하고 Kafka 로 Topic 을 Produce(=Send) 하는 SendMessage 를 작성합니다. 다행이도 kafkaTemplate.send 를 호출 하면 간단하게 됩니다.

여기서는, 위에서 이야기한 것처럼 setHeader(KafkaHeaders.MESSAGE_KEY, messageKey) 코드를 이용하여 kafka Header 에 Message Key 를 설정 하였습니다. 이런 방법으로 Header 에 Custom Key 를 설정 할 수 있다는 걸 알고 사용하면 좋습니다. :)

package ml.jjeaby.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    @KafkaListener(topics = "${spring.kafka.template.medium-jjeaby-topic}", containerFactory = "meJJKafkaListenerContainerFactory", groupId = "${spring.kafka.consumer.medium-jjeaby-group-id}")
    public void listenMeJJTopic(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String messageKey) throws Exception {
        log.info("Topic: [medium-jjeaby-topic] messageKey Message: [" + messageKey + "]");
        log.info("Topic: [medium-jjeaby-topic] Received Message: [" + message + "] from partition: [" + partition + "]");
    }

    @KafkaListener(topics = "${spring.kafka.template.company-jjeaby-topic}", containerFactory = "diJJKafkaListenerContainerFactory", groupId = "${spring.kafka.consumer.company-jjeaby-group-id}")
    public void listenDiJJTopic(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String messageKey) throws Exception {
        log.info("Topic: [company-jjeaby-topic] messageKey Message: [" + messageKey + "]");
        log.info("Topic: [company-jjeaby-topic] Received Message: [" + message + "] from partition: [" + partition + "]");
    }
}
이제 kafka 에 Topic 을 Consume(=Recv) 하는 부분을 작성합니다.

1. kafkaConsumerConfig 클래스를 만들고 아래와 같이 kafka 접속 주소, Serializer, Deserializer, offsetReset, 등을 설정합니다.

위에서 Multi Topic 을 지원 해야 한다고 했기에 ConsumerFactory, ConcurrentKafkaListenerContainerFactory 를 지원하는 Muti Topic 의 갯수와 동일하게 생성해야 합니다. 여기서는 me-jj-topic, di-jj-topic 을 사용 하므로 각 Topic 마다 ConsumerFactory, ConcurrentKafkaListenerContainerFactor 를 생하면 됩니다.

package ml.jjeaby.kafka;

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
    public ConsumerFactory<String, String> meJJConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeSerializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeSerializer);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = "meJJKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String>
    meJJKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(meJJConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> diJJConsumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeSerializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeSerializer);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, enableAutoCommit);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean(name = "diJJKafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, String>
    diJJKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(diJJConsumerFactory());
        return factory;
    }
}
2. KafkaConsumer 클래스를 생성하고 Kafka 로 Topic 을 Consum(=Recv) 하는 KafkaListener 를 작성합니다. KafkaListener 는 @kafkaListner 어노테이션을 이용하여 간단하게 작성 됩니다. @kafkaListner 어노테이션으로 자동으로 Kafka 에서 Topic 을 Polling 하므로 별도의 코드/설정을 할 필요가 없습니다.

여기서는 setHeader(KafkaHeaders.MESSAGE_KEY, messageKey) 코드로 Produce 에 설정한 kafka Header 에 Message Key 를 @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String messageKey 로 받을 수 있다는 것을 기억 하면 좋습니다. 이런 방식으로 message, messageKey 를 받기 때문이죠 :)

package ml.jjeaby.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaConsumer {

    @KafkaListener(topics = "${spring.kafka.template.me-jj-topic}", containerFactory = "meJJKafkaListenerContainerFactory", groupId = "${spring.kafka.consumer.me-jj-group-id}")
    public void listenMeJJTopic(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String messageKey) throws Exception {
        log.info("Topic: [me-jj-topic] messageKey Message: [" + messageKey + "]");
        log.info("Topic: [me-jj-topic] Received Message: [" + message + "] from partition: [" + partition + "]");
    }

    @KafkaListener(topics = "${spring.kafka.template.di-jj-topic}", containerFactory = "diJJKafkaListenerContainerFactory", groupId = "${spring.kafka.consumer.di-jj-group-id}")
    public void listenDiJJTopic(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String messageKey) throws Exception {
        log.info("Topic: [di-jj-topic] messageKey Message: [" + messageKey + "]");
        log.info("Topic: [di-jj-topic] Received Message: [" + message + "] from partition: [" + partition + "]");
    }
}
여기까지 하면, Spring Boot 의 kafka Template 을 이용한 Producer, Consumer 의 구현은 깔끔하게 완료된다.

이제 Topic 을 Producer(=Send) 하여 Consume(=Recv) 를 테스트 하기 위해 GET /send Rest API 에서 kafkaProducer.sendMessage 를 호출 하도록 추가하고, 서버를 실행해보자~~

@Autowired
KafkaProducer kafkaProducer;

@RequestMapping(method = RequestMethod.GET, path = "/send")
String send() {
    kafkaProducer.sendMessage("medium-jjeaby-topic", "message key medium", "medium -> jjeaby message");
    kafkaProducer.sendMessage("company-jjeaby-topic", "message key company", "company -> jjeaby message");
    return "Kafka Produce!!!";
}
서버가 실행 되고 localhost:8080/send 를 호출 하면 "medium-jjeaby-topic", "company-jjeaby-topic” Topic 이 Produce(=Send) 되고 Consume(=Recv) 된 것이 서버 로그에 나타난다.


Spring Boot 에서 kafka 를 잘 사용하는 것은 좀 더 많은 공부가 필요하지만, 간단하게 사용하기는 이 정도만으로도 충분하다~~:)

  
---
  
# Sample 2.
 pom.xml에 아래와 같이 추가해줍니다.

​

<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example.kafkaexample</groupId>
    <artifactId>kafkaexample</artifactId>
    <version>1.0-SNAPSHOT</version>

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

</project>
​

다음으로, kafka의 설정 파일을 application.properties 파일에 아래와 같이 추가 해주자.

​

kafka.bootstrapAddress=localhost:9092
message.topic.name=mytopic
greeting.topic.name=greeting
filtered.topic.name=filtered
partitioned.topic.name=partitioned
​

​

​

다음으로, 아래와 같이 Kafka의 Topic에 관한 설정을 해준다.

​

package com.example.kafkaexample.config;


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
​

Kafka-Spring 에서는 위의 코드를 통해서, 코드를 이용해서 프로그래밍 적으로 메시지 큐의 토픽을 생성 할 수 있습니다.

​

스프링 부트(Kafka-Spring)에서는, 위와 같이 토픽을 생성해주는 함수를 만들고 Bean으로 등록해주면 자동으로 토픽을 생성해서 주입해줍니다.

​

또한, KafkaAdmin 타입의 생성자를 만들어서, 카프카의 설정정보도 주입이 가능합니다.

​

​

그리고 나서, 실제로 메시지를 발행하는 Producer에 관한 설정을 아래 코드와 같이 해줍시다.

​

package com.example.kafkaexample.config;


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
        configProps.put(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            bootstrapAddress
        );
        configProps.put(
            ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class
        );
        configProps.put(
            ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class
        );

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
​

먼저, ProducerFactory 객체를 이용해서 각 메시지 종류별로, 메시지를 어디에 보내고, 어떠한 방식으로 처리할것인지를 설정해줍니다. 그리고 카프카에서, 실제 메시지는 KafkaTemplate 이라는 객체에 담겨서 보내지게 됩니다.

(일종의 편지봉투 라고 보시면 됩니다.)

​

만약 소켓프로그래밍에 익숙하다면, Producer 객체는 소켓 디스크립터고, ProducerFactory는 소켓 디스크립터를 만들어주는 팩토리 메서드 라고 이해하면 편할겁니다.

​

위의 예제에서는, 2가지 종류의 메시지를 정의 하였습니다.

(즉, 2가지 종류의 편지봉투를 만들었다고 보시면 됩니다.)

​

다음으로, 실제로 메시지를 가져오는 부분인 Consumer에 대한 코드를 아래와 같이 작성 해줍니다.

​

package com.example.kafkaexample.config;


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
​

위의, Producer 부분과 유사하게 ConsumerFactory 객체를 이용해서 각 메시지 종류별로, 메시지를 어디에서 받고, 어떠한 방식으로 처리할것인지를 설정 해줍니다.

​

그리고 위의 예제에서, Consumer와 같은 경우, 위에서 설정한 각 Topic 별로 메시지를 어디서/어떻게 받을지를 설정해주는 메서드들을 지정하였습니다.

​

​

마지막으로, 위에서 작성한 설정을 기반으로 아래와 같이 메시지 큐에 데이터를 넣고 빼보도록 합시다.

​

package com.example.kafkaexample;

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
​

코드의 양이 많으니 하나씩 뜯어서 보도록 합시다.

​

먼저, 메시지를 생성하는 부분입니다.

​

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
​

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
​

먼저, 어떠한 Topic의 메시지를 어떠한 방식으로 받을지를 @KafkaListener를 이용해서 지정해줍니다.

그리고, KafkaListener를 통해서 특정 파티션의 메시지를 받거나 특정 그룹의 메시지를 받거나 하는등의 설정도 가능합니다.

​

그리고, Consumer와 같은 경우, 병렬로 메시지를 처리하는 경우도 있기때문에, 동시접근으로 인한 Race Condition과 같은 경우를 막기 위해서 CountDownLatch 라는 함수를 이용해서 접근을 제한하게 됩니다.

(일종의 세마포어라고 보시면 됩니다.)

​

마지막으로 위에서 정의한 수신/송신 부분을 실제로 사용하는 곳입니다.

​

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
​

​

​

실제로 실행해보면 아래와 같이 메시지를 주고받는것을 알 수 있습니다.

​

Sent message=[Hello, World!] with offset=[1]
Received Messasge in group 'bar': Hello, World!
Received Messasge in group 'foo': Hello, World!
Received Messasge: Hello, World! from partition: 0
Received Message: Hello To Partioned Topic! from partition: 0
Received Message: Hello To Partioned Topic! from partition: 3
Recieved Message in filtered listener: Hello Baeldung!
Recieved Message in filtered listener: Hello World!




---

<details>
  <summary>Exp.</summary>  
  <pre>

  </pre>
</details>