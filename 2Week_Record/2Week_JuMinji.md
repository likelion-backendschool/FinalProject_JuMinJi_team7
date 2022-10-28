## Title: [2Week] 주민지

### 미션 요구사항 분석 & 체크리스트

---
#### 필수 기능

- [ ] 장바구니에 상품 추가
- [ ] 장바구니 개별상품 삭제
- [ ] 장바구니 상품 전체 주문
- [ ] 주문 후 미결제 상태 시에만 주문 취소
- [ ] 예치금 서비스
- [ ] 예치금만으로 결제
- [ ] 카드만으로 결제 (PG연동)
- [ ] 예치금+카드 결제 (PG연동)
- [ ] 카드 주문
- [ ] 주문 목록 페이지
- [ ] 결제 후 myBook에 도서상품 추가

#### 추가 기능
- [ ] 주문 후 10분 이내에만 환불 가능


### 2주차 미션 요약

---

## I. 구현하기 어려웠던 기능 접근방법
#### 1.장바구니 품목 선택 삭제
이 기능은 컨트롤러, 서비스, 레포지토리 뿐만 아니라 프론트인 타임리프에서도 처리를 해줬어야 됐기 때문에 구현 전, 그리고 구현하면서 자바스크립트(제이쿼리)에 대해서 공부를 했습니다. <br>
- 자바스크립트 `map()` 메서드 : 적용 요소의 인덱스와 값을 자동으로 받아서 `해당 요소값을 해당 요소의 인덱스만큼 반복`하는 반복문 (https://mjn5027.tistory.com/80)
```java
 <input onchange="CartItemCheckbox__changed();" type="checkbox" class="cartItemCheckbox checkbox" th:value="${cartItem.id}">

 <script>
 const $checked = $('.cartItemCheckbox:checked');
 // $checked 요소를 el에 담아 index만큼 $checked 요소의 value값(cartItem.id)를 변수ids에 저장    
 const ids = $checked.map((index, el) => $(el).val()).get();
    form.ids.value = ids;
 </script>
```
- `name`과 `value`의 차이, `input box`를 `type="hidden"`으로 숨겨서 `form` 내의 데이터 서버로 전송하기 <br> (https://greeenhong.tistory.com/291?category=989109)
```java
<a href="javascript:;" onclick="RemoveCartItemsForm__submit();" class="btn btn-outline-secondary btn-sm py-2 px-4">
  <span class="ml-1" style="font-size:15px;">선택삭제</span>
 </a>
<form method="POST" name="removeCartItemsForm" th:action="@{|/cart/remove|}" hidden>
  <input type="hidden" name="ids">
</form>

<script>
  function RemoveCartItemsForm__submit() {
    // 선택삭제 폼을 새로 생성한 form 변수에 대입
    const form = document.removeCartItemsForm;
    // (carItemCheckbox 클래스명들을 가진) 체크박스 요소들 중에 checked가 된 요소를 %checked 변수에 대입
    const $checked = $('.cartItemCheckbox:checked');

    // 체크한 요소들이 없을 시 알림창
    if ($checked.length == 0) {
      alert('삭제할 장바구니 품목을 선택해주세요.');
      // form submit
      return;
    }
  </script>
```
#### 2. (PG 결제+ 보유 예치금)에서 보유예치금을 제외한 값을 PG 결제값으로 넘기기
[강사님 음원결제 강의자료](https://wiken.io/ken/10764) 를 참고하여 보유예치금을 기입하는 input box value값을 id로 가져와서 토스페이먼츠 결제값 파라미터에 (주문 결제금액) - (예치금 사용금액) 대입하는 로직을 적용해주었습니다 <br>
(설명 편의상 보유예치금을 보여주는 부분과 예치금 전액사용 체크박스 코드는 제외하고 가져왔습니다)
```java
// maxUseRestCash 타임리프 변수 생성 -> 삼항연산자 비교하여 값 들어가도록
<div th:with="maxUseRestCash = ${order.calculatePayPrice > restCash ? restCash : order.calculatePayPrice}">
  <span>사용 예치금 : </span>
  <input type="number" 
    id="PaymentForm__useRestCash"
    // 디폴트값이 0으로 설정되어 사용자가 사용예치금 미입력 시에도 다음 단계로 넘어가지 않는 일이 없도록
    value="0" 
    th:placeholder="|0 ~ ${maxUseRestCash}|"
    class="input input-bordered min-w-[300px]" min="0" th:max="${maxUseRestCash}"
    onkeydown="PaymentForm__useRestCashFix();"
    onkeyup="PaymentForm__useRestCashFix();"
    >
</div>

<script>
    // 서버에서 모델에 담아 타임리프로 전송한 데이터 ${order.calculatePayPrice} 값을 변수 orderPayPrice에 저장
    const orderPayPrice = /*[[ ${order.calculatePayPrice} ]]*/ null;

    // PaymentForm__useRestCash 아이디 가진 요소 값을 변수에 저장
    const $PaymentForm__useRestCash = $("#PaymentForm__useRestCash");
    
    // 결제 버튼을 클릭하면 작동하는 토스페이먼츠 결제진행 메서드
    function payment() {
    ..
   let useRestCash = parseInt($PaymentForm__useRestCash.val());
   ..
   // 토스페이먼츠 필수 파라미터에 값을 대입
    const paymentData = {
     amount: orderPayPrice - useRestCash,
       ..
      }
</script>
```
## II. 구현 중 생긴 이슈

#### 1. 토스페이먼츠 필수 파라미터 누락 문제
토스페이먼츠가 다음 단계로 안넘어가 console창을 확인해보니 필수 파라미터가 누락되었다며 400 에러가 뜨는 경우가 있었습니다.
1-1. 자바스크립트에 타임리프 변수가 적용되지 않았던 문제 <br>
자바스크립트 선언문에 th:inline="javascript"을 추가해주지 않아 타임리프 변수가 제대로 적용되지 않았고 아래 stackOverFlow 사이트를 참고하여 해결할 수 있었습니다.
(https://stackoverflow.com/questions/25687816/setting-up-a-javascript-variable-from-spring-model-by-using-thymeleaf)
1-2.

#### 2. `.gitignore`에 추가해야 하는 파일을 추가하지 않아 원격저장소에 올라갔던 문제
뒤늦게 `.gitignore`에 추가 후 원격저장소에서 브랜치를 삭제하고 다시 push해도 브랜치에 여전히 해당 파일이 남아있는 문제가 생겼었습니다.
https://inpa.tistory.com/entry/GIT-%E2%9A%A1%EF%B8%8F-Github%EC%97%90-%EC%9E%98%EB%AA%BB-%EC%98%AC%EB%9D%BC%EA%B0%84-%ED%8C%8C%EC%9D%BC-%EC%82%AD%EC%A0%9C%ED%95%98%EA%B8%B0 를 참고하여 프로젝트 폴더의 git에 들어가서 파일 삭제 작업 진행

#### 3. @Valid를 통한 폼 데이터 유효성 검사 시에 숫자형의 경우 @NotEmpty와 @NotBlank로 검사 시 오류 났던 문제
|  @NotBlank      |  @NotEmpty |@NotNull|
|-----------------|------------|--------|
|null, "", " " 불가|null, "" 불가|null 불가|

https://sanghye.tistory.com/36


## III. 개발 목표

#### 1.모듈화
메서드별로 최대한 단일 기능을 가질 수 있도록 모듈화에 집중했습니다.
특히 DB관련 로직의 경우 간단한 set() 등이어도 @Transactional을 통해 서비스단에서 한번에 관리될 수 있도록 최대한 독립적으로 메서드를 작성하려고 노력했습니다.
다만 아직까지는 어느정도까지 독립적으로 만들어야 하며, 어느 정도까지 추상적으로 만들어야 하는지 기준이 분명하게 잡히지 않아 아직 더 많은 경험이 필요한 것 같습니다.

#### 2.가독성
제 3자의 입장에서 봐도 해당 메서드가 어떤 기능을 하는지 알 수 있도록 최대한 메서드명, 변수명에 신경 썼습니다.
```java
// Order.java
    public boolean isRefundable() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        if ( isPaid == false ) return false;
        if ( isCanceled ) return false;
        if ( isRefunded ) return false;
        if( currentDateTime.isAfter(payDate.plusMinutes(10))) return false;

        return true;
    }
    
// OrderController.java
    @PostMapping("/{id}/refund")
    @PreAuthorize("isAuthenticated()")
    public String refund(@PathVariable long id, @AuthenticationPrincipal MemberContext memberContext) {
      ..
        if(order.isRefundable() == false) {
            throw new OrderCannotBeRefundedException("해당 주문은 환불 불가능합니다.");
        }

       ..
    }
``` 
각 조건들을 isRefundable()이라는 하나의 메서드로 생성하여 환불처리 관련 로직 시 환불이 가능한지 체크하는 부분에서 어떤 기능을 하는지 직관적으로 들어올 수 있게끔 해주었습니다.


## IV. 아쉬운 점
#### 테스트
아직 빠른 시간 안에 테스트코드를 짤 만큼 JUnit이 익숙하지 않아 코드 테스트 대신 사이트 접속으로 테스트를 여러 번 진행했습니다.
실제로 사이트 접속 후 테스트한 결과는 잘 작동했는데, 테스트코드에서의 권한 문제로 인해 테스트는 실패하는 경우가 종종 있었기 때문입니다.
앞으로의 개발 과정에서는 이런 테스트코드 작성에 더 익숙해져야 할 것 같습니다.


## V. Refactoring
- 아직은 장바구니 전체 주문만 구현한 상태지만, 장바구니 선택 삭제처럼 장바구니 선택 주문도 가능하도록 리팩토링 예정입니다.
- Order에서 OrderItem을 가져오거나 Product에서 HashTag를 가져오는 등 특정 배열(테이블)에 속한 특정 요소(칼럼)들을 가져오는 스트림문을 가독성을 높이도록 좀 더 깔끔하게 리팩토링 예정입니다.
- 중복 코드 제거
- 기능 별 분리
