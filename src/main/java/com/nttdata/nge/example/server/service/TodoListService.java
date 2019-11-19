package com.nttdata.nge.example.server.service;

import java.util.Date;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoListService {
	
	public TodoListService() {
    }
	
	@RequestMapping("/home")
    public String home() {
        System.out.println(new Date() + " ======= /home =======");
        return "home";
    }

}
