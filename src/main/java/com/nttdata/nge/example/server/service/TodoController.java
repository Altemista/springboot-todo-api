package com.nttdata.nge.example.server.service;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.nge.example.server.entity.TodoEntity;
import com.nttdata.nge.example.server.model.Todo;

@CrossOrigin(methods = { POST, GET, OPTIONS, DELETE, PATCH }, maxAge = 3600, allowedHeaders = { "x-requested-with",
		"origin", "content-type", "accept" }, origins = "*")
@RestController
@RequestMapping("/todos")
public class TodoController {

	@Autowired
	private TodoService todoService;

	@RequestMapping(method = GET)
	public Collection<Todo> listAll() {
		System.out.println("TodoController.listAll()");

		return todoService.getAll().stream().map(t -> t.toTodo()).collect(Collectors.toList());
	}

	@RequestMapping(method = POST)
	public Todo add(@RequestBody Todo todo) {
		System.out.println("TodoController.add()");
		System.out.println(todo);

		TodoEntity entity = new TodoEntity();
		entity.setCompleted(todo.isCompleted());
		entity.setPriority(todo.getOrder());
		entity.setTitle(todo.getTitle());
		todoService.add(entity);

		return entity.toTodo();
	}

	@RequestMapping(method = DELETE)
	public void deleteAll() {
		System.out.println("TodoController.deleteAll()");
		// TODO MSI
	}

	@RequestMapping(method = DELETE, value = "/{todo-id}")
	public void delete(@PathVariable("todo-id") long id) {
		System.out.println("TodoController.delete(): " + id);
		todoService.delete(id);
	}

}
