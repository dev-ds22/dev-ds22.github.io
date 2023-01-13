---
layout: single
title: "[Kafka] KafkaListener Multi Topic 사용하기"
excerpt: "KafkaListener topic 여러개 사용하기"

categories:
  - tech
tags:
  - [kafka, topic]

toc: false
toc_sticky: true

date: 2023-01-12
last_modified_at: 2023-01-12
---

# Spring - @KafkaListener topic 여러개 사용

## KafkaListener를 구현예제.

### 1. application.yaml에 토픽 이름 설정.

```yaml
kafka:
  topic:
    member: member-topic
```

- 설정에 있는 토픽명을 사용하기 위해 topics = "${kafka.topic.member}"를 입력.

```java
  @KafkaListener(id = "myGroupId", topics = "${kafka.topic.member}")
  public void kafkaListener(MessageDto messageDto) {
    switch (messageDto.getType()) {
      case REGISTER: {
          memberService.register(messageDto);
          break;
      }
      case WITHDRAWAL: {
          memberService.withdrawal(messageDto.getId());
          break;
      }
      default: {
          throw new RuntimeException("wrong type error. type=" + messageDto.getType());
      }
    }
  }
```

### 2. Topic 분리 또는 Topic명 변경의 경우

- 그냥 바꿔 버리면 일시적으로 Message를 못 받다가 유실가능.
- 기존 토픽도 사용하고 새 토픽도 사용하는 상태로 변경.

- application.yaml에 새로운 토픽 이름을 추가.

```yaml
kafka:
  topic:
    member-old: member-topic
    member: member-topic-sandbox
```

- KafkaListener 새성 시 토픽을 두 개 설정.

```java
  @KafkaListener(id = "myGroupId", topics = {"${kafka.topic.member-old}", "${kafka.topic.member}"})
  public void kafkaListener(MessageDto messageDto) {
    switch (messageDto.getType()) {
      case REGISTER: {
          memberService.register(messageDto);
          break;
      }
      case WITHDRAWAL: {
          memberService.withdrawal(messageDto.getId());
          break;
      }
      default: {
          throw new RuntimeException("wrong type error. type=" + messageDto.getType());
      }
    }
  }
```

### 3. 토픽의 개수 가변적으로 변경 시.

- application.yaml 설정에서 쉼표(,)로 토픽 리스트를 설정.

```yaml
kafka:
  topic:
    member: member-topic,member-topic-sandbox
```

- 가져다 사용할 때 SpEL (Spring Expression Language)를 사용.

```java
  @KafkaListener(id = "myGroupId", topics = "#{'${kafka.topic.member}'.split(',')}")
  public void kafkaListener(MessageDto messageDto) {
    switch (messageDto.getType()) {
      case REGISTER: {
          memberService.register(messageDto);
          break;
      }
      case WITHDRAWAL: {
          memberService.withdrawal(messageDto.getId());
          break;
      }
      default: {
          throw new RuntimeException("wrong type error. type=" + messageDto.getType());
      }
    }
  }
```

- kafka.topic.member의 설정을 쉼표(,)로 split 해서 리스트로 만든다는 의미.
- SpEL을 이용해서 설정은 하나인데 여러 개의 토픽을 쉽게 사용.


<details>
  <summary>Exp.</summary>  
  <pre>

### 참조

  </pre>
</details>