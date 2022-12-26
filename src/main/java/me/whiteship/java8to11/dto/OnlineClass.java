package me.whiteship.java8to11.dto;

import java.util.Optional;

public class OnlineClass {
    private int id;
    private String title;
    private boolean closed;

    public Progress progress;

    public OnlineClass(int id, String title, boolean closed) {
        this.id = id;
        this.title = title;
        this.closed = closed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

//    public Progress getProgress() {
//        // 로직 처리를 할 때 에러 정보를 보내주는 것은, 그만큼 리소스를 쓰게 되기 때문에 좋은 습관은 아니다.
////        if (this.progress == null) {
////            throw new IllegalStateException();
////        }
//        return progress;
//    }

    /**
     * return 값으로만 쓰는 것이 권장 사항. (메서드 매개변수 타입, 맵의 키 타입, 인스턴스 필드 타입으로 쓰지 말자.)
     * 기존의 getter 방식으로 사용하면 Optioanal 이 제공하는 isPresent()로 값이 있는지 확인 후에 보내야 한다.
     * 호출하는 쪽에서 null 을 넣어서 호출할 수 있다. 그래서 NullPointException 을 일으킬 수 있기 때문에, 받아온 값에 대해 한 번더 isNull 체크를 해주어야 한다.
     * 그래서 처음부터 return Optional.ofNullable 로 진행하여 isNull 체크도 같이 들어가며 진행하도록 하는 것이 안전하다.
     * Key 는 null 이 아니기 때문에 Optional 을 이용할 때는 이와 같은 상황에서도 사용하지 않도록 한다.
     * ---
     * Optional 이라는 박스를 만들어 그 안에 객체를 담는 것. 이 박스 안은 null 일 수도 있고 뭔가가 담겨져 있을 수도 있다.
     * 이 박스 안에 넣을 때, null 을 넣을 수도 있다. 그래서 Nullable 을 사용한다.
     * 비어있는 객체를 넣은 경우(null 을 넣은 경우) 비어있는 Optional 을 반환한다.
     *
     * @return Optional<Progress>
     */
    public Optional<Progress> getProgress(){
        return Optional.ofNullable(progress);
    }

    public void setProgress(Progress progress) {
        this.progress = progress;
    }
}
