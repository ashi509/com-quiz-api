package com.quiz.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.text.SimpleDateFormat;

@SpringBootApplication
public class ComQuizApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ComQuizApiApplication.class, args);
	}

	@Bean
	public SimpleDateFormat simpleDateFormat(){
		return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	}

}
