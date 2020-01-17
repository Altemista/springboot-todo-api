package com.nttdata.nge.example.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;

public class Todo {

	private final long id;
	private final String title;
	private final boolean completed;
	private final int order;

	@JsonCreator
	public Todo(@JsonProperty("id") long id, @JsonProperty("title") String title,
			@JsonProperty(value = "completed") boolean completed, @JsonProperty(value = "order") int order) {
		this.id = id;
		this.title = title;
		this.completed = completed;
		this.order = order;
	}
	
	public long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public boolean isCompleted() {
		return completed;
	}

	public int getOrder() {
		return order;
	}

	@Override
	public String toString() {
		return "Todo{" + "title='" + title + '\'' + ", completed=" + completed + ", order=" + order + '}';
	}

	public Todo merge(Todo updatedTodo) {
		return new Todo(Optional.ofNullable(updatedTodo.id).orElse(id),
				Optional.ofNullable(updatedTodo.title).orElse(title), updatedTodo.completed,
				Optional.ofNullable(updatedTodo.order).orElse(order));
	}
}
