package com.nttdata.nge.example;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import org.testng.annotations.Test;

import com.nttdata.nge.example.server.entity.TodoEntity;
import com.nttdata.nge.example.server.model.Todo;

public class ModelConversionTests {

	private static final String TITLE = "Some text";

	@Test
	public void model2entity() {

		Todo m = new Todo(1, TITLE, true, 5);
		compare(m, TodoEntity.fromTodo(m));
	}
	
	@Test
	public void testToString() {

		Todo m = new Todo(1, TITLE, true, 5);
		String string = m.toString();
		assertTrue(string.contains(TITLE));
	}
	
	@Test
	public void mergeWithNull() {

		Todo m = new Todo(1, TITLE, true, 5);
		Todo m2 = new Todo(2, null, false, 7);
		
		Todo merged = m.merge(m2);
		
		assertEquals(merged.getId(), 2);
		assertEquals(merged.getTitle(), TITLE);
		assertEquals(merged.isCompleted(), false);
		assertEquals(merged.getOrder(), 7);
	}

	@Test
	public void entity2Model() {

		TodoEntity e = new TodoEntity();
		e.setId(1l);
		e.setCompleted(false);
		e.setPriority(12);
		e.setTitle(TITLE);

		compare(e.toTodo(), e);
	}

	private void compare(Todo m, TodoEntity e) {
		assertEquals((Long) m.getId(), e.getId());
		assertEquals((Integer) m.getOrder(), e.getPriority());
		assertEquals(m.getTitle(), e.getTitle());
		assertEquals(m.isCompleted(), e.isCompleted());
	}

}
