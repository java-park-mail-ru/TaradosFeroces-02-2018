package application;


import application.dao.UserDAO;
import application.dao.implementations.UserDAOImplRunTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public UserDAO userDAO() {
        return new UserDAOImplRunTime();
    }
}

