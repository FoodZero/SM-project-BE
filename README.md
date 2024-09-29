# SM Project Backend

이 프로젝트는 **Spring Boot** 기반의 백엔드 애플리케이션입니다.

## 주요 기능
1. **회원 가입 및 로그인**
2. **냉장고 관리 기능**
3. **식재료 관리 기능**
4. **OCR을 활용한 영수증 스캔으로 냉장고 식재료 등록 기능**
5. **냉장고 공유 기능**
6. **레시피 검색 기능**
7. **AI 레시피 추천 기능**
8. **레시피 북마크 및 추천 기능**
9. **커뮤니티 게시글 관리**
10. **커뮤니티 게시글 댓글 기능**
11. **위치 등록 기능**
12. **앱 푸시 알림 기능**

## 데이터베이스 구조
- **member**: 회원 정보 저장.
- **member_refrigerator**: 회원-냉장고 관계 저장.
- **refrigerator**: 냉장고 정보 저장.
- **food**: 식재료 저장.
- **recommend**: 레시피 추천 저장.
- **recipe**: 레시피 저장.
- **bookmark**: 레시피 북마크 저장.
- **fcm_token**: firebase 토큰 저장.
- **member_password**: 회원 계정 비밀번호 저장.
- **receipt_image**: 영수증 저장.
- **post**: 커뮤니티 게시글 저장.
- **comment**: 커뮤니티 댓글 저장.
- **location**: 회원 위치 데이터 저장

## 기술 스택
- **Spring Boot**
- **Spring Security**: JWT 인증.
- **Redis**: 인증 코드 관리.
- **MySQL**: 데이터베이스.
- **JPA**: 데이터베이스 처리.
- **Gradle**: 빌드 도구.

## 

## 설정 방법
`application.yml` 또는 `application.properties`에 데이터베이스 및 Redis 정보를 설정합니다.

## 실행 방법
```bash
$ ./gradlew build
$ ./gradlew bootRun
