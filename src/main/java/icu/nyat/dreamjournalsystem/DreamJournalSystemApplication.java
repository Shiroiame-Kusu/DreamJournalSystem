package icu.nyat.dreamjournalsystem;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 梦境日记系统启动类
 * Dream Journal System Application
 */
@SpringBootApplication
@MapperScan("icu.nyat.dreamjournalsystem.mapper")
@EnableAsync
public class DreamJournalSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DreamJournalSystemApplication.class, args);
    }
}
