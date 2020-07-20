package com.exadel.booking.user;

import com.exadel.booking.user.role.Role;
import com.exadel.booking.user.role.RoleDto;
import com.exadel.booking.user.role.RoleService;
import com.exadel.booking.utils.modelmapper.AMapper;
import lombok.RequiredArgsConstructor;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userDao;
    private final RoleService roleService;
    private final AMapper<User, UserDto> userMapper;
    private final AMapper<Role, RoleDto> roleMapper;

    public UserDto getUserById(UUID id) {
        return userMapper.toDto(findUserById(id));
    }

    public User findUserByUsername(String username) {
        return userDao.findUserByUsername(username);
    }

    public UserDto findUserByLastName(String lastName) {
        return userMapper.toDto(userDao.findUserByLastName(lastName));
    }

    public List<UserDto> getAllUsers() {
        return userMapper.toListDto(userDao.findAll());
    }

    public UserDto updateUser(UUID id, UserDto userDto) {
        User userInDB = findUserById(id);
        if (StringUtils.isNotBlank(userDto.getEmail())) {
            userInDB.setEmail(userDto.getEmail());
        }
        return userMapper.toDto(userDao.save(userInDB));
    }


    public UserDto editUsersRole(UUID id, RoleDto roleDto) {
        User userInBD = findUserById(id);
        userInBD.setRoles(
                Collections.singletonList(roleMapper.toEntity(roleService.getRoleByName(roleDto.getName()))));
        return userMapper.toDto(userDao.save(userInBD));
    }

    private User findUserById(UUID id) {
        return Optional.ofNullable(userDao.findUserById(id))
                .orElseThrow(() -> new EntityNotFoundException("there is no such user with id:" + id));
    }
}