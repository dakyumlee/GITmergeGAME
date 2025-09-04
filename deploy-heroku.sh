#!/bin/bash

echo "🚀 Git Merge Game 헤로쿠 배포 시작!"
echo ""

# Heroku CLI 설치 확인
if ! command -v heroku &> /dev/null; then
    echo "❌ Heroku CLI가 설치되지 않았습니다."
    echo "다음 명령어로 설치하세요:"
    echo "brew tap heroku/brew && brew install heroku"
    exit 1
fi

# 앱 이름 입력받기
echo "🎮 헤로쿠 앱 이름을 입력하세요 (예: my-gitmerge-game):"
read -p "앱 이름: " APP_NAME

if [ -z "$APP_NAME" ]; then
    echo "❌ 앱 이름을 입력해주세요."
    exit 1
fi

echo ""
echo "📝 설정 내용:"
echo "앱 이름: $APP_NAME"
echo "URL: https://$APP_NAME.herokuapp.com"
echo ""
read -p "계속하시겠습니까? (y/N): " -n 1 -r
echo ""

if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "❌ 배포가 취소되었습니다."
    exit 1
fi

echo ""
echo "🔨 빌드 중..."
./gradlew build

echo "🔐 헤로쿠 로그인..."
heroku auth:whoami > /dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "헤로쿠에 로그인해주세요:"
    heroku login
fi

echo "📱 헤로쿠 앱 생성 중..."
heroku create $APP_NAME

echo "🗄️ PostgreSQL 애드온 추가 중..."
heroku addons:create heroku-postgresql:essential-0 -a $APP_NAME

echo "⚙️ 환경 변수 설정 중..."
heroku config:set SPRING_PROFILES_ACTIVE=heroku -a $APP_NAME

echo "📤 Git 배포 중..."
git add .
git commit -m "Deploy to Heroku"
git push heroku main

echo ""
echo "🎉 배포 완료!"
echo "🌐 앱 URL: https://$APP_NAME.herokuapp.com"
heroku open -a $APP_NAME
