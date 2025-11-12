package de.hinni.haushaltsmanager;

import org.springframework.boot.SpringApplication;

public class TestHaushaltsmanagerApplication {

    public static void main(String[] args) {
        SpringApplication.from(HaushaltsmanagerApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
