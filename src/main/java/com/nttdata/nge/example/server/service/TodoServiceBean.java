package com.nttdata.nge.example.server.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nttdata.nge.example.server.entity.TodoEntity;
import com.nttdata.nge.example.server.repository.TodoRepository;

@Service
public class TodoServiceBean implements TodoService {
	
	@Autowired
	private TodoRepository todoRepository;

	@Override
	public void add(TodoEntity entity) {
		todoRepository.save(entity);
	}

	@Override
	public List<TodoEntity> getAll() {
		return todoRepository.findAll();
	}

}
