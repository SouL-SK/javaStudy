package me.whiteship.java8to11;

public interface InterfaceFoo {

    void printName();

    // default method > 구현체를 제공하는 방법. 반드시 문서화 할 것. @implSpec 사용.
    /**
     * @implSpec 이 구현체는 getName()으로 가져온 문자열을 대문자로 바꿔 출력한다.
     * 주의! 이 구현체를 등록하는 방법은 직접 만든 인터페이스에 선언할 것, 라이브러리에서 이 default methods 를 사용하지 말 것.
     */
    default void printNameUpperCase() {
        System.out.println(getName().toUpperCase());
    }

    String getName();
}
