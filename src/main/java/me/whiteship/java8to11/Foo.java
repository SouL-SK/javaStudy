package me.whiteship.java8to11;

import java.util.function.*;

/**
 * this class is practice for function class in java util
 */
public class Foo {

    public static void main(String[] args) {
        /*
        lambda expression : (argument list) -> { body }

        argument list:
        - do not have values : ()
        - have one value : (A) or A
        - have several values : (A, B)
        - The type of the argument may be omitted, inferred by the compiler, but may be specified.
        (Integer A, Integer B)
         */

        Function<Integer, Integer> plus10 = (i) -> i + 10;
        Function<Integer, Integer> multiply2 = (i) -> i * 2;
        System.out.println(plus10.apply(1));
        System.out.println(multiply2.apply(2));

        /*
        multiply2를 먼저 한 뒤, plus10을 한다.
        plus10 = i + 10
        multiply2 = i * 2
        i = 2
        ---
        1) (i + 10).compose((2 * 2)) = (i + 10).apply(4)
        2) 4 + 10 = 14
        */
        System.out.println(plus10.compose(multiply2).apply(2));

        /*
        plus10을 먼저 한 뒤, multiply2를 한다.
        plus10 = i + 10
        multiply2 = i * 2
        i = 2
        ---
        1) (2 + 10).andThen((i * 2)) = (i * 2).apply(12)
        2) 12 * 2 = 24
         */
        System.out.println(plus10.andThen(multiply2).apply(2));
        System.out.println("-----");

        Consumer<Integer> printT = (i) -> System.out.println(i);
        // Consumer<Integer> printT = System.out::println;
        printT.accept(10);
        System.out.println("-----");

        Supplier<Integer> get10 = () -> 10;
        System.out.println(get10.get());
        System.out.println("-----");

        Predicate<String> startsWithSoul = (s) -> s.startsWith("SouL");
        Predicate<Integer> isEven = (i) -> i % 2 == 0;

        String hisName = "Sanggyu";
        String herName = "SouL";
        int even = 10;
        int odd = 1;
        System.out.println(startsWithSoul.test(herName));
        System.out.println(startsWithSoul.test(hisName));
        System.out.println(isEven.test(even));
        System.out.println(isEven.test(odd));
        System.out.println("-----");

        /*
        Function 의 입력값과 출력값의 타입이 동일할 때 사용할 수 있다.
        만약 입력값과 출력값이 타입이 다르다면 BinaryOperator를 사용해야 한다.
         */
        UnaryOperator<Integer> newPlus10 = (i) -> i + 10;
        UnaryOperator<Integer> newMultiply2 = (i) -> i * 2;

        System.out.println(plus10.apply(1));
        System.out.println(multiply2.apply(2));
        System.out.println(plus10.compose(multiply2).apply(2));
        System.out.println(plus10.andThen(multiply2).apply(2));
        System.out.println("-----");

        BinaryOperator<Integer> sum = (a, b) -> a + b;
//        BinaryOperator<Integer> sum = (Integer a, Integer b) -> a + b;

//        --- lambda ---
        Foo foo = new Foo();
        foo.run();
    }

    private void run() {
        /*
        baseNumber가 final 키워드가 없어도, 사실 상 final 값(변경되지 않는 값) 이라면 익명 클래스 또는 람다에서 참조 가능 == effective final
        람다는 익명 클래스 구현체와 달리 쉐도잉 하지 않음.
         */
        final int baseNumber = 10;

        // local class, access to local variable
        class localClass {
            void printBaseNumber() {
                System.out.println(baseNumber);
            }
        }

        // abstract class, access to local variable
        Consumer<Integer> integerConsumer = new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) { // 여기서 쓰는 baseNumber는 위의 variable이 아닌 accept()로 들어오는 baseNumber, 즉 밑의 baseNumber가 들어온다.
                System.out.println(baseNumber);
            }
        };

        // lambda, access to local variable
        IntConsumer printInt = (i) -> {
            System.out.println(baseNumber);
        };

//        IntConsumer printInt = (baseNumber) -> { // 만약 위의 baseNumber를 가져오려 하면 에러가 난다. 같은 스콥에 있기 때문에 중복된 이름을 쓸 수 없는 것이다.
//            System.out.println(baseNumber + 1); // final 값을 이렇게 바꿔버리며 final 값이 아니게 되어도 에러가 난다.
//        };

        printInt.accept(baseNumber);
    }
}