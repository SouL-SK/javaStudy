package me.whiteship.java8to11;

public interface Bar extends InterfaceFoo{

//    void printNameUpperCase();

    default void printNameUpperCase() {
        System.out.println("Bar");
    }

    // 같은 메서드를 만든 interface를 한 개의 클래스에서 impl 하면 컴파일 에러 ( 어떤 인터페이스의 메서드를 사용할 지 모르기 때문.)
    // 위 처럼 같은 메서드 이름을 쓰더라도 새로 추상 메서드를 만들어 Bar에서만 쓸 수 있는 메서드로 만들 수 있다. 물론 저 메서드를 상속받는 클래스들은 재정의 필요.

    /*
    인터페이스를 미리 만들어 둔 뒤에, 따로 implements 를 만들어서 인터페이스를 받아오는 클래스를 이용해 다중 상속이 안되는 문제점을 해결했다. > 비침투성을 강조하는 접근 방법. (상속을 강제하지 않음)
    extends 를 쓰지 않는 이유가 상속을 강제하지 않고 인터페이스로부터 메서드를 받아오기 위함.
     */
}
