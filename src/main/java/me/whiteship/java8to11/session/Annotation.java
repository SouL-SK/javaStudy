package me.whiteship.java8to11.session;

import java.lang.annotation.*;

// java 5에 대한 기본 지식이 부족하다는 것. <- 이건 자바에 대한 기초가 부족하다는 것.
//    중복 사용할 애노테이션을 만들기

// 하기의 애노테이션은 얼마나 유지할 것인지
@Retention(RetentionPolicy.RUNTIME)
//    TYPE_PARAMETER: 타입 변수에만 사용할 수 있다.
//    TYPE_USE: 타입 변수를 포함해서 모든 타입 선언부에 사용할 수 있다.
@Target(ElementType.TYPE_USE)
@Repeatable(Annotation.AnnotationContainer.class)
public @interface Annotation {
    String value();

    //중복 애노테이션 컨테이너 만들기 > 컨테이너 애노테이션은 중복 애노테이션과 @Retention 및 @Target 이 같거나 더 넓어야 한다.
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE_USE)
    @interface AnnotationContainer {
        Annotation[] value();
    }
}
