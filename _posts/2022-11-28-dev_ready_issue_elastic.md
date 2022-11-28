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

https://velog.io/@shinychan95/Elasticsearch-%EA%B8%B0%EB%B3%B8-%EA%B0%9C%EB%85%90-%EB%B0%8F-%ED%8A%B9%EC%A7%95-%EC%A0%95%EB%A6%AC

## ElasticSearch
### DB만 있으면 되는데, 왜 굳이 검색엔진?

- 관계형 데이터베이스는 단순 텍스트매칭에 대한 검색만을 제공
- 텍스트를 여러 단어로 변형하거나 텍스트의 특질을 이용한 동의어나 유의어를 활용한 검색 가능
- ElasticSearch에서는 관계형 데이터베이스에서 불가능한 비정형 데이터의 색인과 검색이 가능
  - 빅데이터 처리에 중요.
- ElasticSearch에서는 형태소 분석을 통한 자연어 처리가 가능
  - ElasticSearch는 다양한 형태소 분석 플러그인을 제공.
- 역색인 지원으로 매우 빠른 검색이 가능

### 관계형 데이터베이스와의 개념 비교

| ElasticSearch | RDBMS |
|---|---|
| 인덱스(Index) | 데이터베이스(Database) |
| 샤드(Shard) | 파티션(Partition) |
| 타입(Type) | 테이블(Table) |
| 문서(Document) | 행(Row) |
| 필드(Field) | 열(Column) |
| 매핑(Mapping) | 스키마(Schema) |
| Query DSL | SQL |

- 6.0이하 버전의 ElasticSearch에서는 하나의 인덱스 내부 기능에 따라 데이터 분류 후에 여러 개의 타입을 만들어 사용했지만 현재는 하나의 인덱스에 하나의 타입만을 구성해야 합니다.
- 매핑은 필드의 구조와 제약조건에 대한 명세를 말하며 관계형 DB의 스키마와 같습니다.
- 관계형 DB와 ElasticSearch는 인덱스라는 개념을 다르게 사용하는데, 관계형 DB에서 인덱스는 그저 Where절의 쿼리와 Join을 빠르게 만들어주는 보조데이터의 도구로 사용됩니다.

### RESTful API 사용
관계형 DB와 ElasticSearch를 비교했을 때, 가장 커다란 부분 중 하나는 데이터의 CRUD를 하는 방식이 조금 다른 것입니다.

관계형 DB의 경우, 우리가 주로 데이터의 추가, 삭제 등을 위해 사용하는 방법은 클라이언트에서 관계형 DB가 있는 서버에 연결을 맺어 SQL을 날리는 방식이었을 것입니다.

이를테면 JDBC에서 관계형DB가 있는 아이피와 포트를 연결하여 SELECT, INSERT, DELETE등의 쿼리를 날리는 방식이었을 것입니다.

ElasticSearch의 경우에는 이와 약간 다릅니다. 데이터를 CRUD하기 위해서 RESTful API라는 방식을 이용합니다.

RESTful API의 정의는 위의 사이트 링크를 참조하시면 될 것 같습니다. HTTP 통신에서 갖는 GET, POST, PUT, DELETE 등의 메소드가 RESTful API의 형식대로 그대로 적용됩니다. HEAD 메소드는 친숙하진 않지만, 특정 문서의 정보 유무를 확인하는데 이용될 수 있습니다.

또한 ElasticSearch의 POST 즉, 데이터 삽입의 경우에는 관계형 데이터베이스와 약간 다른 특성을 갖고 있는데, 스키마가 미리 정의되어 있지 않더라도, 자동으로 필드를 생성하고 저장한다는 점입니다. 이러한 특성은 큰 유연성을 제공하지만 선호되는 방법은 아닙니다.

### ElasticSearch 장점
데이터베이스 대용으로 사용 가능
NoSQL 데이터베이스처럼 사용이 가능합니다. 또한 분류가 가능하고 분산 처리를 통해 거의 실시간(NRT)에 데이터 검색이 가능합니다.

