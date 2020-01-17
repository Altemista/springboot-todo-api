package com.nttdata.nge.example.server.service;

import java.util.List;

import com.nttdata.nge.example.server.entity.TodoEntity;

public interface TodoService {
	
	TodoEntity add(TodoEntity entity);
	
	void delete(long id);
	
	List<TodoEntity> getAll();

}
