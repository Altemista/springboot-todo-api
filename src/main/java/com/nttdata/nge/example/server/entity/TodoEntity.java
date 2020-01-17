package com.nttdata.nge.example.server.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.nttdata.nge.example.server.model.Todo;

@Entity
@Table(name = "TODO")
public class TodoEntity {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "TITLE")
	private String title;

	@Column(name = "COMPLETED")
	private boolean completed;

	@Column(name = "PRIO")
	private Integer priority;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public Todo toTodo() {
		return new Todo(id, title, completed, priority);
	}

	public static TodoEntity fromTodo(Todo todo) {
		TodoEntity entity = new TodoEntity();
		entity.setId(todo.getId());
		entity.setCompleted(todo.isCompleted());
		entity.setPriority(todo.getOrder());
		entity.setTitle(todo.getTitle());
		return entity;
	}

}
