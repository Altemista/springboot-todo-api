package com.nttdata.nge.example.server.service;

import java.util.List;

import com.nttdata.nge.example.server.entity.TodoEntity;

public interface TodoService {
	
	void add(TodoEntity entity);
	
	List<TodoEntity> getAll();

}
