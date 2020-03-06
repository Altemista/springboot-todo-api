package com.nttdata.nge.example.server.service;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nttdata.nge.example.server.entity.TodoEntity;
import com.nttdata.nge.example.server.model.Todo;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@CrossOrigin(methods = { POST, GET, OPTIONS, DELETE, PATCH }, maxAge = 3600, allowedHeaders = { "x-requested-with",
		"origin", "content-type", "accept" }, origins = "*")
@RestController
@RequestMapping("/todos")
public class TodoController {

	@Autowired
	private TodoService todoService;

	@ApiOperation(httpMethod = "GET", value = "Returns all Todos", nickname = "listAllTodos", tags = { "Todo" })
	@ApiResponses(value = { @ApiResponse(code = 200, message = ""),
			@ApiResponse(code = 400, message = "No Todos found"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@RequestMapping(method = GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Collection<Todo> listAll() {
		return todoService.getAll().stream().map(t -> t.toTodo()).collect(Collectors.toList());
	}

	@ApiOperation(httpMethod = "POST", value = "Add a Todo", nickname = "addTodo", tags = { "Todo" })
	@ApiResponses(value = { @ApiResponse(code = 200, message = ""),
			@ApiResponse(code = 400, message = "Tried to create a Todo with missing data"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@RequestMapping(method = POST)
	public ResponseEntity<Todo> add(@RequestBody Todo todo) {

		if (todo.getTitle() == null || todo.getTitle().isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
		TodoEntity entity = new TodoEntity();
		entity.setCompleted(todo.isCompleted());
		entity.setPriority(todo.getOrder());
		entity.setTitle(todo.getTitle());
		todoService.add(entity);

		return ResponseEntity.ok(entity.toTodo());
	}

	@ApiOperation(httpMethod = "DELETE", value = "Delete all todos", nickname = "deleteAllTodos", tags = { "Todo" })
	@ApiResponses(value = { @ApiResponse(code = 200, message = ""),
			@ApiResponse(code = 500, message = "Internal server error") })
	@RequestMapping(method = DELETE)
	public void deleteAll() {
		todoService.getAll().forEach(t -> delete(t.getId()));
	}

	@ApiOperation(httpMethod = "DELETE", value = "Delete a single todo", nickname = "deleteTodo", tags = { "Todo" })
	@ApiResponses(value = { @ApiResponse(code = 200, message = ""),
			@ApiResponse(code = 404, message = "Could not find the todo-id"),
			@ApiResponse(code = 500, message = "Internal server error") })
	@RequestMapping(method = DELETE, value = "/{todo-id}")
	public ResponseEntity<Void> delete(@PathVariable("todo-id") long id) {
		try {
			todoService.delete(id);
			return ResponseEntity.ok().build();
		} catch (EmptyResultDataAccessException e) {
			return ResponseEntity.notFound().build();
		}
	}

}
