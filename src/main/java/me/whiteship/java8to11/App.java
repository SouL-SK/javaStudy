package me.whiteship.java8to11;

import me.whiteship.java8to11.dto.OnlineClass;
import me.whiteship.java8to11.session.Greeting;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class App {

    public static void main(String[] args) {
//        sessionOne();
//        sessionTwo();
//        sessionThree();
        sessionFour();
    }

    private static void sessionOne() {

        int size = 1500;
        int[] numbers = new int[size];
        Random random = new Random();

        IntStream.range(0, size).forEach(i -> numbers[i] = random.nextInt());
        long start = System.nanoTime();
        Arrays.sort(numbers);
        System.out.println("serial sorting took " + (System.nanoTime() - start));

        IntStream.range(0, size).forEach(i -> numbers[i] = random.nextInt());
        start = System.nanoTime();
        Arrays.parallelSort(numbers);
        System.out.println("parallel sorting took " + (System.nanoTime() - start));
    }

    private static void sessionTwo() {
        Greeting greeting = new Greeting();

//        UnaryOperator<String> hi = (s) -> "hi " + s; < 지금 이 코드는 Greeting의 hi()와 같은 코드이다. 이걸 메서르 레퍼런스를 사용해서 간결하게 표현할 수 있다.

        // static method reference
        UnaryOperator<String> hi = Greeting::hi;

        // 특정 객체의 instance method reference
        UnaryOperator<String> hello = greeting::hello;

        // constructor reference(constructor's return value is type) 이 참조는 첫 번째 생성자(no parameter)를 참조한다.
        Supplier<Greeting> newGreeting = Greeting::new; // there is not exist the greeting object
        Greeting greetingObj = newGreeting.get(); // here is existed the greeting obj


        // constructor reference 이 참조는 두 번째 생성자(have parameter)를 참조한다. ctl 누르면서 new 클릭 해보자.
        Function<String, Greeting> soulGreeting = Greeting::new;
        Greeting soul = soulGreeting.apply("soul");
        System.out.println(soul.getName());

        // 불특정 다수의 객체 instance method reference
        String[] names = {"soul", "gyu", "young", "jyeng"};
        Arrays.sort(names, String::compareToIgnoreCase); //compareToIgnoreCase는 instance method
        System.out.println(Arrays.toString(names));
//        Arrays.sort(names, new Comparator<String>() { Comparator가 함수형 인터페이스기 때문에 이 자리에 람다가 들어갈 수 있다.
//            @Override
//            public int compare(String o1, String o2) {
//                return 0;
//            }
//        });
    }

    public static void sessionThree() {
        InterfaceFoo foo = new InterfaceFooImpl("soul");
//        foo.printName();
//        foo.printNameUpperCase();
//        System.out.println("-------------");

        List<String> name = new ArrayList<>();
        name.add("soul");
        name.add("gyu");
        name.add("min");
        name.add("tae");

//        name.forEach(System.out::println);
//        System.out.println("-------------");
//
//        for (String n : name) {
//            System.out.println(n);
//        }

        Spliterator<String> spliterator = name.spliterator();
        Spliterator<String> spliterator1 = spliterator.trySplit();
        while (spliterator.tryAdvance(System.out::println)) ;
        System.out.println("-------------");
        while (spliterator1.tryAdvance(System.out::println)) ;

        System.out.println("-------------");
        name.removeIf(s -> s.startsWith("s"));
        name.forEach(System.out::println);
        System.out.println("-------------");
        name.sort(String::compareToIgnoreCase); // 문자열 순
        name.forEach(System.out::println);
        System.out.println("-------------");
        Comparator<String> comparatorToIgnoreCases = String::compareToIgnoreCase;
        name.sort(comparatorToIgnoreCases.reversed());
        name.forEach(System.out::println);
        System.out.println("-------------");
        name.stream().map(String::toUpperCase).filter(s -> s.startsWith("s")).collect(Collectors.toList());
        name.forEach(System.out::println);
        System.out.println("-------------");

    }

    public static void sessionFour() {
        List<OnlineClass> springClasses = new ArrayList<>();
        springClasses.add(new OnlineClass(1, "spring boot", true));
        springClasses.add(new OnlineClass(2, "spring data jpa", true));
        springClasses.add(new OnlineClass(3, "spring mvc", false));
        springClasses.add(new OnlineClass(4, "spring core", false));
        springClasses.add(new OnlineClass(5, "rest api development", false));

        List<OnlineClass> javaClasses = new ArrayList<>();
        javaClasses.add(new OnlineClass(6, "The Java, Test", true));
        javaClasses.add(new OnlineClass(7, "The Java, Code manipulation", true));
        javaClasses.add(new OnlineClass(8, "The Java, 8 to 11", false));

        List<List<OnlineClass>> myClassEvents = new ArrayList<>();
        myClassEvents.add(springClasses);
        myClassEvents.add(javaClasses);


        // TODO: Filter(Predicate)
        System.out.println("classes that start with \"spring\"");
        springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("spring"))
                .forEach(oc -> System.out.println(oc.getId()));
        System.out.println("-------------");
        System.out.println("classes that not closed");
        springClasses.stream()
                .filter(oc -> !oc.isClosed())
                .forEach(oc -> System.out.println(oc.getId()));
        System.out.println("더 단축시키기 (메서드 레퍼런스 사용)");
        springClasses.stream()
                .filter(Predicate.not(OnlineClass::isClosed))
                .forEach(oc -> System.out.println(oc.getId()));
        System.out.println("-------------");
        // TODO: Map(Function)
        System.out.println("Create a stream by collecting only class names");
        springClasses.stream()
                .map(OnlineClass::getTitle)
                .forEach(System.out::println);
        System.out.println("-------------");
        // TODO: FlatMap(Function)
        System.out.println("print all of the class titles in Two class lists");
//        springClasses.stream()
//                .map(OnlineClass::getTitle)
//                .forEach(System.out::println);
//        javaClasses.stream()
//                .map(OnlineClass::getTitle)
//                .forEach(System.out::println);
        /*
         flatMap 은 List 두 개를 담고 있는 myClassEvents 라는 collection 을 다룬다.
         먼저 이 collection 에서 나오는 리스트를 stream (컨베이어 벨트)로 보내어 각각의 list로 먼저 보낸다.
         그리고 그 list 들은 flatMap 안에서 처리가 되며 OnlineClass obj 로 나온다. ( 여기서 flatMap 은 중개 오퍼레이터)
         그래서 처음 들어갈 때 list 로 들어가서 OnlineClass type 으로 나오기 때문에 forEach 로 받아서 해당 타입을 그대로 getTitle 하면 된다.
         */
        myClassEvents.stream()
                .flatMap(list -> list.stream())
                .forEach(oc -> System.out.println(oc.getTitle()));
        System.out.println("-------------");
        // TODO: generate(Supplier) or Iterate(T seed, UnaryOperator)
        System.out.println("Among the unlimited streams that increase by 1 from 10, only up to 10 excluding the previous 10");
//        springClasses.stream()
//                .iterator(springClasses, )
//                .limit(10).forEach(oc -> oc.getTitle());
//        Stream.iterate(10, i -> i + 1); << 중개 오퍼레이션이라 터미널 오퍼레이션이 없으면 아무것도 하지 않는다. 출력을 하는 터미널 오퍼레이션, forEach를 넣어줘야 한다.
        Stream.iterate(10, i -> i + 1)
                .skip(10)
                .limit(10)
                .forEach(System.out::println);
        System.out.println("-------------");
        // TODO: anyMatch, allMatch, nonMatch
        System.out.println("Check if there is a class containing Test in Java class");
        boolean test = javaClasses.stream().anyMatch(oc -> oc.getTitle().contains("Test")); // boolean 이 return 되기 때문에 바로 종료 된다.
        System.out.println(test);
        System.out.println("-------------");

        System.out.println("During spring classes, collect only those with spring in the title and create a list");
        List<String> spring = springClasses.stream()
                .filter(oc -> oc.getTitle().contains("spring"))
                .map(OnlineClass::getTitle)
                .collect(Collectors.toList());

        System.out.println("map이 먼저 오는 경우: 어떤 오퍼레이터들은 그 오퍼레이터를 지나감과 동시에 타입이 바뀔 수 있어서 조심해야 한다.");
        List<String> spring2 = springClasses.stream()
                .map(OnlineClass::getTitle)
                .filter(t -> t.contains("spring"))
                .collect(Collectors.toList());
        spring.forEach(System.out::println);
        System.out.println("-------------");
        spring2.forEach(System.out::println);
        System.out.println("-------------");
    }
}
