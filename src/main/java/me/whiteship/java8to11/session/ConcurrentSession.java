package me.whiteship.java8to11.session;

import org.springframework.aop.ThrowsAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ConcurrentSession {

    /**
     * 자바 Concurrent 프로그래밍 배경 지식
     * Concurrent software : 동시에 여러 작업을 할 수 있는 소프트웨어
     * 자바에서 지원하는 컨커런트 프로그래밍 : 멀티 프로세싱 (ProcessBuilder), 멀티 쓰레드
     * 자바 멀티 쓰레드 프로그래밍 : Thread / Runnable
     */
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        boolean throwError = true;

        CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> {
            if (throwError) {
                throw new IllegalStateException();
            }

            System.out.println("Hello " + Thread.currentThread().getName());
            return "Hello";
        }).handle((result, ex) -> {
            if (ex != null) {
                System.out.println(ex);
                return "ERROR!";
            }
            return result;
        });
//        runnableThread();
//        executorsEx();
//        callableFuture();
//        completableFuture1();
        completableFuture2();
    }


    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("Thread : " + Thread.currentThread().getName());
        }
    }

    static void runnableThread() throws InterruptedException {
        // 1번째 방법.
        MyThread myThread = new MyThread();
        myThread.start();

        System.out.println("myThread: " + Thread.currentThread().getName());

        // 2번째 방법. Method reference 또한 사용할 수 있다.
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("thread : " + Thread.currentThread().getName());
            }
        });
        thread.start();

        // 람다로 표현할 수도 있다.
        Thread thread1 = new Thread(() -> {
            while (true) {
                System.out.println("thread1 : " + Thread.currentThread().getName());
                try {
                    // 현재 쓰레드를 멈춘다. 다른 쓰레드가 처리할 수 있도록 기회를 주지만 그렇다고 락을 놔주진 않는다. (잘못하면 데드락 걸릴 수 있으니 주의)
                    Thread.sleep(1000L);
                } catch (InterruptedException e) {
                    // interrupt 를 했을 때 catch 에서 잡은 이후 return 이나 RuntimeException 계열 Exception 을 던져 종료시켜도 되고, 다른 일을 하게 해도 된다.
                    // 이 곳은 이 thread 가 sleep 상태일 때, 깨우면 접근할 수 있다. InterruptedException 이 발생하기 때문.
                    System.out.println("interrupted!!!");
                    throw new RuntimeException(e);
                }
            }
        });
        thread1.start();
        System.out.println("Hello: " + Thread.currentThread().getName());
        Thread.sleep(3000L);
        // interrupt 는 다른 쓰레드를 깨워서 InterruptedException 을 발생시킨다. 그 에러가 발생했을 때 할 일은 코딩하기 나름이다.
        thread1.interrupt();
        // join 을 쓰면 interrupt 랑 다르게 다른 쓰레드가 끝날 때까지 기다리고 진행한다.
    }

    /**
     * 고수준 (High-level) Concurrency programming
     * 쓰레드를 만들고 관리하는 작업을 애플리케이션에서 분리, 그런 기능을 Executors 에게 위임.
     * <p>
     * Executors 가 하는 일:
     * 쓰레드 만들기: 애플리케이션이 사용할 쓰레드 풀을 만들어 관리한다.
     * 쓰레드 관리: 쓰레드 생명 주기를 관리한다.
     * 작업 처리 및 실행: 쓰레드로 실행할 작업을 제공할 수 있는 API를 제공한다.
     * <p>
     * 주요 인터페이스
     * Executor: execute(Runnable)
     * ExecutorService: Executor 상속 받은 인터페이스로, Callable 도 실행할 수 있으며, Executor 를 종료시키거나, 여러 Callable 을 동시에 실행하는 등의 기능을 제공한다.
     * ScheduledExecutorService: ExecutorService 를 상속 받은 인터페이스로 특정 시간 이후에 또는 주기적으로 작업을 실행할 수 있다.
     */
    static void executorsEx() {

        // singleThread 라 1개의 쓰레드만 돌지만
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            System.out.println("Thread: " + Thread.currentThread().getName());
        });

        // 2개의 쓰레드로 여러 개의 태스크를 줄 수도 있다.
        ExecutorService executorService1 = Executors.newFixedThreadPool(2);
        executorService1.submit(() -> System.out.println(("Hello")));
        executorService1.submit(() -> System.out.println(("world")));
        executorService1.submit(() -> System.out.println(("soul")));
        executorService1.submit(() -> System.out.println(("gyu")));
        executorService1.submit(() -> System.out.println(("kim")));
        executorService1.submit(() -> System.out.println(("sang")));

        executorService1.shutdown();

        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(getRunnable("test for scheduleAtFixedRate"), 1, 2, TimeUnit.SECONDS);
        // Graceful Shutdown 은 shutdown, Hard shutdown 은 shutdownNow https://2kindsofcs.tistory.com/53
        executorService.shutdownNow();

        // 이 밖에도 fork/join framework 같은 멀티 프로세싱 애플리케이션을 사용할 때 유용한 구현체도 있다.
    }

    private static Runnable getRunnable(String message) {
        return () -> System.out.println(message + Thread.currentThread().getName());
    }

    /**
     * 16. Callable 과 Future
     * Callable 은 Runnable 과 유사하지만 작업의 결과를 받을 수 있다는 차이가 있다.
     * Runnable 의 run method 는 void 타입이라 결과를 반환하지 않는다.
     * Future 은 비동기적인 작업의 현태 상태를 조회하거나 결과를 가져올 수 있다.
     * Callable 로 받아론 결과를 Future 로 핸들링 할 수 있다.
     */
    public static void callableFuture() throws ExecutionException, InterruptedException {


//        ExecutorService executorService = Executors.newSingleThreadExecutor();
        ExecutorService executorService = Executors.newFixedThreadPool(4);

        Callable<String> hello = () -> {
            Thread.sleep(2000L);
            return "hello";
        };

        Callable<String> java = () -> {
            Thread.sleep(3000L);
            return "java";
        };

        Callable<String> soul = () -> {
            Thread.sleep(1000L);
            return "soul";
        };

        Future<String> helloFuture = executorService.submit(hello);
        System.out.println("Started!!!!!!");

        // 작업 상태를 확인할 수 있다. 완료 시 true, 아니면 false 를 리턴한다.
        System.out.println(helloFuture.isDone());

        // 블록킹 콜이다.
        helloFuture.get();

        // 작업 취소하기, 이 코드를 get() 보다 먼저 실행하면 오류가 난다. 이미 취소시킨 작업은 가져올 수 없다는 오류이다.
        helloFuture.cancel(false);

        // 작업 상태를 확인할 수 있다. 완료 시 true, 아니면 false 를 리턴한다.
        System.out.println(helloFuture.isDone());

        // 여러 작업을 동시에 실행하기, 동시에 실행한 작업 중에 제일 오래 걸리는 작업만큼 시간이 걸린다.
        List<Future<String>> futuresInvokeAll = executorService.invokeAll(Arrays.asList(hello, java, soul));

        for (Future<String> f : futuresInvokeAll) {
            System.out.println(f.get());
        }

        String s = executorService.invokeAny(Arrays.asList(hello, java, soul));
        System.out.println(s);
        System.out.println("End!!!!!!");
        executorService.shutdown();
    }

    /**
     * 17. CompletableFuture
     * 자바에서 비동기 (Asynchronous) 프로그래밍을 가능케 하는 인터페이스
     * Future 만으로 힘든 부분을 CompletableFuture 로 구현한다.
     */
    public static void completableFuture1() throws ExecutionException, InterruptedException {
        // 기존의 Future 를 사용했을 때의 단점,
        // - Future 를 외부에서 완료 시킬 수 없다. 취소하거나, get() 에 타임아웃을 설정할 수는 있다.
        // - 블로킹 코드 (get()) 을 사용하지 않고서는 작업이 끝났을 때 콜백을 실행할 수 없다.
        // - 여러 Future 를 조합할 수 있다. 예) Event 정보 가져온 다음 Event 에 참석하는 회원 목록 가져오기
        // - 예외 처리용 API 를 제공하지 않는다.
        ExecutorService executorService = Executors.newFixedThreadPool(4);
        Future<String> futureEx1 = executorService.submit(() -> "hello");

        // TODO
        futureEx1.get();

        // completableFuture 를 사용했을 때의 개선점,
        // 외부에서 Future 완료 시키기가 가능하다.

        // 생성 후에 complete 를 통해 future 완료 시키기 -- 정석 코드
        CompletableFuture<String> completableFutureEx1 = new CompletableFuture<>();
        completableFutureEx1.complete("soul is best!");
        // get() 이 없어지진 않는다. (블로킹 코드 후 값 가져오기)
        System.out.println(completableFutureEx1.get());
        System.out.println("-------------------------------------");

        // 생성 후에 complete 를 통해 future 완료 시키기 -- 간결화
        CompletableFuture<String> completableFutureEx2 = CompletableFuture.completedFuture("soul is great!");
        System.out.println(completableFutureEx2.get());
        System.out.println("-------------------------------------");

        // return 이 없는 경우
        CompletableFuture<Void> noReturnFuture = CompletableFuture.runAsync(() -> {
            System.out.println("Hello! There is no return values, good luck :) " + Thread.currentThread().getName() + "\n");
        });
        // get() 이나 join() 를 해야만 값이 출력된다.
        noReturnFuture.get();
        System.out.println("-------------------------------------");

        // return 이 있는 경우
        CompletableFuture<String> hasReturnFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("Hello! There is return values, thx :) " + Thread.currentThread().getName() + "\n");
            return "future is completed";
        });
        System.out.println(hasReturnFuture.get());

        System.out.println("-------------------thenApply(Function): 리턴값을 받아서 다른 값으로 바꾸는 콜백------------------");

        // 콜백의 유형 < thenApply(Function), thenAccept(Consumer), thenRun(Runnable) >
        // thenApply(Function): 리턴값을 받아서 다른 값으로 바꾸는 콜백, 리턴이 있다.
        CompletableFuture<String> callBackApply = CompletableFuture.supplyAsync(() -> {
            System.out.println("This is call back future you can make, when you use the completableFuture only." + Thread.currentThread().getName() + "\n");
            return "call back is ready";
        }).thenApply((s) -> {
            System.out.println(Thread.currentThread().getName());
            return s.toUpperCase();
        });

        callBackApply.get();
        System.out.println("-------------------thenAccept(Consumer): 리턴값을 또 다른 작업을 처리하는 콜백(리턴 없이)------------------");
        // thenAccept(Consumer): 리턴값을 또 다른 작업을 처리하는 콜백(리턴 없이)
        CompletableFuture<Void> callBackAccept = CompletableFuture.supplyAsync(() -> {
            System.out.println("This is call back future you can make, when you use the completableFuture only." + Thread.currentThread().getName() + "\n");
            return "call back is ready";
        }).thenAccept((s) -> { // 위의 작업이 끝난 결과를 가지고 또 다른 작업을 진행. 리턴은 없다. Consumer 가 들어온다.
            System.out.println(Thread.currentThread().getName());
            System.out.println(s.toUpperCase());
        });

        callBackAccept.get();
        System.out.println("-------------------thenRun(Runnable): 리턴값 받지 않고 다른 작업을 처리하는 콜백------------------");
        // thenRun(Runnable): 리턴값 받지 않고 다른 작업을 처리하는 콜백
        CompletableFuture<Void> callBackRun = CompletableFuture.supplyAsync(() -> {
            System.out.println("This is call back future you can make, when you use the completableFuture only." + Thread.currentThread().getName() + "\n");
            return "call back is ready";
        }).thenRun(() -> { // 리턴은 필요없고, 작업을 하는 것이 중요하다. (결과값을 참고도 못한다.)
            System.out.println(Thread.currentThread().getName());
        });

        callBackRun.get();
        System.out.println("-------------------------------------");

        // Thread.currentThread().getName() 을 해보면 현재 Executor(ThreadPool) 을 알 수 있다. ThreadPool 바꿀 수 있다.
        // 콜백 자체를 또 다른 쓰레드에서 실행할 수 있다.
        // Default ThreadPool: ForkJoinPool.commonPool()
        ExecutorService executorService1 = Executors.newFixedThreadPool(8);
        System.out.println("Before) current Thread Pool: " + Thread.currentThread().getName());
        CompletableFuture<Void> executorChange = CompletableFuture.supplyAsync(() -> {
            System.out.println("Executor changing!! currentThread is " + Thread.currentThread().getName() + "\n");
            return "Executor changed!!";
        }, executorService1).thenRun(() -> { // 여기서 Executor 변경 적용
            System.out.println("After) current Thread Pool: " + Thread.currentThread().getName());
        });

        executorChange.get();
        System.out.println("-------------------------------------");
    }

    /**
     * 18. CompletableFuture 2
     */
    public static void completableFuture2() throws InterruptedException, ExecutionException {

        CompletableFuture<String> world = CompletableFuture.supplyAsync(() -> {
            System.out.println("World " + Thread.currentThread().getName());
            return "World";
        });
        CompletableFuture<String> hello = CompletableFuture.supplyAsync(() -> {
            System.out.println("Hello " + Thread.currentThread().getName());
            return "Hello";
        });


        System.out.println("CompletableFuture 조합하기");
        System.out.println("thenCompose(): 두 작업이 서로 이어서 실행하도록 조합");
        System.out.println("hello 작업 뒤에 다른 작업이 진행되도록 할 수 있다. 물론 서로 연관관계가 있어야 한다.");
        // hello.thenCompose(s -> getWorld(s)); < 이 작업에서 getWorld를 메서드 레퍼런스 방식으로 변경
        CompletableFuture<String> futureCompose = hello.thenCompose(ConcurrentSession::getWorld);
        System.out.println(futureCompose.get());

        System.out.println("thenCombine(): 두 작업을 독립적으로 실행하고 둘 다 종료했을 때 콜백 실행. 즉, 둘이 연관관계가 없지만 비동기적으로 진행하도록 하고 둘 다 작업 결과가 나왔을 때 get.");
        CompletableFuture<String> futureCombine = hello.thenCombine(world, (h, w) -> h + " " + w);
        System.out.println(futureCombine.get());


        System.out.println("allOf(): 여러 작업을 모두 실행하고 모든 작업 결과에 콜백 실행");
        CompletableFuture<Void> futureAllOf = CompletableFuture.allOf(hello, world)
                .thenAccept(System.out::println);
        System.out.println(futureAllOf.get() + "\n이 allOf 는 null 을 반환한다. 제대로 받는 방법은 아래와 같다.");

        System.out.println("task 로 받을 모든 것을 list에 넣어둔다");
        List<CompletableFuture<String>> futures = Arrays.asList(hello, world);
        CompletableFuture[] futureAllOfs = futures.toArray(new CompletableFuture[futures.size()]);
        CompletableFuture<List<String>> futureReturn = CompletableFuture.allOf(futureAllOfs)
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
        System.out.println("결과를 전부 받는다.");

        futureReturn.get().forEach(System.out::println);

        System.out.println("------anyOf------");
        System.out.println("anyOf(): 여러 작업 중에 가장 빨리 끝난 하나의 결과에 콜백 실행");
        CompletableFuture<Void> futureAnyOf = CompletableFuture.anyOf(world, hello).thenAccept(System.out::println);
        futureAnyOf.get();


        System.out.println("예외처리");
        System.out.println("exceptionally(Function)");

        System.out.println("handle(BiFunction)");
    }

    private static CompletableFuture<String> getWorld(String message) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("World " + Thread.currentThread().getName());
            return message + "world";
        });
    }

    /**
     * 다음에 공부하면 좋은 것들
     * fork - join framework
     * flow api
     */
}
