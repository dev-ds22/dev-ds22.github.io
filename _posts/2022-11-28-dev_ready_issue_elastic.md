---
layout: single
title: "[Tech Stack] 02 - ELK Stack"
excerpt: "ELK stack 적용과 kafka 적용여부"

categories:
  - tech
tags:
  - [tech, elk, kafka]

toc: false
toc_sticky: true

date: 2022-11-28
last_modified_at: 2022-11-28
---
# ELK stack 과 Kafka
- 엘라스틱 사용요청으로 ELK Stack 환경필요
- ELK Stack = ElasticSearch + Logstash + Kibana (+ Beats)

## Logstash
- 데이터 수집 파이프라인
- 파이프라인으로 데이터를 수집하여 필터를 통해 변환 후 ES 로 전송

## Kibana
- ES 에서 색인된 데이터를 검색하여 분석 및 시각화

## Beats
- 경량 데이터 수집기
- 서버에 에이전트로 설치하여 다양한 유형의 데이터를 Logstash나 Elasticsearch에 전송

## ELK(Elasticsearch + Logstash + Kibana) + Filebeat + Kafka

# Kafka 사용


<details>
  <summary>Exp.</summary>  
  <pre>

### 실무

- END
  </pre>
</details>