---
layout: single
title: "[Spring Batch] Spring Batch"
excerpt: "Spring Batch 정리 #3"

categories:
  - tech
tags:
  - [tech, Spring Batch]

toc: false
toc_sticky: true

date: 2022-12-14
last_modified_at: 2022-12-14
---
# Spring Batch 정리 #3
## SimpleJob
- SimpleJob은 Step을 실행시키는 Job 구현체로서 SimpleJobBuilder에 의해 생성된다.
- 여러 단계의 Step을 구성할 수 있으면 Step을 순차적으로 실행시킨다.
- 모든 Step의 시랳ㅇ이 성공적으로 완료되어야 Job이 성공적으로 완료된다.
- 맨 마지막에 실행한 Step의 BatchStatus가 Job의 최종 BatchStatus가 된다.

```java
@Bean
public Job job1() {
    return jobBuilderFactory.get("job1")
      .start(step1())
      .next(step2())
      .next(step3())
      .incrementer(new RunIdIncrementer())
      .validator(new JobParametersValidator() {
        @Override
        public void validate(JobParameters parameters) throws JobParametersInvalidException {

        }
      })
      .preventRestart()
      .listener(new JobExecutionListener() {
        @Override
        public void beforeJob(JobExecution jobExecution) {

        }

        @Override
        public void afterJob(JobExecution jobExecution) {

        }
      })
      .build();
}
```

- get(String): Job을 생성한다.
- start(Step): Job에서 처음 실행할 Step을 설정한다.
  - 최초에 한번 설정하면 SimpleJobBuilder이 반환된다.
- next(Step): 다음에 실행 할 Step을 설정한다.
  - 횟수제한 X, 모든 next()의 Step이 종료되면 Job이 종료된다.
- incrementer(JobParametersIncrementer): JobParameter의 값을 자동으로 증가해주는 JobParametersIncrementer을 설정한다.
  - JobParameters에서 필요한 값을 증가시켜 다음에 사용될 JobParameters 오브젝트를 리턴한다.
  - 기존의 JobParameter 변경없이 Job을 여러 번 시작하고자 할 때 사용한다.
- RunIdIncrementer 구현체를 지원하고, 필요에 따라 인터페이스를 직접 구현할 수 있다.
- preventRestart(Boolean): Job의 재시작 가능 여부를 설정한다. 기본값 true
  - false는 재시작을 지원하지 않는다는 의미이다.
  - Job이 실패해도 재시작이 안되며 Job을 재시작하려고 하면 JobRestartException이 발생한다.
  - 재시작과 관련있는 기능으로 Job을 처음 실행하는 것과는 아무 상관이 없다.
- validator(JobParameterValidator): JobParameter을 실행하기 전에 올바른 구성이 되었는지 검증하는 JobParametersValidator을 설정한다.
  - Job 실행에 필요한 파라미터를 검증하는 용도로 사용한다.
  - DefaultJobParametersValidator 구현체를 지원하고, 필요에 따라 직접 인터페이스를 구현할 수 있다
- listener(JobExecutionListner): Job 라이프 사이클의 특정 시점에 콜백 제공도록 설정한다.
- build(): SimpleJob을 생성한다.

<details>
  <summary>Exp.</summary>  
  <pre>

### 참조


  </pre>
</details>