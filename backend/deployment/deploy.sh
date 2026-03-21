#!/bin/bash

# 1. 환경 설정
BASE_DIR="/home/ubuntu/backend-deploy"
DOCKER_DIR="$BASE_DIR/docker"
COMPOSE_FILE="$DOCKER_DIR/docker-compose.yml"
NGINX_CONF_DIR="$BASE_DIR/nginx"

# 도커 컴포즈 명령어 정의
DOCKER_COMPOSE="docker compose -f $COMPOSE_FILE"

echo "--- 🚀 배포 프로세스 시작 ---"
cd "$DOCKER_DIR"

#  필수 환경 변수 주입 확인
if [ -z "$BACKEND_IMAGE_TAG" ] || [ -z "$DOCKERHUB_USERNAME" ]; then
    echo "❌ 에러: 필수 환경 변수(BACKEND_IMAGE_TAG 등)가 설정되지 않았습니다."
    exit 1
fi
# docker-compose.yml.yml 파일 존재 확인
if [ ! -f "$COMPOSE_FILE" ]; then
    echo "❌ 에러: $COMPOSE_FILE 파일을 찾을 수 없습니다."
    exit 1
fi

# 3. Nginx 조각 파일(.inc) 존재 확인
if [ ! -f "$NGINX_CONF_DIR/be_blue.inc" ] || [ ! -f "$NGINX_CONF_DIR/be_green.inc" ]; then
    echo "❌ 경고: Nginx 설정 파일(.inc) 중 일부가 누락되었습니다."
    exit 1
fi
echo "✅ 사전 환경 검사 통과 (변수 및 설정 파일 확인 완료)!"

# 현재 실행 중인 컨테이너 확인 (Blue가 Up 상태인지)
IS_BLUE=$($DOCKER_COMPOSE ps | grep "backend-blue" | grep "running" || true)

if [ -z "$IS_BLUE" ]; then
    TARGET_COLOR="blue"
    TARGET_PORT=8081
    OLD_COLOR="green"
    INC_FILE="be_blue.inc"
else
    TARGET_COLOR="green"
    TARGET_PORT=8082
    OLD_COLOR="blue"
    INC_FILE="be_green.inc"
fi

echo "### 배포 타겟: $TARGET_COLOR (Port: $TARGET_PORT) ###"

# 1. 새 버전 이미지 가져오기
echo "1. $TARGET_COLOR 이미지 Pull..."
$DOCKER_COMPOSE pull backend-$TARGET_COLOR || exit 1

# 2. 새 컨테이너 실행
echo "2. $TARGET_COLOR 컨테이너 실행..."
$DOCKER_COMPOSE up -d backend-$TARGET_COLOR || exit 1

# 3. 헬스체크 (Spring Actuator 활용)
for i in {1..20}; do
    echo "3. $TARGET_COLOR 헬스체크 중... ($i/20)"
    sleep 5
    # 내부망(127.0.0.1)으로 헬스체크 시도
    RESPONSE=$(curl -s http://127.0.0.1:$TARGET_PORT/api/actuator/health | grep "UP" || true)
    if [ -n "$RESPONSE" ]; then
        echo "✅ 헬스체크 성공!"
        break
    fi
    if [ $i -eq 20 ]; then
        echo "❌ 헬스체크 실패! $TARGET_COLOR 배포를 중단하고 롤백합니다."
        $DOCKER_COMPOSE stop backend-$TARGET_COLOR
        exit 1
    fi
done

# 4. Nginx 설정 전환
echo "4. Nginx 설정 교체 및 Reload..."
if [ -f "$NGINX_CONF_DIR/$INC_FILE" ]; then
    sudo cp "$NGINX_CONF_DIR/$INC_FILE" /etc/nginx/conf.d/backend.inc
    # 설정 파일 문법 검사 후 리로드
    if sudo nginx -t; then
        sudo nginx -s reload
        echo "✅ Nginx 설정 로드 완료 ($TARGET_COLOR)"
    else
        echo "❌ Nginx 설정 오류! 배포를 중단합니다."
        exit 1
    fi
else
    echo "❌ 에러: $INC_FILE 파일을 찾을 수 없습니다."
    exit 1
fi

# 5. 구 버전 컨테이너 정리
echo "5. 이전 컨테이너($OLD_COLOR) 정리..."
$DOCKER_COMPOSE stop backend-$OLD_COLOR || true
$DOCKER_COMPOSE rm -f backend-$OLD_COLOR || true

echo "🎊 $TARGET_COLOR 배포 완료!"


