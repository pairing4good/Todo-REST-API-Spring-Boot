package com.pairgood.todo_api.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.*;

@Service
public class TodoService {
    private final Logger logger;
    private final TodoRepository todoRepository;

    @Autowired
    public TodoService (final TodoRepository todoRepository, final Logger logger){
        this.todoRepository = todoRepository;
        this.logger = logger;
    }

    public List<Todo> getMyTodoList(){
        List<Todo> todoList= new ArrayList<>();
        todoRepository.findAll().forEach(todoList::add);

        return  todoList;
    }

    public long AddItemToThelist(Todo todo){
        long todoId;
        todoRepository.save(todo);
        todoId=todo.getTodoId();

        return todoId;
    }

    public void DeleteItem(long id){
        todoRepository.deleteById(id);
        logger.info("Item removed from the list");
    }

    @SuppressWarnings({"OptionalGetWithoutIsPresent", "CallToPrintStackTrace"})
    public long UpdateTodoItem(long todoId, Todo todo){

        long updateTodoId=0;
        try {
            Todo updatedTodo=todoRepository.findById(todoId).get();

            updatedTodo.setTodoTitle(todo.getTodoTitle());
            updatedTodo.setTodoDescription(todo.getTodoDescription());
            updatedTodo.setTodoDate(todo.getTodoDate());
            updatedTodo.setComplete(todo.isComplete());
            todoRepository.save(updatedTodo);
            updateTodoId=updatedTodo.getTodoId();
            return updateTodoId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updateTodoId;
    }

    public boolean isTodoItemIdValid(long todoId){
        return todoRepository.findById(todoId).isPresent();
    }

    public long getNumberTodoItem(){
      return todoRepository.count();
    }


}
