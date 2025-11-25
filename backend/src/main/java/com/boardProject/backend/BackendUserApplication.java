package com.boardProject.backend;  // 너 프로젝트 패키지에 맞게

import com.boardProject.backend.test.TestUser;
import com.boardProject.backend.test.TestUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class BackendUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendUserApplication.class, args);
    }

    @Bean

    CommandLineRunner test(TestUserRepository repo) {
        return args -> {
            TestUser user = new TestUser();
            user.setName("Mir");
            repo.save(user);

            System.out.println("저장 성공! id=" + user.getId());
        };
    }
}

