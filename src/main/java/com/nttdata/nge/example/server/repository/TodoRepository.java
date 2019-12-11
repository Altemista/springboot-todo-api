package com.nttdata.nge.example.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nttdata.nge.example.server.entity.TodoEntity;

public interface TodoRepository extends JpaRepository<TodoEntity, Long> {
	
}
