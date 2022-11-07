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

#### 2개 이상의 잡이 존재할 때 특정 잡 실행하기
- **기존코드** : **환경변수**에서 특정 잡 지정
```
//application.yml

batch:
    job:
      names: ${job.name:makeRebateOrderItemJob}
```
```java
// JobScheduler.java

private final Job job;
```
- **수정코드**: **스케줄러**에서 특정 잡 지정
```java
// JobScheduler.java

private final Job makeRebateOrderItemJob;

    //@Scheduled(cron = "0 0 4 * * *") // 매일 새벽 4시 (성공 시 중복인수는 재실행X, 실패 시 다시 실행하도록)
    @Scheduled(cron = "1 * * * * *") // 매분 1초마다 테스트
    public void jobScheduler(){
        // 현재 날짜가 15일 이전이면 yearMonth를 두 달 전으로, 15일 이후면 한 달 전으로
        LocalDate today = LocalDate.now();
        today = today.isBefore(today.withDayOfMonth(15)) ? today.minusMonths(2) : today.minusMonths(1);
        String  yearMonth = today.format(DateTimeFormatter.ofPattern("YYYY-MM"));

        // jobParameters.yearMonth 생성
        JobParameters jobParameters = new JobParametersBuilder().addString("yearMonth", yearMonth).toJobParameters();

        try {
            JobExecution jobExecution = jobLauncher.run(makeRebateOrderItemJob, jobParameters);
            System.out.println("Job's Status:::"+jobExecution.getStatus());
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
                 | JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }
```
```java
// MakeRebateOrderItemJobConfig.java
    @Bean
    public Job makeRebateOrderItemJob(Step makeRebateOrderItemStep1, CommandLineRunner initData) throws Exception {
        initData.run();


        return jobBuilderFactory.get("makeRebateOrderItemJob")
                .start(makeRebateOrderItemStep1)
                .build();
    }
```
### 2. enum을 이용하여 이전 미션과제 리팩토링
AuthLevel 클래스와 CashLog.eventType을 enum을 이용하여 리팩토링 진행했습니다. enum을 디비 테이블에 저장할 때 컨버터와 `@enumerated`을 적용해보며 어떤 식으로 저장되는지 살펴봤습니다. <br>
특히 `CashLog.eventType`은 `EventGroup`(enum)과 `PayGroup`(enum)을 합쳐 단순 String 타입으로 테이블에 저장했는데, 합쳐서 단순 String타입으로 저장할 지라도 EventGroup과 PayGroup을 좀 더 깔끔하게 그룹화하기 위해 열거형으로 생성했습니다. <br>
또한 이전에 배웠던 `@Embeddable`/`@Embedded`와 차별적인 enum만의 특징을 찾아보며 적절한 곳에 enum을 사용하고자 했습니다.
- [우아한형제들 Java Enum 활용기](https://techblog.woowahan.com/2527/)
- [@Enumerated 사용법](https://tomee.apache.org/examples-trunk/jpa-enumerated/)
- [EnumSet](https://scshim.tistory.com/253) <br>
#### Q. EventType 자체를 enum으로 만들어서 EventGroup과 PayGroup을 리스트로 넣지 않은 이유? 
A. 우형 기술블로그의 경우 중복값 없이 그룹 안에 타입 리스트로 관리를 했지만, 저의 경우 중복값이 생기기 때문에 이게 맞나? 싶어서 진행을 안했는데 중복값이 생겨도 크게 상관은 없을 것 같아서 EventGroup과 PayGroup을 묶어서 하나의 enum으로 관리하는 쪽으로 리팩토링 생각중입니다. <br>
```java
// EventGroup.java
@Getter
public enum EventGroup {
    ORDER("주문"),
    REFUND("환불"),
    CHARGE("충전"),
    REBATE("정산"),
    WITHDRAW("출금");


    EventGroup(String value) {
        this.value = value;
    }

    private String value;

}
```
```java

// PayGroup.java

@Getter
public enum PayGroup {
    REMITTANCE("무통장입금"),
    CARD("카드"),
    CASH("예치금");

    private String value;

    PayGroup(String value){
        this.value = value;
    }
}
```
```java
// CashService.java

   public CashLog addCash(Member member, long price, EventGroup eventGroup, PayGroup payGroup, Order order) {
        CashLog cashLog = CashLog.builder()
                .price(price)
                .eventType(eventGroup.getValue() + "_" + payGroup.getValue())
                .order(order)
                .member(member)
                .build();

        cashLogRepository.save(cashLog);

        return cashLog;
    }
```
#### eventType EventGroup(enum) + PayGroup(enum)으로 바꾸기 전
<img width="621" alt="Untitled (1)" src="https://user-images.githubusercontent.com/63441091/199881189-2abeb26d-370a-43e3-ae64-e192e4ee9e76.png"> <br>

#### eventType EventGroup(enum) + PayGroup(enum)으로 바꾼 후
<img width="621" alt="Untitled" src="https://user-images.githubusercontent.com/63441091/199881172-a6319c79-eacb-494e-bf3d-ff4198384491.png">

### 3. Withdraw 엔티티
`Withdraw` 엔티티의 경우 `RebateOrderItem` 엔티티 대비 컬럼들이 적으나 출금신청폼 컬럼(은행명, 계좌번호, 출금 신청액) 외에 반드시 필요하다고 생각 드는 컬럼들은 추가적으로 넣어주었습니다.
<img width="978" alt="image" src="https://user-images.githubusercontent.com/63441091/199425234-c049efef-bc47-48e1-9a0e-1891f571fa49.png">
<img width="1165" alt="image" src="https://user-images.githubusercontent.com/63441091/199430338-86c59446-2993-4890-bc9e-9ffbd3678c62.png">


## II. 리팩토링
1. 중복 코드 제거
2. 정산이나 출금처리 완료 후 이전 페이지(리스트 페이지)에서 파라미터 저장하여 파라미터 보존한 채 이전페이지 redirect
3. 출금처리 취소(출금처리 가능여부(계좌나 출금금액 등) 판단) 추가하면서 예치금 차감 로직은 출금 신청 받은 직후로 이동
4. `CashLog`에서 FK가 무한정 늘어나지 않도록 FK들을 `relTypeCode`(관련 있는 FK(ex.Member, Order) Code), `relTypeId` 2개 컬럼으로 리팩토링
5. 취소 가능 시간, 판매자 정산비율 등은 설정파일(.yml)에서 가져올 수 있도록 리팩토링
6. String 유효성 검증은 단순 null 체크(? == null)로 검증하기 보다는 `StringUtils.hasText()` 로 null 체크 + 공백 제외한 길이가 1 이상인지 검증할 수 있도록 리팩토링 [참고자료](https://creampuffy.tistory.com/120)
7. 관리자 레이아웃 페이지 중복제거 -> 사용자와 관리자 공통 레이아웃을 생성하여 관리자와 사용자 레이아웃이 다중 상속되도록 + 관리자페이지에서는 footer 제거(사용자페이지일 때만 나오도록)

## III. 궁금한점
- 회원의 예치금 차감은 출금신청 받은 직후에 진행해야 하는지, 출금처리를 하면서 진행해야 하는지
  - 현재 코드처럼 출금처리할 때 예치금 차감을 하면 그 사이에 차감예정인 예치금을 사용하거나 다시 출금신청할 땐 어떻게 예외처리해야 할 지
