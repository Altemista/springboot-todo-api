package com.nttdata.nge.example.server.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;

public class Todo {

	private final String title;
	private final boolean completed;
	private final Long order;

	@JsonCreator
	public Todo(@JsonProperty("title") String title, @JsonProperty(value = "completed") boolean completed,
			@JsonProperty(value = "order") long order) {
		this.title = title;
		this.completed = completed;
		this.order = order;
	}

	public String getTitle() {
		return title;
	}

	public boolean isCompleted() {
		return completed;
	}

	public long getOrder() {
		return order;
	}

	@Override
	public String toString() {
		return "Todo{" + "title='" + title + '\'' + ", completed=" + completed + ", order=" + order + '}';
	}

	public Todo merge(Todo updatedTodo) {
		return new Todo(Optional.ofNullable(updatedTodo.title).orElse(title), updatedTodo.completed,
				Optional.ofNullable(updatedTodo.order).orElse(order));
	}
}
