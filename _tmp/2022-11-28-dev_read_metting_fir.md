---
layout: single
title: "[기술협상] 내부사전회의 - 2022.11.29"
excerpt: "기술협상 내부미팅 - 2022.11.29"

categories:
  - note
tags:
  - [meeting]

toc: false
toc_sticky: true

date: 2022-11-29
last_modified_at: 2022-11-29
---
# 다이소 기술협의 내부미팅

- BCG team : 옴니채널(온라인 + 오프라인) 구현목표
- 옴니채널
  - 상품, 매장 재고연계(현재 87개, 600개 희망)
  - 전시 관리 요구
  - 상품코드는 마스터성 코드로 별도관리 원하지 않음 
- OMS 시스템 : 협력업체 사용 시스템으로 추정

### 예상이슈
- 매장별 상품 매핑 이슈
- 매장상품 전시 방법
- 온라인 상품 Migration 방법


## 차별서비스

- 구독서비스 : 정기배송
  - 주문Batch 와 결재 Batch를 별도로 구현
  - **결재실패 시 RollBack 방법은?**

- B2B Mall : 사업자몰, 대량구매는 일반인도 가능하도록
  - **대량 배송 시 재고, 용차(무게, 부피 계산)의 이슈있음**

- UCC(User Create Contents)  : 개인Blog + 상품Mapping, Following 기능
- MemberShip 활용

## AI Engine - 개인화, 상품추천 기능
- **솔루션사용 외 수동 구현 요구**
- 별개 알고리즘 필요, 간단한 형태로 구현 협의 중

## 검색엔진
- **ElasticSearch 사용요청**
  - 별도 사업자 협의 중
- **GA(Google Analystic) 적용희망**

## 연계
- ERP, POS, WMS, OMS 와의 연계필요
  - **필요시 Kafka 적용필요**

## 상품
- 단독
- 패키지 : 오프라인 상 하나의 상품
- 번들 : 온라인 상에서만 존재, 출고 지시시 분리해서 처리

## 간편결제
- PAYCO, 카카오, 네이버 페이 사용중
  - **PG 없이Direct 사용 요청**

## 그외
- 상품권에 대한 환불이 있을 수 있음
- 상담솔루션 도입
  - 연계방법은?
- **ISMS문제 : 자기 시스템간 보안문제**

<details>
  <summary>Exp.</summary>  
  <pre>

### 참조


  </pre>
</details>