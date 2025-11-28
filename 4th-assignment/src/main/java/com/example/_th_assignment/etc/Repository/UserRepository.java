package com.example._th_assignment.etc.Repository;


import com.example._th_assignment.Dto.UserDto;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Optional;

@Repository
public class UserRepository {
    private final HashMap<String, UserDto> userStore;

    public UserRepository() {
        userStore = new HashMap<>();
        String password = BCrypt.hashpw("Mypassword1!", BCrypt.gensalt());
        UserDto user = new UserDto("dummy", "foo@bar", password, "");
        save(user);
    }

    public Optional<UserDto> getbyEmail(String useremail) {
        return Optional.ofNullable(userStore.get(useremail));
    }

    public Boolean isExists(String useremail) {
        return userStore.containsKey(useremail);
    }

    public UserDto save(UserDto userDto) {
        if(userStore.containsKey(userDto.getEmail()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "same email already exists");

        userStore.put(userDto.getEmail(), userDto);
        return userDto;
    }
    public UserDto update(String email, UserDto updateuser) {


        userStore.put(email, updateuser);
        return updateuser;
    }

    public UserDto updatePassword(String email, UserDto newuser) {
//        UserDto user =getbyEmail(email).orElseThrow(()->
//                new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));


        userStore.put(email, newuser);
        return newuser;
    }

    public void delete(UserDto user) {
        String email = user.getEmail();
        userStore.remove(email);
    }


}
