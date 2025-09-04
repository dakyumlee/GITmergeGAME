#!/bin/bash
echo "🎮 Git Merge Game 빠른 설정 시작..."

# Gradle Wrapper 권한 설정
if [ -f "gradlew" ]; then
    chmod +x gradlew
    echo "✅ gradlew 권한 설정 완료"
fi

# 기본 디렉토리 확인/생성
mkdir -p src/main/resources/static
mkdir -p src/main/java/com/gitmerge

# 간단한 테스트 페이지 생성
cat > src/main/resources/static/test.html << 'HTMLEOF'
<!DOCTYPE html>
<html>
<head><title>Test</title></head>
<body><h1>서버 동작 중! 🎮</h1></body>
</html>
HTMLEOF

echo "✅ 빠른 설정 완료!"
echo "이제 ./gradlew bootRun 실행하세요"
