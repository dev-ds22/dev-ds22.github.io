---
layout: single
title: "[Jenkins] jenkins gitlab 연동 및 자동배포"
excerpt: "jenkins gitlab 연동 및 자동배포(CentOS)"

categories:
  - tech
tags:
  - [jenkins, gitlab]

toc: false
toc_sticky: true

date: 2023-01-25
last_modified_at: 2023-01-25
---

# jenkins gitlab 연동 및 자동배포

## 1. jenkins Web hook URL 확인 및 Token 생성

![inst gitlab](./../../images/tech/jenkins_gitlab_01.png)

- WebHook URL : http://pixxx.xxxx.com:8099/project/xxxxx-pub
- Trigger 이벤트 설정 : Push Events

![inst gitlab](./../../images/tech/jenkins_gitlab_02.png)

- Secret Token 생성 : b586c2195a13f8a94b91d1cfxxxxx

## 2. gitlab Web hook 설정


### 참고. Jenkins 유저로 전환

```bash
su - jenkins -s /bin/bash

sudo chown -R jenkins /usr/local/lib/node_modules/
sudo chown -R jenkins /usr/local/bin/
sudo chown -R jenkins /usr/local/share/
```

<details>
  <summary>Exp.</summary>  
  <pre>

### 참조

# FrontOffice
```bash
cd /usr/local/daiso-tomcat/vue/daiso-vue-pub
npm install --force &&
npm run build &&
pm2 stop -s daiso-pub || :
pm2 delete -s daiso-pub || :
set DEBUG=express:* & pm2 start npm --name daiso-pub -- run start
pm2 save || :
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