#!/bin/bash

# 1. 환경 설정 (경로 확인 필수)
BASE_DIR="/home/ubuntu/backend-deploy"
DOCKER_DIR="$BASE_DIR/docker"  # 기존 구조에 맞춰 조정
COMPOSE_FILE="$DOCKER_DIR/docker-compose.yml"
NGINX_CONF_DIR="$BASE_DIR/nginx"

# 도커 컴포즈 명령어 정의
DOCKER_COMPOSE="docker compose -f $COMPOSE_FILE"

echo "--- 🚀 멀티모듈(api-server) 배포 프로세스 시작 ---"
cd "$DOCKER_DIR"

# 2. 필수 환경 변수 주입 확인
# GitHub Actions에서 넘겨준 태그와 사용자명이 있는지 확인합니다.
if [ -z "$DOCKER_IMAGE_TAG" ] || [ -z "$DOCKERHUB_USERNAME" ]; then
    echo "❌ 에러: 필수 환경 변수(DOCKER_IMAGE_TAG 등)가 설정되지 않았습니다."
    exit 1
fi

if [ ! -f "$COMPOSE_FILE" ]; then
    echo "❌ 에러: $COMPOSE_FILE 파일을 찾을 수 없습니다."
    exit 1
fi

# 3. Nginx 조각 파일(.inc) 존재 확인
if [ ! -f "$NGINX_CONF_DIR/be_blue.inc" ] || [ ! -f "$NGINX_CONF_DIR/be_green.inc" ]; then
    echo "❌ 에러: Nginx 설정 파일(.inc)이 $NGINX_CONF_DIR 에 없습니다."
    exit 1
fi

echo "✅ 사전 환경 검사 통과!"

# 4. 현재 실행 중인 컨테이너 확인 (Blue/Green 결정)
# backend-blue 컨테이너가 실행 중인지 확인하여 타겟 결정
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
echo "1. $TARGET_COLOR 이미지 Pull (Tag: $DOCKER_IMAGE_TAG)..."
$DOCKER_COMPOSE pull backend-$TARGET_COLOR || exit 1

# 2. 새 컨테이너 실행
echo "2. $TARGET_COLOR 컨테이너 실행..."
$DOCKER_COMPOSE up -d backend-$TARGET_COLOR || exit 1

# 3. 헬스체크 (Spring Actuator 활용)
# 중요: api-server의 context-path나 actuator 경로가 맞는지 확인하세요.
for i in {1..20}; do
    echo "3. $TARGET_COLOR 헬스체크 중... ($i/20)"
    sleep 7 # 멀티모듈은 초기 구동 시간이 조금 더 걸릴 수 있어 간격을 넓혔습니다.

    # 헬스체크 경로 주의: /api/actuator/health 또는 /actuator/health
    RESPONSE=$(curl -s http://127.0.0.1:$TARGET_PORT/actuator/health | grep "UP" || true)

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

# 4. Nginx 설정 전환 (Downtime 최소화)
echo "4. Nginx 설정 교체 및 Reload..."
if [ -f "$NGINX_CONF_DIR/$INC_FILE" ]; then
    # /etc/nginx/conf.d/backend.inc를 직접 참조하는 구조여야 합니다.
    sudo cp "$NGINX_CONF_DIR/$INC_FILE" /etc/nginx/conf.d/backend.inc

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
# 바로 지우지 않고 30초 정도 유예를 두어 기존 연결을 처리하게 할 수도 있습니다.
$DOCKER_COMPOSE stop backend-$OLD_COLOR || true
$DOCKER_COMPOSE rm -f backend-$OLD_COLOR || true

echo "🎊 LocalMate(api-server) $TARGET_COLOR 배포 완료!"