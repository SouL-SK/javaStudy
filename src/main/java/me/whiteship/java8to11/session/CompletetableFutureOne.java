package me.whiteship.java8to11.session;

import java.util.concurrent.*;
import java.util.concurrent.CompletableFuture;

public class CompleteableFutureOne {

    /**
     * 17. CompletableFuture
     * 자바에서 비동기 (Asynchronous) 프로그래밍을 가능케 하는 인터페이스
     * Future 만으로 힘든 부분을 CompletableFuture 로 구현한다.
     */

    public static void main(String[] args) throws ExecutionException, InterruptException {
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Future<String> futureEx1 = executorService.submit(() -> "hello");

        // TODO
        futureEx1.get();

        // 기존의 Future 를 사용했을 때의 단점,
        // - Future 를 외부에서 완료 시킬 수 없다. 취소하거나, get() 에 타임아웃을 설정할 수는 있다.
        // - 블로킹 코드 (get()) 을 사용하지 않고서는 작업이 끝났을 때 콜백을 실행할 수 없다.
        // - 여러 Future 를 조합할 수 있다. 예) Event 정보 가져온 다음 Event 에 참석하는 회원 목록 가져오기
        // - 예외 처리용 API 를 제공하지 않는다.

        CompleteableFuture<String> completableFutureEx1 = new CompleteableFuture<>();
        completableFutureEx1.complete("soul");
        System.out.println(completableFutureEx1.get());
    }

}