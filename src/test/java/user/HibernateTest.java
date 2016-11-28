package user;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import project.Application;
import project.config.AppRootConfig;
import project.config.AppWebConfig;
import project.model.user.User;
import project.security.SecurityConfig;
import project.service.UserService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by slava23 on 11/24/2016.
 */

@RunWith(SpringRunner.class)
@DataJpaTest
@WebAppConfiguration
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = {AppWebConfig.class, AppRootConfig.class, SecurityConfig.class, Application.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class HibernateTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserService userService;

    @Test
    public void findUserById(){
        User user = new User("testUser");
        User savedUser = entityManager.persist(user);
        int id = savedUser.getId();
        User userById = userService.getUserById(id);
        assertEquals(userById.getUsername(),"testUser");
    }

    @Test
    public void findUserByName(){
        User user = new User("testUser");
        User savedUser = entityManager.persist(user);
        String username = savedUser.getUsername();
        User userByName = userService.getUserByName(username);
        assertEquals(userByName.getUsername(),"testUser");
    }

    @Test
    public void deleteUser(){
        User user = new User("testUser");
        User savedUser = entityManager.persist(user);
        int id = savedUser.getId();
        userService.deleteUser(id);
        User userById = userService.getUserById(id);
        assertNull(userById);
    }

    @Test
    public void updateUser(){
        User user = new User("testUser");
        User savedUser = entityManager.persist(user);
        int id = savedUser.getId();
        savedUser.setUsername("testUserNew");
        userService.saveUser(savedUser);
        User userById = userService.getUserById(id);
        assertEquals(userById.getUsername(),"testUserNew");
    }

}
