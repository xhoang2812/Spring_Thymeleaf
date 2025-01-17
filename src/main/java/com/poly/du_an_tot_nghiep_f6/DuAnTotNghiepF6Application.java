package com.poly.du_an_tot_nghiep_f6;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class DuAnTotNghiepF6Application {

    public static void main(String[] args) {
        SpringApplication.run(DuAnTotNghiepF6Application.class, args);
        System.out.println(
                "\n\n\n\n                                                                        " +
                "==============================================================\n                                                                        " +
                "===                       Hello World!                     ===\n                                                                        " +
                "===            Welcome To WebApp F6 Application            ===\n                                                                        " +
                "===          Chương Trình Đã Được Chạy Thành Công          ===\n                                                                        " +
                "==============================================================" +
                "\n\n");
    }

}
