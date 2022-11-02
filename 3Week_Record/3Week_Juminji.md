## Title: [3Week] 주민지

### 미션 요구사항 분석 & 체크리스트

---
#### 필수 기능
- [x] 관리자 생성 및 관리자 권한 추가 작업
- [x] 관리자 전용 페이지(+ layout, navbar)
- [x] 정산데이터 생성
- [x] 정산데이터 리스트
- [x] 건별정산처리
- [x] 전체정산처리
#### 추가 기능
- [x] 정산데이터를 배치로 생성
  - 스프링 내장 스케쥴러를 이용해서 배치가 매달 15일 새벽 4시에 실행되도록
  - `@EnableScheduling`과 `@Scheduled(cron="")` 이용
- [x] 출금신청기능(사용자기능)
- [x] 관리자용 출금 리스트 뷰
- [x] 출금처리기능(관리자기능)

### 3주차 미션 요약

---

##  I. 접근방법
### 1. 스프링 배치를 이용한 정산데이터 생성 스케줄링
매번 직접 다른 인수를 넣어 스프링 배치로 잡을 실행해보는 경험은 해봤으나, 이렇게 인수와 시간을 자동화하여 잡을 처리하는 방식은 아직 낯설어서 스케줄링 관련 자료를 찾아봤습니다. `@Scheduled(cron="")`을 이용하여 주기와 시간을 설정하고 `JobParameters`를 이용하여 정산일 기준 이전 달을 job 파라미터로 설정해주었습니다.
- [cron 주기설정](https://itworldyo.tistory.com/40)
- [Scheduler 예시](https://www.codeusingjava.com/boot/batch/scheduler)
<img width="1342" alt="image" src="https://user-images.githubusercontent.com/63441091/199423293-4e750ea6-9297-42af-a1bc-040cb69239d9.png">
<img width="776" alt="image" src="https://user-images.githubusercontent.com/63441091/199423557-d5696781-4dba-46c2-9a92-11f977a62cf4.png">


### 2. enum을 이용하여 이전 미션과제 리팩토링
AuthLevel 클래스와 CashLog.eventType을 enum을 이용하여 리팩토링 진행했습니다. enum을 디비 테이블에 저장할 때 컨버터와 `@enumerated`을 적용해보며 어떤 식으로 저장되는지 살펴봤습니다. <br>
특히 `CashLog.eventType`은 `EventGroup`(enum)과 `PayGroup`(enum)을 합쳐 단순 String 타입으로 테이블에 저장했는데, 합쳐서 단순 String타입으로 저장할 지라도 EventGroup과 PayGroup을 좀 더 깔끔하게 그룹화하기 위해 열거형으로 생성했습니다. <br>
또한 이전에 배웠던 `@Embeddable`/`@Embedded`와 차별적인 enum만의 특징을 찾아보며 적절한 곳에 enum을 사용하고자 했습니다.
- [우아한형제들 Java Enum 활용기](https://techblog.woowahan.com/2527/)
- [@Enumerated 사용법](https://tomee.apache.org/examples-trunk/jpa-enumerated/)
- [EnumSet](https://scshim.tistory.com/253)

### 3. Withdraw 엔티티
`Withdraw` 엔티티의 경우 현재는 건별 출금처리만 하고 있지만 후에 일괄처리도 하게 될 경우에 외래키 제약을 제거할 수도 있다는 가능성을 두고 만들었습니다. `RebateOrderItem` 엔티티 대비 컬럼들이 적으나 출금신청폼 컬럼(은행명, 계좌번호, 출금 신청액) 외에 반드시 필요하다고 생각 드는 컬럼들은 추가적으로 넣어주었습니다.
<img width="978" alt="image" src="https://user-images.githubusercontent.com/63441091/199425234-c049efef-bc47-48e1-9a0e-1891f571fa49.png">
<img width="1165" alt="image" src="https://user-images.githubusercontent.com/63441091/199430338-86c59446-2993-4890-bc9e-9ffbd3678c62.png">


## II. 리팩토링
1. 중복 코드 제거
2. 정산이나 출금처리 완료 후 이전 페이지(리스트 페이지)에서 파라미터 저장하여 파라미터 보존한 채 이전페이지 redirect

## III. 궁금한점
- 회원의 예치금 차감은 출금신청 받은 직후에 진행해야 하는지, 출금처리를 하면서 진행해야 하는지
  - 현재 코드처럼 출금처리할 때 예치금 차감을 하면 그 사이에 차감예정인 예치금을 사용하거나 다시 출금신청할 땐 어떻게 예외처리해야 할 지
