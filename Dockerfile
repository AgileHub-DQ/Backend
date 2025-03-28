# Dockerfile
# gradle && jdk17 이미지 빌드 - build 별명
FROM --platform=linux/amd64 gradle:jdk17-jammy AS build

# 작업 디렉토리 /app 생성
WORKDIR /app

# 빌드하는데 필요한 build.gradle, settings.gradle 파일 현재 디렉토리로 복사
COPY build.gradle settings.gradle /app/
COPY gradle /app/gradle

# 그래들 파일이 변경되었을 때만 새롭게 의존패키지 다운로드 받게함.
# 의존성 다운로드
RUN gradle dependencies --no-daemon

# 소스코드파일 /app 작업 디렉토리로 복사
COPY . /app

# Gradle 빌드를 실행하여 JAR 파일 생성
RUN gradle build -x test --parallel

FROM --platform=linux/amd64 eclipse-temurin:21.0.4_7-jre-jammy

WORKDIR /app

# 빌드 이미지에서 생성된 JAR 파일을 런타임 이미지로 복사
COPY --from=build /app/build/libs/*.jar /app/agile.jar

EXPOSE 8080
EXPOSE 9010

ENTRYPOINT ["java"]
CMD ["-jar", \
    "-Dspring.profiles.active=prod", \
    "-Dcom.sun.management.jmxremote=true", \
    "-Dcom.sun.management.jmxremote.local.only=false", \
    "-Dcom.sun.management.jmxremote.port=9010", \
    "-Dcom.sun.management.jmxremote.rmi.port=9010", \
    "-Dcom.sun.management.jmxremote.ssl=false", \
    "-Dcom.sun.management.jmxremote.authenticate=false", \
    "-Djava.rmi.server.hostname=223.130.129.226", \
    "agile.jar"]