대량의 비정형 데이터 보관 및 검색 가능
기존 데이터베이스로 처리하기 어려운 대량의 비정형 데이터 검색이 가능하며, 전문 검색(Full-Text Search)과 구조 검색 모두를 지원합니다. 기본적으로는 검색엔진이지만 MongoDB나 Hbase처럼 대용량 스토리지로 사용도 가능합니다.

오픈소스 검색엔진
아파치 루씬(Lucene)기반 오픈소스 검색엔진으로 무료로 사용 가능하며, 많은 컨트리뷰터들이 실시간으로 소스를 수정해주기 때문에 버그가 발생하면 빠르게 해결됩니다.

전문 검색(Full-text Search)
내용 전체를 색인하여 특정 단어가 포함된 문서를 검색하는 것이 가능합니다.

통계 분석
비정형 로그 데이터를 수집하고 한 곳에 모아서 통계 분석이 가능하다. 키바나를 이용하면 시각화 또한 가능합니다.

스키마리스(Schemaless)
기존의 관계형 데이터베이스는 스키마라는 구조에 따라 데이터를 적합한 형태로 변경하여 저장 관리하지만 ElasticSearch는 비정형의 다양한 형태의 문서도 자동으로 색인, 검색이 가능합니다.

RESTful API
RESTful API를 사용하여 HTTP통신 기반으로 요청을 받아 JSON 형식으로 응답한다는 것은 다양한 플랫폼에서 응용 가능하다는 것을 의미합니다.

멀티 테넌시(Multi-tenancy)
ElasticSearch에서 인덱스는 관계형 DB의 데이터베이스와 같은 개념임에도 불구하고, 서로 다른 인덱스에서도 검색할 필드명만 같으면 여러 개의 인덱스를 한번에 조회할 수 있습니다.

Document-Oriented
여러 계층의 데이터를 JSON 형식의 구조화된 문서로 인덱스에 저장 가능합니다. 계층 구조로 문서도 한 번의 쿼리로 쉽게 조회 가능합니다.

역색인(Inverted-Index)
앞의 글에 설명했듯 역색인을 지원합니다.

확장성과 가용성
매우 많은 데이터가 존재할 때, 분산 시스템 구성으로 병렬적인 처리가 가능합니다. 분산 환경에서는 데이터가 샤드(Shard)라는 단위로 나누어 제공됩니다. 인덱스 생성 시마다 샤드의 수 조정이 가능합니다. 데이터의 종류와 성격에 따라 데이터를 분산하여 빠르게 처리 가능합니다.

### ElasticSearch 단점
'실시간(Real Time)' 처리는 불가능하다.

ElasticSearch의 데이터 색인의 특징 때문에 ElasticSearch의 색인된 데이터는 1초 뒤에나 검색이 가능합니다. 왜냐하면 색인된 데이터가 내부적으로 커밋(Commit)과 플러시(Flush)와 같은 과정을 거치기 때문입니다. 그래서 ElasticSearch 공식 홈페이지에서도 NRT(Near Real Time)라는 표현을 씁니다.
트랜잭션(Transaction) 롤백(Rollback) 등의 기능을 제공하지 않는다.

분산 시스템 구성의 특징 때문에, 시스템적으로 비용 소모가 큰 롤백, 트랜잭션을 지원하지 않습니다. 그래서 데이터 관리에 유의해야 합니다.
진정한 의미의 업데이트(Update)를 지원하지 않는다.

ElasticSearch에는 물론 업데이트 명령이 있습니다만, 실제로는 데이터를 삭제했다가 다시 만드는 과정으로 업데이트됩니다. 이러한 특성은 나중에 불변성(Immutable)이라는 이점을 제공하기도 합니다.

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

### 참조
 * [Elastic 가이드 북]( https://esbook.kimjmin.net/ )
 * [AWS EC2 ELK 설치]( https://angryfullstack.tistory.com/entry/AWS-EC2-ELK-Elasticsearch7x-Logstash7x-Kibana7x-%EC%B5%9C%EC%8B%A0%EB%B2%84%EC%A0%84-%EC%84%A4%EC%B9%98 )

  </pre>
</details>