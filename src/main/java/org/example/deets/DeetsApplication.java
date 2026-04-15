package org.example.deets;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.sql.DataSource;

@SpringBootApplication
public class DeetsApplication {
    @Autowired
    private DataSource dataSource;

    public static void main(String[] args) {
        SpringApplication.run(DeetsApplication.class, args);
    }

    @PostConstruct
    public void checkPool() {
        HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
        System.out.println("Max pool size: " + hikariDataSource.getMaximumPoolSize());
        System.out.println("Min idle: " + hikariDataSource.getMinimumIdle());
    }

}
