package org.example.deets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DeetsApplication {
//    TODO: race condition when 2 requests simultaneously try to shorten the same url
    public static void main(String[] args) {
        System.setProperty("user.timezone", "Asia/Kolkata");
        SpringApplication.run(DeetsApplication.class, args);
    }

}
