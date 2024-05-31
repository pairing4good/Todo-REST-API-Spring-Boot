package com.pairgood.todo_api.todo;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.*;

@SuppressWarnings("unused")
@RestController
@RequestMapping("api/v1/todo")
public class TodoController {
    private final Logger logger;
    private final TodoService todoService;

    @SuppressWarnings("unused")
    @Autowired
    public TodoController(final TodoService todoService, final Logger logger){
        this.todoService = todoService;
        this.logger = logger;
    }

    @SuppressWarnings("unused")
    @RequestMapping(method = RequestMethod.POST, value = "/additem")
    public ResponseEntity<ResponseTodoList> AddItemTolist(@Valid @RequestBody Todo todo){
        long todoId = todoService.AddItemToThelist(todo);

        if(todoId>0){
            ResponseTodoList responseTodoList= new ResponseTodoList("Item added to todo list", HttpStatus.CREATED);
            logger.info(responseTodoList.getMessage()+". code: "+responseTodoList.getCode());
            return new ResponseEntity<>(responseTodoList,HttpStatus.CREATED);

        }else{
            ResponseTodoList responseTodoList= new ResponseTodoList("Item Not added to todo list", HttpStatus.BAD_REQUEST);
            logger.info(responseTodoList.getMessage()+". code: "+responseTodoList.getCode());
            return new ResponseEntity<>(responseTodoList,HttpStatus.BAD_REQUEST);
        }

    }

    @SuppressWarnings("unused")
    @RequestMapping(method = RequestMethod.PUT, value = "/updateitem/{updateTodoId}")
    public ResponseEntity<ResponseTodoList> UpdateItem(@PathVariable long updateTodoId, @Valid @RequestBody Todo todo){

        boolean isTodoIdValid=todoService.isTodoItemIdValid(updateTodoId);

        if(isTodoIdValid){
            long todoId;
            todoId=todoService.UpdateTodoItem(updateTodoId,todo);

            if(todoId>0){
                ResponseTodoList responseTodoList= new ResponseTodoList("Item with the following title "+todo.getTodoTitle()+" updated", HttpStatus.OK);
                logger.info(responseTodoList.getMessage()+". code: "+responseTodoList.getCode());
                return new ResponseEntity<>(responseTodoList, HttpStatus.OK);

            }else{
                ResponseTodoList responseTodoList= new ResponseTodoList("Item Not updated", HttpStatus.BAD_REQUEST);
                logger.info(responseTodoList.getMessage()+". code: "+responseTodoList.getCode());
                return new ResponseEntity<>(responseTodoList, HttpStatus.BAD_REQUEST);
            }


        }else {
            ResponseTodoList responseTodoList = new ResponseTodoList("Request not successful, invalid information provided. Please try again.", HttpStatus.NOT_FOUND);

            return new ResponseEntity<>(responseTodoList, HttpStatus.NOT_FOUND);
        }
    }

    @SuppressWarnings("unused")
    @RequestMapping(method = RequestMethod.GET, value = "/todolist")
    public List<Todo> getTodoList(){
        return todoService.getMyTodoList();

    }

    @SuppressWarnings("unused")
    @RequestMapping(method = RequestMethod.DELETE, value = "/deleteitem/{deleteTodoId}")
    public ResponseEntity<ResponseTodoList> DeleteItem(@PathVariable long deleteTodoId){

        boolean isTodoIdValid=todoService.isTodoItemIdValid(deleteTodoId);

        if(isTodoIdValid){

            todoService.DeleteItem(deleteTodoId);


                ResponseTodoList responseTodoList= new ResponseTodoList("Item deleted", HttpStatus.OK);
                logger.info(responseTodoList.getMessage()+". code: "+responseTodoList.getCode());
                return new ResponseEntity<>(responseTodoList, HttpStatus.OK);



        }else {
            ResponseTodoList responseTodoList = new ResponseTodoList("Request not successful, invalid information provided. Please try again.", HttpStatus.NOT_FOUND);

            return new ResponseEntity<>(responseTodoList, HttpStatus.NOT_FOUND);

        }
    }

    @SuppressWarnings("unused")
    @RequestMapping(method = RequestMethod.GET, value = "/todocount")
    public long getNumberTodo(){
        return todoService.getNumberTodoItem();
    }
}
