# 빌드 스테이지
FROM --platform=$BUILDPLATFORM gradle:7.6.1-jdk17 AS build

# 컨테이너 내부 /app 디렉토리를 기준 실행
WORKDIR /app

# 빌드 캐시를 위해 의존성 관련 파일만 먼저 복사
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

# 의존성 다운로드
RUN gradle dependencies --no-daemon

# 소스 코드 복사
COPY . /app

# 애플리케이션 빌드
RUN gradle clean build --no-daemon

# 실행 스테이지
FROM --platform=$TARGETPLATFORM openjdk:17-jdk-slim

# 컨테이너 내부 /app 디렉토리 기준
WORKDIR /app

# 빌드 스테이지에서 생성된 jar 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 환경 변수 파일 복사
COPY .env .env

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]