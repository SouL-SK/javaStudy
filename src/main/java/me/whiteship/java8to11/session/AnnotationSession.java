package me.whiteship.java8to11.session;

import java.util.Arrays;

@Annotation("first annotation")
@Annotation("second annotation")
public class AnnotationSession {
    // generic, type parameter <- 언능 공부해라.. 물어보면 바로 나올 수 있을 때까지

    static class LetsStudyAnnotation<@Annotation("first") T> {
        //자바 8 부터 애노테이션을 타입 선언부에도 사용가능하게 되었다.
//        또한 애노테이션을 중복해서 사용할 수 있게 되었다.
//        앞의 C가 type parameter, 뒤의 C는 파라미터이다.
//        type parameter 에 애노테이션 사용 가능
//        타입 선언부: 제네릭 타입, 변수 타입, 매개변수 타입, 예외 타입, ...
        public static <@Annotation("second") C> void print(C c) {
            System.out.println(c);
        }
    }

    public static void main(String[] args) {
        Annotation.AnnotationContainer annotationContainer = AnnotationSession.class.getAnnotation(Annotation.AnnotationContainer.class);
        Arrays.stream(annotationContainer.value()).forEach(c -> {
            System.out.println(c.value());
        });
    }

}
