package me.whiteship.java8to11.session;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * sequence of elements supporting sequential and parallel aggregate operations
 * 데이터를 담고 있는 저장소(컬렉션)이 아니다.
 * Functional in nature, 스트림이 처리하는 데이터 소스를 변경하지 않는다.
 * 스트림으로 처리하는 데이터는 오직 한 번만 처리한다.
 */
public class StreamSession {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        names.add("soul");
        names.add("gyu");
        names.add("sang");
        names.add("kim");

        System.out.println("스트림이 처리하는 데이터 소스를 변경하지 않는다.");
        Stream<Object> stringStream = names.stream().map(String::toUpperCase); // 이 결과는 또 다른 스트림이 되지, 기존의 names 스트림이 변경되지 않는다.
        names.forEach(System.out::println);
        stringStream.forEach(System.out::println);
        System.out.println("-----------------");

        names.stream().map((s) -> { // 여기 map 은 중개 오퍼레이션이다. stream 을 return 하는 오퍼레이션을 중개 오퍼레이션이라고 한다.
            System.out.println(s);
            /*
            왜 출력을 안할까? 여기서 중개 오퍼레이션이 lazy 하게 처리가 된다고 말할 수 있다.
            스트림 파이프라인을 정의해야 진행이 되는데, 스트림 파이프 라인이란 0 또는 다수의 중개 오퍼레이션과 한 개의 종료 오퍼레이션으로 구성된 파이프 라인이다.
            스트림의 데이터 소스는 오직 터미널 오퍼레이션을 실행할 때에만 처리한다.
            이 밑의 collect stream 처리 방식을 보면 스트림 파이프 라인을 알 수 있다.
             */
            return s.toUpperCase();
        });
//        names.forEach(System.out::println);
        System.out.println("-----------------");

        System.out.println("--stream 출력--");
        List<String> collect = names.stream().map((s) -> {
            System.out.println(s);
            return s.toUpperCase();
        }).collect(Collectors.toList()); // 여기 collect 는 종료 오퍼레이션이다. stream 을 return 하지 않는 오퍼레이션을 종료 오퍼레이션이라고 한다.
        System.out.println("--collect 출력--");
        collect.forEach(System.out::println);
        System.out.println("-----------------");

        System.out.println("병렬 처리");
        /*
        꼭 병렬 처리가, 멀티 스레딩이 좋은가요?
        아닙니다. 비용이 듭니다. 이렇게 쓰기보다는 그냥 스레드를 쓰시는게 좋습니다.
        물론 안의 처리할 내용이 다르기 때문에 이 parallelStream 으로 바꿨을 때 성능 향상이 있는지 확인하며 진행하면 좋습니다.
         */
        List<String> collectString = names.parallelStream().map((s) -> {
            System.out.println(s + "" + Thread.currentThread().getName());
            return s.toUpperCase();
        }).collect(Collectors.toList());
        collectString.forEach(System.out::println);
    }
}
