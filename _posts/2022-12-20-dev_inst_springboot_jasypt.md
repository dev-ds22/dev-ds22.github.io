---
layout: single
title: "[SPRING BOOT] application.yaml 에서 ENC사용 (feat.Jasypt)"
excerpt: "JASYPT(JAva Simplified encrYPTion) application.yml 설정"

categories:
  - tech
tags:
  - [tech, spring boot,jasypt]

toc: false
toc_sticky: true

date: 2022-12-21
last_modified_at: 2022-12-21
---
# application.yaml 에서 ENC사용

## 1. 개요

- Jasypt는 특정 값을 암호화해주는 라이브러리

## 2. DB의 url, username, password를 암호화하기

### 2-1. Mybatis Interceptor 배경

- gradle

```xml
  implementation (‘com.github.ulisesbocchio:jasypt-spring-boot-starter:3.0.4’)
```
  
- mavem

```xml
  <dependency>
      <groupId>com.github.ulisesbocchio</groupId>
      <artifactId>jasypt-spring-boot-starter</artifactId>
      <version>3.0.4</version>
  </dependency>
```

### 2-2. JasyptConfig.java

```java
@RequiredArgsConstructor
@Configuration
public class JasyptConfig {

    private static final String ENCRYPT_KEY = "my_jasypt_key";

    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor(){
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(ENCRYPT_KEY);                                            //암호화에 사용할 key
        config.setAlgorithm("PBEWithMD5AndDES");                                    //사용할 알고리즘
        config.setKeyObtentionIterations("1000");                                   //해싱 횟수
        config.setPoolSize("1");                                                    //인스턴스 pool
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator"); // salt 생성 클래스
        config.setIvGeneratorClassName("org.jasypt.iv.NoIvGenerator");
        config.setStringOutputType("base64");                                    //인코딩방식
        encryptor.setConfig(config);
        return encryptor;
    }
}
```

### 2-3. 암호화 대상값 미리 암호화

```java
@SpringBootTest
class JasyptApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void jasypt() {
        String url = "my_db_url";
        String username = "my_db_username";
        String password = "my_db_password";

        System.out.println(jasyptEncoding(url));
        System.out.println(jasyptEncoding(username));
        System.out.println(jasyptEncoding(password));
    }

    public String jasyptEncoding(String value) {

        String key = "my_jasypt_key";
        StandardPBEStringEncryptor pbeEnc = new StandardPBEStringEncryptor();
        pbeEnc.setAlgorithm("PBEWithMD5AndDES");
        pbeEnc.setPassword(key);
        return pbeEnc.encrypt(value);
    }
}
```
  

```bash
  암호화된 값 :
  GE1npS9T7z/GOqwcRLBPMbUSGQzj+WgSNCFvH5PoU83Z6ljps9ninis4P+4WNxBNz/RNhOrmReM=
  GFHmfIqHmrTaME93hLZqgg==
  oBMaRuMSZyJDmebXacU8Vg==
```

### 2-4. application.yml 설정

```yaml
  spring:
    datasource:
      url: ENC(GE1npS9T7z/GOqwcRLBPMbUSGQzj+WgSNCFvH5PoU83Z6ljps9ninis4P+4WNxBNz/RNhOrmReM=)
      username: ENC(GFHmfIqHmrTaME93hLZqgg==)
      password: ENC(oBMaRuMSZyJDmebXacU8Vg==)
      
  jasypt:
    encryptor:
      bean: jasyptStringEncryptor
```

- jasyptStringEncryptor를 jasypt bean으로 등록하고 각 속성값에 ENC(암호화값) 형식으로 입력

- 예제2

```yaml
  ... 생략

  # Jasypt
  jasypt:
    encryptor:
      bean: jasyptStringEncryptor
      algorithm: PBEWithMD5AndDES
      pool-size: 2
      string-output-type: base64
      key-obtention-iterations: 100000
      password: password    
  ... 생략
```
  

- bean: Jasypt Config 파일에서 등록하는 빈의 이름.
- algorithm: 암/복호화에 사용되는 알고리즘.
- pool-size: 암호화 요청을 담고 있는 pool의 크기. 2를 기본값으로 권장.
- string-output-type: 암호화 이후에 어떤 형태로 값을 받을지 설정. base64 / hexadecimal을 선택가능
- key-obtention-iterations: 암호화 키를 얻기 위해 반복해야 하는 해시 횟수. 클수록 암호화는 오래 걸리지만 보안 강도는 높아짐.
- password: ***암호화 키. 비밀키이므로 노출되지 않도록 주의***.

## 추가 1.

- 비밀키를 별도의 txt 파일로 분리하고, 이를. gitignore를 통해 제외시킨다면, 안전하게 설정 정보를 커밋
- 변경된 JasyptConfig 파일 소스.

```java
  // 암호화 키 파일을 읽어서 설정하는 방식
  // 암호화 키를 담고 있는 파일은 gitignore 이용 제외필요
  @Configuration
  @EnableEncryptableProperties
  public class JasyptConfig {
    @Value("${jasypt.encryptor.algorithm}")
    private String algorithm;
    @Value("${jasypt.encryptor.pool-size}")
    private int poolSize;
    @Value("${jasypt.encryptor.string-output-type}")
    private String stringOutputType;
    @Value("${jasypt.encryptor.key-obtention-iterations}")
    private int keyObtentionIterations;

    @Bean
    public StringEncryptor jasyptStringEncryptor() {
      PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
      encryptor.setPoolSize(poolSize);
      encryptor.setAlgorithm(algorithm);
      encryptor.setPassword(getJasyptEncryptorPassword());
      encryptor.setStringOutputType(stringOutputType);
      encryptor.setKeyObtentionIterations(keyObtentionIterations);
      return encryptor;
    }

    private String getJasyptEncryptorPassword() {
      try {
        ClassPathResource resource = new ClassPathResource("jasypt-encryptor-password.txt");
        return Files.readAllLines(Paths.get(resource.getURI())).stream()
            .collect(Collectors.joining(""));
      } catch (IOException e) {
        throw new RuntimeException("Not found Jasypt password file.");
      }
    }
  }
```
  

## 추가2. jasypt 알고리즘 AES256 변경방법

- dependencis 추가
  - implementation 'com.github.ulisesbocchio:jasypt-spring-boot-starter:+'

- 사용 클래스 변경
  - PooledPBEStringEncryptor encryptor -> StandardPBEStringEncryptor encryptor

- 알고리즘 변경
  - PBEWithMD5AndDES -> PBEWITHSHA256AND256BITAES-CBC-BC

- 나머지 사용법은 동일

<details>
  <summary>Exp.</summary>  
  <pre>

### 참조

  </pre>
</details>