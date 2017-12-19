FROM java
MAINTAINER yangjie.cao@yuancredit.com
WORKDIR /payservice
ADD /open-micro-service-demo.jar /payservice/
ADD /app.sh /payservice/
ENTRYPOINT ["java", "-Duser.timezone=GMT+08","-Xms1024m","-Xmx4096m", "-jar", "/payservice/open-mirco-service-demo.jar"]
#CMD ["--spring.profile.active=develop"]