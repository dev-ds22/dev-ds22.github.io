---
layout: single
title: "[기술협상] 다이소 기술형상 - 2022.12.01"
excerpt: "대량주문, 컨텐츠 커머스, 공통, UI/UX"

categories:
  - note
tags:
  - [meeting]

toc: false
toc_sticky: true

date: 2022-12-01
last_modified_at: 2022-12-01
---
# 12.01 다이소 기술협상  

## 대량주문  

- 대량주문 가능상품 별도 선정  
- 대량주문 구매가능여부는 Application에서 가능여부 판단 필요  
- 배송관련 로직 제공  
    - 상품개수 (CBM, 상품개수), 모든 상품에 등록  
    - CBM값이 있는지 => 다 등록예정, **TMS기능 - 다이소 측에서 로직제공**  
    - 택배가능여부  

- 주문에 따라 택배, 퀵으로 나뉨  
    - 퀵배송 운임테이블등 만들어야 함  
    - 퀵배송 : 다마스, 5톤차등 필요 시 용차로직은 퀵업체에서 제공 - 연계필요(거의 바로 응답)  
        - 제공불능등 문제 발생 시는 **퀵업체 책임**  
        - 실제 운영중인 업체 및 로직  

- 낱개수량조정 : 점포에서 수량조정(오프라인)  
- 배송가능여부 판단 후 뒷단로직 필요 시 오퍼레이터 확인 후 뒷단로직 적용  
- 커머스 플랫품에 아닌 뒷단에서 이루어짐  
    - Legacy와 인터페이스, 매장재고소거처분, WMS 다 섞여있음  
    - 퀵배차 : API연계필요 - **업체 상차, 배송 연계필요**  
    - 용차 : 퀵업체 1업체 선정후 연계필요(배송용량, 배송지역)  
    - 창고에서 WMS와의 연계만으로 가능할지? 별도 Application 개발제공이 필요한지?  


## 컨텐츠 커머스  

- 메인페이지 구성  
- 테마? - 기준이 무엇인가?  
    - 구성은 어떻게 구성하는가?  
    - 고정테마를 생각 : **프리셋으로 사전 설정 - BO에서 해시태그로 관리**  
        - 검색결과에서 필터된 형태로 생각  
        - 처음 작성 시 테마 제공(유저가 새로 등록하는것은 불가)  
        - 어떤형태로 그룹핑할지는 아직 기획중, (20대여성...)  
- 유사 컨텐츠?
    - 동일분류 컨텐츠에서 추천 -> 기준은 무엇인가?
    - 추천 필요하다면 상품추천엔진이 필요 (상품추천 솔루션 사용예정)
- 이미지 업로드, 폰트, 색깔 조정 필요 - 편집툴 필요
    - 툴(에디터) 내에서 편집
    - 이미지 리사이징 필요, 이미징 리사이징 솔루션 사용여부
        - 에디터에서 이미지리사이징 사용불가 => 용량등 최적화된 제한필요
        - PC와 모바일의 문제
    - 동영상 업로드 요청 : 내부서버에서 처리안함, 유투브등 이용하는 방식으로 협의
    - 자체 인프라에 등록, **비용등의 문제로 현재 고민 중**
- 미리보기에 따른 보안 이슈
    - 외부에서 접근 가능한지?
    - AWS S3 과금문제?
- 이미지 업로드 시 이미지 크롭기능 지원
- 몇개 템플릿 제공 후 해당화면 서비스  

- **솔루션사용여부 결정필요**
- **별도 정책 협의 필요**
- **이미지캐싱, CDN 사용하고 있는지? 시스템 확인 필요**

- 컨텐츠 커머스 홈기능  
    - 팔로잉 기능을 없앰, 개인 홈으로의 링크기능 삭제  
    - 검색 기능  
    - 자신이 작성한 글 목록, 타인이 작성한 글 목록  

- 리워드  
    - 컨텐츠 고객 : 다이소 멤버쉽과 별도  

- 컨텐츠 생성자도 일반고객과 동일, 아무나 컨탠츠생성 가능  
- UGC는 일반회원, 인플루언스 컨텐츠 등록은 BO Admin에서(외부연계컨텐츠)  

- 컨텐츠 편집은 불가  
- 낮은 컨텐츠 필터링  
    - 정책에의한 필터링, 자동 필터링 아님  
- 컨텐츠 자동 추천, 테마 수동 설정  
- 외부연계 컨텐츠 관리 기능, embeded 옵션가능 - 다음 영상노출기능 숨기기  
- 스크립트 허용안함.  

## 공통  

- Footer SNS URL 설정가능하도록 요청  
- 동적템플릿 일단 취소  
- HR정보 반영은 필요(스케줄링)  
- 기획전등에 자동플레이 기능 - 편의기능 추후조정 가능  
- Lazy loading 기능  
- **AWS관련 네오텍과의 협력을 원함**  

## UI / UX  

- 각기 다른 2개의 시안 ->(3W) 1차 시안 ->(3W) 2차 시안 ->(3W) 3차 최종결정  
- 기간은 유동적  
- 익스등 불가능한 영역 - 따로 문서화해서 논의필요

