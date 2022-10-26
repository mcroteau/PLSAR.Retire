package dev.blueocean;

import dev.blueocean.extras.Todo;
import dev.blueocean.extras.TodoRepo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PersistTest extends BaseTest {

    @Test
    public void testSaveTodo(){
        Todo todo = new Todo();
        todo.setTitle("Foo");
        TodoRepo todoRepo = (TodoRepo) kakai.getElement("todorepo");
        todoRepo.save(todo);

        List<Todo> todos = todoRepo.getList();
        assertEquals(1, todos.size());
    }

    @Test
    public void testList(){
        Todo todo = new Todo();
        todo.setTitle("Foo");
        TodoRepo todoRepo = (TodoRepo) kakai.getElement("todorepo");
        todoRepo.save(todo);

        List<Todo> todos = todoRepo.getList();
        assertEquals(1, todos.size());
    }

    @Test
    public void testDelete(){
        Todo todo = new Todo();
        todo.setTitle("Foo");
        TodoRepo todoRepo = (TodoRepo) kakai.getElement("todorepo");
        todoRepo.save(todo);

        todoRepo.delete(1);
        List<Todo> todos = todoRepo.getList();
        assertEquals(0, todos.size());
    }

    @Test
    public void testGetCache(){
        assertNotNull(kakai);
    }


}