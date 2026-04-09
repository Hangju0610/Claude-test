### 목표
TODO List를 위한 Server를 구현한다.

- git이 init이 되어있지 않다면, init을 한다.
- git에 pr을 작성하면서 코드 수정 및 개발하도록 한다.
- main, dev, feature로 git flow를 구성한다.

### 사용 기술 스택
기술 스택은 최소한으로 한다.
기술 : Java 22, Spring Boot 3.3.0, JPA, Gradle, Bean Validation (spring-boot-starter-validation), swagger
front : thymeleaf
DB : Local 환경에서는 H2 database를 사용한다.

### 아키텍처 구조
- Controller -> Service -> Repository -> Entity 레이어 구조를 따른다.

### 구현 기능
1. TODO List CRUD 구현
2. 회원 로직 개발 (회원가입, 회원 별 List 기능 구현)