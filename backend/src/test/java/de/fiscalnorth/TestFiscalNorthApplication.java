package de.fiscalnorth;

import org.springframework.boot.SpringApplication;

public class TestFiscalNorthApplication {

    public static void main(String[] args) {
        SpringApplication.from(FiscalNorthApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
