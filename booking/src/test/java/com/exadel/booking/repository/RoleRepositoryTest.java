package com.exadel.booking.repository;


import com.exadel.booking.AbstractTest;
import com.exadel.booking.entities.user.role.Role;
import com.exadel.booking.entities.user.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class RoleRepositoryTest extends AbstractTest {
    @Autowired
    private RoleRepository roleDao;

    @Test
    public void whenFindByName_thenReturnRole() {
        Role role = createRole();
        Role found = roleDao.findRoleByName(role.getName());
        assertThat(found.getName()).isEqualTo(role.getName());
    }
}