package com.example._th_assignment.Service;

import com.example._th_assignment.CustomException.UserConflictException;
import com.example._th_assignment.CustomException.UserNicknameConflictException;
import com.example._th_assignment.CustomException.UserUnAuthorizedException;
import com.example._th_assignment.Dto.Request.RequestUserDto;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Entity.User;
import com.example._th_assignment.JpaRepository.UserJpaRepository;
import jakarta.transaction.Transactional;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserJpaRepository userJpaRepository;
    private final Validator validator;

    @Autowired
    public UserService(UserJpaRepository userJpaRepository, Validator validator) {
        this.validator = validator;
        this.userJpaRepository = userJpaRepository;
    }

    public User findByEmail(String email){
        return userJpaRepository.findByEmailAndIsdeletedFalse(email)
                .orElseThrow(() -> new UserUnAuthorizedException(email));


    }

    @Transactional
    public UserDto saveUser (RequestUserDto request){
        validator.checkValidNickname(request);
        validator.checkValidPassword(request);
        UserDto userDto = apply2User(request);

        if(userJpaRepository.existsByEmail(userDto.getEmail())){
            throw new UserConflictException(userDto.getEmail());
        }
        if(userJpaRepository.existsByNickname(userDto.getNickname())){
            throw new UserNicknameConflictException(userDto.getNickname());
        }

        String password = BCrypt.hashpw(userDto.getPassword(), BCrypt.gensalt());
        userDto.setPassword(password);
        User user= User.from(userDto);
        userJpaRepository.save(user);
        return userDto;
    }

    @Transactional
    public UserDto updateUser(RequestUserDto request, UserDto sessionUser) {
        validator.checkValidNickname(request);
        UserDto userdto = apply2UserForUpdate(request, sessionUser);
        User user = findByEmail(userdto.getEmail());
        user.updateUser(userdto);

        return user.toUserDto();
    }

    @Transactional
    public UserDto updateUserPassword(RequestUserDto request, UserDto sessionUser) {


        validator.checkValidPassword(request);
        User user = findByEmail(sessionUser.getEmail());

        String password = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        user.changePwd(password);
        return user.toUserDto();
    }

    @Transactional
    public void deleteUser(UserDto userdto){
        User user = findByEmail(userdto.getEmail());

        user.delete();
    }

    public UserDto checkUser(String username, String password) {
        User user = findByEmail(username);
        if(!BCrypt.checkpw(password, user.getPassword()))
            throw new UserUnAuthorizedException(username);
        return user.toUserDto();
    }
    public UserDto apply2User(RequestUserDto requestUserDto) {
        return new UserDto(requestUserDto);
    }

    public UserDto apply2UserForUpdate(RequestUserDto requestUserDto, UserDto user){
        String nickname = requestUserDto.getNickname();
        String email = user.getEmail();
        String password = user.getPassword();
        String image = requestUserDto.getImage();
        return new UserDto(nickname, email, password, image);
    }


    public boolean existemail(String email){
        return userJpaRepository.existsByEmail(email);
    }

    public boolean existnickname(String nickname){
        return userJpaRepository.existsByNickname(nickname);
    }
}
