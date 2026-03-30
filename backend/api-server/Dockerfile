# 1. Base Image 설정 (Java 21 가벼운 버전)
FROM openjdk:21-jdk-slim

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. GitHub Actions에서 빌드된 api-server의 JAR 파일을 이미지로 복사
# 배포 스크립트(deploy.sh)나 Docker Compose 설정에 맞춰 JAR_FILE 경로를 유연하게 설정할 수 있습니다.
ARG JAR_FILE=api-server/build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 4. 운영 환경(prod) 프로파일 적용 및 실행
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]

# 5. 컨테이너가 사용할 포트 명시 (Spring Boot 기본 포트)
EXPOSE 8080