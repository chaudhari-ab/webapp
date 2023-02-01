//package com.product.crud.Test;
//
//import com.product.crud.model.User;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.MockitoJUnitRunner;
//
//import java.time.LocalDateTime;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//
//public class UserServiceTest {
//    @Mock
//    private User repo;
//
//    @InjectMocks
//    private UserService service;
//
//    @Test
//    public void testSaveUser() {
//        User user = new User();
//        user.setUsername("john");
//        user.setPassword("password");
//
//        when(repo.save(user)).thenReturn(user);
//
//        User savedUser = service.saveUser(user);
//
//        assertNotNull(savedUser);
//        assertEquals("john", savedUser.getUsername());
//        assertNotEquals("password", savedUser.getPassword());
//        assertNotNull(savedUser.getAccount_created());
//        assertNotNull(savedUser.getAccount_updated());
//
//        verify(repo, times(1)).save(user);
//    }
//}
