package me.whiteship.java8to11.session;

import me.whiteship.java8to11.dto.OnlineClass;
import me.whiteship.java8to11.dto.Progress;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

public class OptionalSession {

    public static void main(String[] args) {
        List<OnlineClass> springClasses = new ArrayList<>();
        springClasses.add(new OnlineClass(1, "spring boot", true));
        springClasses.add(new OnlineClass(2, "spring date jpa", true));
        springClasses.add(new OnlineClass(3, "spring mvc", false));
        springClasses.add(new OnlineClass(4, "spring core", false));
        springClasses.add(new OnlineClass(5, "rest api development", false));

//        OnlineClass spring_boot = new OnlineClass(1, "spring boot", true);
//        Duration studyDuration = spring_boot.getProgress().getStudyDuration();
        // progress가 null 일 때는 NullPointException 이 뜬다.
        // 그렇다고 이렇게 수동으로 null 체크를 해주면 에러가 많이 뜰 수 있다.
        // getProgress에서 null 체크를 해볼까?
//        if (spring_boot.progress != null) {
//            System.out.println(studyDuration);
//        }
//        위의 코드를 표현할 수단이 바로 Optional 이다.

        // Optional.of 는 박싱, 언박싱 과정이 많이 발생하기 때문에 OptionalInt.of 와 같이 사용하자.
        // Collection, Map, Stream Array, Optional 은 Optional 로 감싸지 말 것 (왜냐하면 앞의 타입들은 그 자체로 Null 인지 판단할 수 있는 컨테이너 성격의 타입들이기 때문)
        OptionalInt.of(springClasses.size());

        Optional<OnlineClass> spring1 = springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("jpa"))
                .findFirst();
        boolean present1 = spring1.isPresent();
        System.out.println(present1);
        System.out.println(!present1);

        // Optional 에서 없는 값을 가지고 조회해봤을 때 stream 에 값이 없다면 에러가 난다.
//        OnlineClass onlineClass1 = spring1.get();
//        System.out.println(onlineClass1.getTitle());

        System.out.println("---------------");
        Optional<OnlineClass> spring2 = springClasses.stream()
                .filter(oc -> oc.getTitle().startsWith("spring"))
                .findFirst();

        System.out.println("Optional 에 있는 값 가져오기");
        OnlineClass onlineClass2 = spring2.get();
        System.out.println(onlineClass2.getTitle());

        boolean present2 = spring2.isPresent();
        System.out.println(present2);
        System.out.println(!present2);


        System.out.println("Optional 에 값이 있는 경우에 그 값을 가지고 ~~를 하라. ( spring 으로 시작하는 수업이 있으면 title, id 를 출력하라.");
        spring2.ifPresent(oc -> System.out.println(oc.getTitle()));
        spring2.ifPresent(oc -> System.out.println(oc.getId()));
        
        System.out.println("없다면, 비어있는 수업을 return 하라");
        OnlineClass onlineClassForOrElse = spring1.orElse(createNewClass()); // optional 에 instance 를 넘겨주는 것. method reference 를 주는 것은 아니다. createNewClass() 를 사용하고 만든다.
        System.out.println(onlineClassForOrElse.getTitle());

        System.out.println("값이 있으면 가져오고, 없는 경우에 새로 만들어서 return 하라");
        OnlineClass onlineClassForOrElseGet = spring1.orElseGet(OptionalSession::createNewClass); // supplier 추가 가능. createNewClass() 를 사용하지 않고 새로 만든다.
        System.out.println(onlineClassForOrElseGet.getTitle());

        System.out.println("-------------------");
        System.out.println("값이 있으면 가져오고, 없으면 에러를 던져라.");
        OnlineClass onlineClassForMethodRef = spring2.orElseThrow(IllegalStateException::new);
        System.out.println(onlineClassForMethodRef);

        System.out.println("Optional 에 들어있는 값 걸러내기");
        Optional<OnlineClass> onlineClassForFilter =
                spring2.filter(oc -> !oc.isClosed());
//                spring2.filter(OnlineClass::isClosed);

        System.out.println(onlineClassForFilter.isEmpty());
        System.out.println(onlineClassForFilter.isPresent());

        System.out.println("Optional 에 들어있는 값 변환하기");
        spring2.map(OnlineClass::getId);

        // 이런 값이라면 상관없지만, 만약 Progress 클래스 처럼 클래스 안에 클래스가 들어있는 경우라면 양파 껍질 까듯이 Optional 에 Optional 이렇게 여러 겹으로 되어 있는 타입을 하나하나 벗겨가며 접근해야 한다.
        Optional<Optional<Progress>> progress = spring2.map(OnlineClass::getProgress); // (2)
        Optional<Progress> progress4 = progress.orElse(Optional.empty()); // (3)
        Optional<Progress> progress1 = progress.orElseThrow();
        Progress progress2 = progress1.orElseThrow();
        // 위의 과정을 해주는 메서드가 있다. 바로 flatMap 이다. ( Optional flatMap(Function): Optional 안에 들어있는 인스턴스가 Optional 인 경우에 사용하면 편리하다.)
        Optional<Progress> progress3 = spring2.flatMap(OnlineClass::getProgress); // (1)

        // (1) 줄은 (2) + (3) 과 같다.
    }

    private static OnlineClass createNewClass() {
        System.out.println("creating new online class");
        return new OnlineClass(10, "New Class", false);
    }

}
