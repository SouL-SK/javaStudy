package me.whiteship.java8to11.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.Id;

@SpringBootApplication
public class UserRepositoryExample implements CommandLineRunner {

    @Autowired
    private UserRepository<User, Long> userRepository;

    public static void main(String[] args) {
        SpringApplication.run(UserRepositoryExample.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 새로운 User 객체 생성
        User user = new User();
        user.setId(1L);
        user.setUsername("soul");
        user.setEmail("soul.kim@snplab.io");

        // UserRepository를 사용하여 User 객체 저장
        userRepository.save(user);

        // UserRepository를 사용하여 User 객체 조회
        User retrievedUser = userRepository.findById(1L).orElse(null);
        if (retrievedUser != null) {
            System.out.println("Retrieved User: " + retrievedUser.getUsername());
        }
    }
}

@Entity
class User {
    @Id
    private Long id;
    private String username;
    private String email;

    // Getter와 Setter 메서드

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}

@Repository
interface UserRepository<T, ID> extends CrudRepository<T, ID> {
    // CrudRepository를 확장하여 User 엔티티와 Long 타입의 ID를 사용하는 CRUD 메서드가 자동으로 생성됩니다.
    // 추가적으로 사용자 정의 메서드를 선언할 수도 있습니다.
}