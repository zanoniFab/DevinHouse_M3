package com.example.exerciciosmodulo3s01;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ExerciciosModulo3S01Application {

    public static void main(String[] args) {

        SpringApplication.run(ExerciciosModulo3S01Application.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
