package com.wasacz.hfms.persistence;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest //it is using to testing repositories of jpa
class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void whenFindByUsername_thenReturnUser() {
        //given
        User user = User.builder().username("Test").password("security_password").role(Role.ROLE_USER).build();
        entityManager.persist(user);
        entityManager.flush();

        //when
        Optional<User> found = userRepository.findByUsername("Test");

        //then
        assertTrue(found.isPresent());
        assertEquals(found.get().getUsername(), user.getUsername());
        assertEquals(found.get().getPassword(), user.getPassword());
        assertEquals(found.get().getRole(), user.getRole());
    }
}