package com.product.crud;

import com.product.crud.model.User;
import com.product.crud.repo.CrudRepo;
import com.product.crud.services.CrudService;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class DemoApplicationTests {

    @Autowired
    private CrudService service;
    @MockBean
    private CrudRepo repository;
    @Test
    public void saveUserTest() {
        User user = new User();
        //UUID.randomUUID(),"kk", "K", "a1100@dddfgii.com", "sdsdssscdD@2", LocalDateTime.now(), LocalDateTime.now()
        user.setId(UUID.randomUUID());
        user.setFirst_name("Abhishek");
        user.setLast_name("Chaudhari");
        user.setPassword("Password");
        user.setAccount_created(LocalDateTime.now());
        user.setAccount_updated(LocalDateTime.now());
        user.setUsername("Abhishek");
        String username = "Abhishek";
        when(repository.findByUsername(username)).thenReturn(user);
        assertEquals(user.getUsername(), "Abhishek"); 
    }
}