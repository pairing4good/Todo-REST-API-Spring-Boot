package com.pairgood.todo_api.integration;

import com.pairgood.todo_api.config.LoggingConfiguration;
import com.pairgood.todo_api.todo.TodoController;
import com.pairgood.todo_api.todo.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TodoController.class)
@Import({LoggingConfiguration.class})
public class TodoIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService service;

    @Test
    void shouldReturnServerErrorForMissingEndpoints()  throws Exception {
        mockMvc.perform(get("/bad")).andDo(print()).andExpect(status().is5xxServerError());
    }
}
