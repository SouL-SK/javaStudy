package me.whiteship.java8to11.session;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateAndTimeSession {


    public static void main(String[] args) throws InterruptedException {
//        disadvantagesOfDate();
//        newDateApiJava8();
//        periodForJaveClass();
        parseOrFormat();
    }

    /**
     * 기존의 Date, Time 과 관련된 라이브러리의 문제점
     * Calendar carlendar = new GregorianCalendar();
     * SimpleDateFormat dateFormat = new SimpleDateFormat();
     */
    public static void disadvantagesOfDate() throws InterruptedException {

        // Date 같은 경우는 date 로부터 date.getTime(); 을 통해 시간을 가져올 수 있었다.
        // 자세한 것은 https://futurecreator.github.io/2018/06/07/computer-system-time/ 참고
        Date date = new Date();

        // 문제점 1 > 클래스 이름이 명확하지 않다. Date 인데 time 까지 다룬다.
        long time = date.getTime();
        System.out.println("EPOCK time 은 기계용 시간이다. = " + time);
        System.out.println("date : " + date + "\ntime : " + time);

        // 문제점 2 > mutable 한 클래스라 Thread safe 하지 않다. ( 객체의 값 변경이 자유롭다.)
        Thread.sleep(1000 * 3);
        Date afterThreeSeconds = new Date();
        System.out.println("after 3 seconds = " + afterThreeSeconds);

        /*
         mutable 한 지점
         */
        afterThreeSeconds.setTime(time);
        System.out.println("afterThreeSeconds.setTime(time) = " + afterThreeSeconds);

        // 문제점 3 > 버그 발생할 여지가 많다. month 가 0부터 시작한다. 또한 int type 으로 받기 때문에 -1928을 year 에 넣을 수 있다. 즉 타입 안정성이 없고 month 가 0 부터 시작해서..
        Calendar birthDayWithBug = new GregorianCalendar(1982, 7, 15);
        Calendar birthDayWithBugs = new GregorianCalendar(-1982, Calendar.JULY, 15);

        System.out.println("GregorianCalendar(1982, 7, 15) = " + birthDayWithBug.getTime());
        System.out.println("GregorianCalendar(-1982, Calendar.JULY, 15) = " + birthDayWithBugs.getTime());

        // 심지어 mutable 하다. 그리고 Date에서 getTime 하면 long 이, Calendar 에서 getTime 하면 date 가 나온다.
        System.out.println("getTime() 이지만 Date가 나온다\nbirthDayWithBugs.getTime() = " + birthDayWithBugs.getTime());
        birthDayWithBugs.add(Calendar.DAY_OF_YEAR, 1);
        System.out.println("mutable 한 지점, 하루가 지나갔다.\nbirthDayWithBugs.add(Calendar.DAY_OF_YEAR, 1) = " + birthDayWithBugs.getTime());

        // 그럼 immutable 한 것이 좋은 것일까? 기존의 인스턴스가 변하지 않기 때문에 값을 변경하려면 새로운 객체를 만들어내야 한다.
        LocalDate dateOfBirth = LocalDate.of(2012, Month.MAY, 14);
        LocalDate firstBirthday = dateOfBirth.plusYears(1);

    }

    public static void newDateApiJava8() {
        // 새로 추가된 Date-Time API 는 Date-Time Design Principles 를 따른다.
        // Clear, Fluent, Immutable, Extensible
        // Extensible : java.time.chrono. 에서 많은 달력들을 쓸 수 있고, 직접 만들 수도 있다.
        System.out.println("---기계용 시간을 표현---");
        Instant nowInstant = Instant.now();

        // 기준시 UTC, GMT 영국 그리니치 천문대 기준 시간.
        System.out.println("기본값 time = " + nowInstant);

        System.out.println("타임 존 변경 = " + nowInstant.atZone(ZoneId.of("Asia/Seoul")));

        // Instant 예시
//        ZonedDateTime zonedDateTimeInstant = nowInstant.atZone(ZoneId.systemDefault());
        ZonedDateTime zonedDateTimeInstant = nowInstant.atZone(ZoneId.of("Asia/Seoul"));
        System.out.println("Instant zone time = " + zonedDateTimeInstant);

        // LocalDateTime 예시
        LocalDateTime nowLocalDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTimeLocalDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        System.out.println("LocalDateTime zone time = " + zonedDateTimeLocalDateTime);

        System.out.println("인류용 시간은 연, 월, 일, 시, 분, 초 등을 표현한다.");
        LocalDateTime birthday =
                LocalDateTime.of(1999, Month.OCTOBER, 1, 0, 0, 0);
        System.out.println("인류용 시간 LocalDateTime = " + birthday);
        Instant timeConvertFromZonedDateTime = zonedDateTimeInstant.toInstant();
    }

    public static void periodForJaveClass() {

        // Period 사용 하기. 인류용 시간을 가지고 비교 (LocalDate)
        LocalDate today = LocalDate.now();
        LocalDate thisYearBirthday = LocalDate.of(2020, Month.OCTOBER, 1);

        Period period = Period.between(today, thisYearBirthday);
        System.out.println("getDays() 사용 하기 = " + period.getDays());

        Period until = today.until(thisYearBirthday);
        System.out.println("get() 사용 하기 = " + until.get(ChronoUnit.DAYS));


        // Duration 사용 하기. 기계용 시간을 가지고 비교 (Instant)
        Instant now = Instant.now();

        // TemporalUnit 자리에는 ChronoUnit 이 들어가는 것을 그냥 외우자. 또한 now.plus() 를 한다고 기존 instance 인 now 가 바뀌지 않으니 새로운 객체를 만드는 것을 꼭 명심해라.
        Instant plus = now.plus(10, ChronoUnit.SECONDS);


        Duration between = Duration.between(now, plus);
        System.out.println("Duration.between(now, plus).getSeconds() = " + between.getSeconds());
    }

    public static void parseOrFormat() {

        LocalDateTime now = LocalDateTime.now();

        // format
        DateTimeFormatter MMddyyyy = DateTimeFormatter.ofPattern("MMddyyyy");
        System.out.println(now.format(MMddyyyy));

        // parsing
        LocalDate parse = LocalDate.parse("10011999", MMddyyyy);
        System.out.println(parse);

        // GregorianCalendar 와 Date 타입의 인스턴스를 Instant 나 ZonedDateTime 으로 변환 가능
        Date date = new Date();
        Instant instant = date.toInstant();
        Date newDate = Date.from(instant);

        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        ZonedDateTime dateTime = gregorianCalendar.toInstant().atZone(ZoneId.systemDefault());
        GregorianCalendar.from(dateTime);

        // java.util.TimeZone 에서 java.time.ZoneId 로 상호 변환 가능.
        ZoneId zoneId = TimeZone.getTimeZone("PST").toZoneId();
        TimeZone timeZone = TimeZone.getTimeZone(zoneId);
    }
}
