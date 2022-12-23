---
layout: single
title: "[APM] Spring APM(Application Performance Monitoring)"
excerpt: "Scouter, Actuator, Prometheus, Granafana, AWS CloudWatch"

categories:
  - tech
tags:
  - [scouter, actuator, prometheus, granafana, cloudWatch]

toc: false
toc_sticky: true

date: 2022-12-23
last_modified_at: 2022-12-23
---
# Spring 프로젝트 APM(Application Performance Monitoring)

- 참조1 : https://velog.io/@dplo1514/Spring-%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8-APM-%EA%B5%AC%ED%98%84-1%ED%83%84
- 참조2 : https://velog.io/@viewrain/Scouter-%EB%AA%A8%EB%8B%88%ED%84%B0%EB%A7%81-%EC%8B%9C%EC%8A%A4%ED%85%9C
- 참조3 : https://happy-jjang-a.tistory.com/25
 
## 1. SpringBoot Actuator
- 실행 중인 Application의 내부를 로그를 통해 들여다보는 기능.
- Application에 포함된 다양한 패키지의 로깅 레벨 logging level을 설정, 로그를 수집
- 지정된 엔드포인트가 받은 요청 횟수와 애플리케이션의 활성화 (helath Check) 상태 확인
- MicroMeter를 사용한 선택적 log수집이 가능, 원하는 api별 로그 확인 가능.
- health, info, metrics 등의 다양한 로그가 제공.

## 2. Prometheus
- SoundCloud에서 만든 오픈소스 시스템 메트릭 / 로그 시각화 도구.
- 스프링부트 프로젝트의 actuator/prometheus EndPoint에 일정 시간마다 Get요청을 보내 메트릭을 수집
- 수집한 메트릭을 DashBoard 형태로 모니터링이 가능한 GUI를 제공.

## 3. Granafana
- Prometheus와 마찬가지로 오픈소스 시스템 메트릭 / 로그 시각화 도구.
- Prometheus, MySql , CloudWatch 등 다양한 서버에서의 메트릭 수집이 가능
- DB Server , EC2 Instance , Spring Application 등의 다양한 서버의 메트릭을 한번에 모니터링이 가능.
- 다양한 DashBoard의 PlugIn을 활용해 다양한 GUI가 제공.

## 4. AWS CloudWatch
- AWS에서 제공하는 RDS , EC2 , ElasticCahce 등의 다양한 클라우드 서버의 모니터링 가능.
- EC2 Instance의 Memory 사용량을 모니터링하기 위해 사용.
- EC2 Instance를 직접 모니터링
- Grafana를 활용해 Spring Application과 CloudWatch의 동시 모니터링이 가능.
  
## 5. Scouter
- LG CNS에서 개발한 APM Tool로 어플리케이션 및 Host OS 자원에 대한 실시간 모니터링 기능.
- 모니터링 대상 (전용 agent)
  - Java Agent : Web application (on Tomcat, JBoss, Resin ...), Standalone java application
  - Host Agent : Linux, Windows, Unix
- 모니터링 대상 (Telegraf support)
  - Redis, nginX, apache httpd, haproxy, Kafka, MySQL, MongoDB, RabbitMQ, ElasticSearch, Kube, Mesos ...
- 모니터링 대상 (Zipkin-Scouter storage)
  - zipkin instrumentations (C#, Go, Python, Javascript, PHP...)를 XLog 차트를 통해 디스플레이.
  - see the zipkin-scouter-storage documentation.
  - see the zipkin instrumentations.

## 6. Elastic APM
- 요청에 대한 응답 시간, DB 쿼리, 외부 HTTP 요청 등의 정보를 실시간 모니터링.
- APM agents, Elastic APM integration, Elasticsearch, Kibana 4개의 요소로 구성.

## 고려사항
- **Cloud환경에서는 APM의 이용하는 로깅정보의 중앙집중을 위해 ELK Stack이 필요할수 있음.**


<details>
  <summary>Exp.</summary>  
  <pre>

### 참조

  </pre>
</details>