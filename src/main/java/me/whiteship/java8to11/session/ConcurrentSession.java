package me.whiteship.java8to11.session;

import org.springframework.aop.ThrowsAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class ConcurrentSession {
    /**
     * 자바 Concurrent 프로그래밍 배경 지식
     * Concurrent software : 동시에 여러 작업을 할 수 있는 소프트웨어
     * 자바에서 지원하는 컨커런트 프로그래밍 : 멀티 프로세싱 (ProcessBuilder), 멀티 쓰레드
     * 자바 멀티 쓰레드 프로그래밍 : Thread / Runnable
     */

    public static void main(String[] args) throws InterruptedException, ExecutionException {
//        runnableThread();
//        executorsEx();
        callableFuture();
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

    static void executorsEx() {
        /**
         * 고수준 (High-level) Concurrency programming
         * 쓰레드를 만들고 관리하는 작업을 애플리케이션에서 분리, 그런 기능을 Executors 에게 위임.
         *
         * Executors 가 하는 일:
         *   쓰레드 만들기: 애플리케이션이 사용할 쓰레드 풀을 만들어 관리한다.
         *   쓰레드 관리: 쓰레드 생명 주기를 관리한다.
         *   작업 처리 및 실행: 쓰레드로 실행할 작업을 제공할 수 있는 API를 제공한다.
         *
         * 주요 인터페이스
         *   Executor: execute(Runnable)
         *   ExecutorService: Executor 상속 받은 인터페이스로, Callable 도 실행할 수 있으며, Executor 를 종료시키거나, 여러 Callable 을 동시에 실행하는 등의 기능을 제공한다.
         *   ScheduledExecutorService: ExecutorService 를 상속 받은 인터페이스로 특정 시간 이후에 또는 주기적으로 작업을 실행할 수 있다.
         */

        // singleThread 라 1개의 쓰레드만 돌지만
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() ->{
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

    public static void callableFuture() throws ExecutionException, InterruptedException {

        /**
         * Callable 은 Runnable 과 유사하지만 작업의 결과를 받을 수 있다는 차이가 있다.
         * Runnable 의 run method 는 void 타입이라 결과를 반환하지 않는다.
         * Future 은 비동기적인 작업의 현태 상태를 조회하거나 결과를 가져올 수 있다.
         * Callable 로 받아론 결과를 Future 로 핸들링 할 수 있다.
         */

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
}
