package com.nttdata.nge.example.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NgeExampleServer {

	public static void main(String[] args) {
		System.out.println("Hello NGE");
		SpringApplication.run(NgeExampleServer.class, args);
	}
}
