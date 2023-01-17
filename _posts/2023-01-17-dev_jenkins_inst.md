---
layout: single
title: "[Jenkins] jenkins 설치 및 자동배포"
excerpt: "CentOs 환경 jenkins 설치 및 자동배포 연결과정 정리"

categories:
  - tech
tags:
  - [jenkins]

toc: false
toc_sticky: true

date: 2023-01-17
last_modified_at: 2023-01-17
---

# jenkins 설치 및 자동배포

## 1. jenkins 설치

### 1-1. jenkins repository를 설정파일 생성

```bash
  $ wget -O /etc/yum.repos.d/jenkins.repo https://pkg.jenkins.io/redhat-stable/jenkins.repo
```

### 1-2. /etc/yum.repos.d/jenkins.repo경로에 파일 존재 확인 후 아래 실행

```bash
  $ rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io.key
```
 
### 1-3. yum으로 jenkins 설치

``` bash
  $ yum install jenkins
```

> 이하오류 발생 시  
>   
> Errors during downloading metadata for repository 'gitlab_gitlab-ce':  
>   - Curl error (35): SSL connect error for https:~~~~  
>   
> /etc/yum.repos.d 에서해당하는 repo 파일을 지워준다.  

```bash
  설치되었습니다:
    jenkins-2.375.2-1.1.noarch

  완료되었습니다!
```

### 1-4. 기본 포트(8080)을 9090으로 변경

```bash
  $ vi /etc/sysconfig/jenkins

  ...
  #JENKINS_PORT="8080"
  ## 위 내용을 주석처리하고 아래와 같이 9099으로 변경
  JENKINS_PORT="8099"
  ...

```

> 현재 사용중인 포트 확인하기  
>   
> netstat -tulpn | grep LISTEN   

### 1-5. 방화벽 오픈

```bash
  firewall-cmd --permanent --zone=public --add-port=8099/tcp
  firewall-cmd --reload
```

### 1-6. 서비스 시작

- Jenkins를 systemctl에 등록해주고 실행을 시킵니다. 

```bash
  $ sudo systemctl daemon-reload
  $ sudo systemctl start jenkins
```

#### 자동시작할 경우

```bash
  $ sudo systemctl enable jenkins
```

#### 에러 CASE 1.
- systemctl status jenkins 명령을 이용하여 오류 부분을 상세히 확인.

```bash
  $ systemctl status jenkins

  /etc/init.d/functions : No such file or directory
```

- /etc/init.d/functions 모듈설치.

```bash
  $ yum install -y initscripts
```

#### 에러 CASE 2.

```bash
  The unit jenkins.service has entered the 'failed' state with result 'exit-code'.
  1월 17 13:40:36 localhost systemd[1]: Failed to start Jenkins Continuous Integration Server.
``` 

- 원인1. : 포트가 충돌나서 발생. 
  - /etc/sysconfig/jenkins 경로의 포트를 8080에서 8099으로 변경.
  - /usr/lib/systemd/system/jenkins.service 파일도 수정필요.

  - 해결책 : jenkins.service 파일의 Environment="JENKINS_PORT=9090"로 변경

- 원인2. : 포트가 충돌나서 발생. 

```bash
  localhost jenkins[3385779]: jenkins: invalid Java version: openjdk version "1.8.0_352"
  localhost jenkins[3385779]: OpenJDK Runtime Environment (build 1.8.0_352-b08)
  localhost jenkins[3385779]: OpenJDK 64-Bit Server VM (build 25.352-b08, mixed mode)
  localhost systemd[1]: jenkins.service: Main process exited, code=exited, status=1/FAILURE
```

  - https://www.jenkins.io/blog/2022/06/28/require-java-11/
  - 2022.6월 이후 jenkins 버전에서는 java 11필요.

- 기존에 설치 되어 있던 jenkins 를 삭제

> yum remove jenkins  

```log
  제거되었습니다:
    jenkins-2.375.2-1.1.noarch

  완료되었습니다!
```

- 이전버전 rpm 파일 다운로드

> wget https://get.jenkins.io/redhat-stable/jenkins-2.319.1-1.1.noarch.rpm  

- rpm 파일 다운 받은 파일을 설치

> yum install jenkins-2.319.1-1.1.noarch.rpm   


- demonnize 모듈이 없어 install 필요

> vi /etc/yum.repos.d/daemonize.repo  

  - 아래내용 입력, 저장

```bash
[daemonize]
baseurl=https://download-ib01.fedoraproject.org/pub/epel/7/x86_64/
gpgcheck=no
enabled=yes
```

  - 저장이 완료후 demonize 모듈을 설치한다

  > yum install daemonize -y    
 

  - jenkins를 설치.

#### JAVA 11설치 시 

- amazon-corretto-11 설치 시 

```bash
  sudo rpm --import https://yum.corretto.aws/corretto.key 
  sudo curl -L -o /etc/yum.repos.d/corretto.repo https://yum.corretto.aws/corretto.repo
  sudo yum install -y java-11-amazon-corretto-devel
```

```bash
  $ alternatives --config java

  [root@localhost /]# alternatives --config java

  There are 2 programs which provide 'java'.

    Selection    Command
  -----------------------------------------------
  + 1           java-1.8.0-openjdk.x86_64 (/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.352.b08-2.el9.x86_64/jre/bin/java)
  *  2           /usr/lib/jvm/java-11-amazon-corretto/bin/java
```

- port 수정

> vi /usr/lib/systemd/system/jenkins.service  
>  
> #해당 부분을 찾아 원하는 포트로 수정  
> Environment="JENKINS_PORT=8099"   
>  
> # JAVA 설치 버전이 2개라면 아래 부분의 주석을 제거하고 위에서 복사한 경로로 수정  
> #Environment="JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64"  
> Environment="JAVA_HOME=/usr/lib/jvm/java-11-openjdk-11.0.16.1.1-1.el7_9.x86_64"  


### 1-7. 초기 어드민 패스워드 입력 

  - /var/lib/jenkins/secrets/initialAdminPassword 에서 확인

### 1-8. 플러그인 설치

  - 추천 플러그인 선택

### 1-9. 어드민 계정 정보 입력
  계정명 : admin
  암호 : m2~~~~~
  암호확인 : m2~~~~~
  이름 : m2m
  이메일주소 : m2m

## 빌드 및 배포설정  

<details>
  <summary>Exp.</summary>  
  <pre>

### 참조

# FrontOffice
```bash
npm install --force &&
npm run devbuild &&
pm2 stop -s front-end || :
pm2 delete -s front-end || :
set DEBUG=express:* & pm2 start npm --name front-end -- run start
```

# BackOffice
```bash
#!/bin/bash
echo "PID Check..."

CURRENT_PID=$(ps -ef | grep java | grep PlatformCommon* | awk '{print $2}')
JAR_PATH=/var/jenkins_home/workspace/Dev-PlatformCommon/build/libs/PlatformCommon-0.0.1-SNAPSHOT.jar

echo "Running PID: {$CURRENT_PID}"

if [ -z "$CURRENT_PID" ]
then
  echo "Project is not running"
else
  kill -15 $CURRENT_PID
  sleep 10
fi

echo "Deploy Project...."

nohup java -Dspring.profiles.active=m2mdev -jar $JAR_PATH >> /var/jenkins_home/workspace/nohup.out &

echo "Done"
```

  </pre>
</details>