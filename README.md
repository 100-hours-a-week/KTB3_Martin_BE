#  별 헤는밤

## Back-end 소개
- 일상에 지친 사람들이 모여서 얘기하는 커뮤니티 프로젝트입니다
- spring boot를 사용해서 api를 구현했습니다
- 세션을 통해 로그인을 구현했습니다.
- Spring Security를 통해 인증/인가를 구현했습니다.



---

## 개발 기간
 - 2025.10.12 ~ 2025.12.11

---

##  기능
- 회원가입 / 로그인 / 로그아웃
- 사용자 정보 조회 및 수정
- 게시글 CRUD
- 댓글 CRUD
- 좋아요 추가 / 취소
- 세션 기반 인증 (Spring Security)
- 글로벌 예외 처리 및 응답 포맷 통일

---

##  기술 스택
- **언어:** Java 21
- **프레임워크:** Spring Boot 3.5.x
- **DB:** H2
- **ORM:** JPA / Hibernate
- **인증/인가:** Spring Security (세션 기반)
- **테스트:** JUnit5, MockMvc
- **Other:** Swagger 


---
## ERD
<img width="1072" height="691" alt="image" src="https://github.com/user-attachments/assets/2c4b4f1a-e9e3-4cda-8dce-7dce31128be9" />

---


##  트러블 슈팅
- **순환 참조 문제**
  - Security Config에서 CustomAuthenticationProvider(이하 provider)를 필드에 의존성 주입시에 순환 참조 발생
  - 처음에 provider에 있는 필드가 문제인가 싶어 전부 주석처리뒤에 실행
    - 그럼에도 순환참조 발생
  - Config와 provider가 의존성이 연관된 것을 확인
    - provider의 생성자가 config를 의존하는 것을 알게됨
  - 해결 방안
    - @Lazy를 쓸까도 싶었지만 이건 본질적인 해결법이 아닌것 같아 보류
    - 메서드에 인자로 두어 spring container가 주입하게 함
      - 해결

- **세션에 유저 정보 미저장**
  - customUserDetail을 구현하우에 service로 생성한뒤에 provider로 넘김
  - 로그인시에 successHandler 동작 확인
  - 그러나 로그인 이후 유저정보에 접근시에 403발생
    - 아직 인가를 구현하지 않았기에 다른 문제라 생각됐음
    - 그러나 에러 메시지가 뜨지 않아 명확한 해결법 생각불가
  - handler에 getSession을 하게 되면 정상 작동함
    - 세션에 저장 문제라 판단됨
    - 일단 handler에서 session에 직접접근은 옳지 않기에 본질적 해결방안 모색
  - 구조 탐색
    - AbstractAuthenticationProcessingFilter에 있는 successfulAuthentcation에서 세션 저장
    - securityContextRepository에 saveContext로 context를 저장하는것으로 예상
    - SuccessHandler에서 Repository 존재 여부를 확인했으나 null로 확인
    - 이후 setSecurityContextRepository 메소드로 Repository를 지정 하는 것을 확인
    - CustomAuthenticationFilter에서 repository 지정 -> 해결
  

