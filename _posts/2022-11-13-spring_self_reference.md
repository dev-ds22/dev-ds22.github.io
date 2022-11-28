---
layout: single
title:  "[Spring] Proxy방식의 Transaction 호울 (feat. self reference)"
excerpt: "runtime-error로 인한 rollback 발생시 DB로깅"

categories:
  - Spring
tags:
  - [Spring]

toc: false
toc_sticky: false
 
date: 2022-11-13
last_modified_at: 2022-11-13
---
# Using self reference

## 1. 개요
- runtime-error로 인한 rollback 발생시 DB로깅

 - I would like to rollback a transaction for the data in case of errors and at the same time write the error to db. I can't manage to do with Transactional Annotations.

``` java
    @Service
    public class MyService{

       @Transactional(rollbackFor = Exception.class)
        public void updateData() {
            try{
                processAndPersist();    // <- db operation with inserts
                int i = 1/0; // <- Runtime error
            }catch (Exception e){
                persistError()
                trackReportError(filename, e.getMessage());
            }
        }
    
    
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void persistError(String message) {
            persistError2Db(message); // <- db operation with insert
        }
```

>
> Just throwing an error will not help because persistError() will 
> have the same transaction as updateData() has. Because persistError() is called 
> using this reference, not a reference to a proxy.
>
 
## 2. 해법
- 1. **Using self reference.**
- 2. Using self injection Spring self injection for transactions
- 3. Move the call of persistError() outside updateData() (and transaction). Remove @Transactional from persistError() (it will not work) and use transaction of Repository in persistError2Db().
- 4. Move persistError() to a separate serface. It will be called using a proxy in this case.
- 5. Don't use declarative transactions (with @Transactional annotation). Use Programmatic transaction management to set transaction boundaries manually https://docs.spring.io/spring-framework/docs/3.0.0.M3/reference/html/ch11s06.html



## 3. Using self reference
- You can use self reference to MyService to have a transaction, because you will be able to call not a method of MyServiceImpl, but a method of Spring proxy.

``` java
@Service
public class MyServiceImpl implements MyService {

    public void doWork(MyService self) {
        DataEntity data = loadData();

        try {
            self.updateData(data);
        } catch (Exception ex) {
            log.error("Error for dataId={}", data.getId(), ex);
            self.persistError("Error");
            trackReportError(filename, ex);
        }
    }

    @Transactional
    public void updateData(DataEntity data) {
        persist(data);    // <- db operation with inserts
    }

    @Transactional
    public void persistError(String message) {
        try {
            persistError2Db(message); // <- db operation with insert
        } catch (Exception ex) {
            log.error("Error for message={}", message, ex);
        }
    }
}

public interface MyService {
    void doWork(MyService self);
    void updateData(DataEntity data);
    void persistError(String message);
}
```


<details>
  <summary>Exp.</summary>  
  <pre>

### 실무


- END
  </pre>
</details>