package hoang.edu.spring_java_2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class)
public class SpringJava2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringJava2Application.class, args);
    }

}
