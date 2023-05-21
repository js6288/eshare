FROM java:8u111-jdk
ENV LANG C.UTF-8
ADD ./target/eshare-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar","app.jar","-Dfile.encoding=utf-8"]
EXPOSE 9001

