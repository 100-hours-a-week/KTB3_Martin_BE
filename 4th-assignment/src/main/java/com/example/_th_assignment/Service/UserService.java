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

    @Autowired
    public UserService(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    public User findByEmail(String email){
        return userJpaRepository.findByEmailAndIsdeletedFalse(email)
                .orElseThrow(() -> new UserUnAuthorizedException(email));


    }

    @Transactional
    public UserDto saveUser (UserDto userDto){
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
    public UserDto updateUser(String useremail, UserDto newuser) {
        User user = findByEmail(useremail);
        user.updateUser(newuser);

        return user.toUserDto();
    }
    @Transactional
    public UserDto updateUserPassword(String useremail, UserDto newuser) {
        User user = findByEmail(useremail);
        String password = BCrypt.hashpw(newuser.getPassword(), BCrypt.gensalt());
        newuser.setPassword(password);
        user.changePwd(password);
        return user.toUserDto();
    }

    @Transactional
    public void deleteUser(UserDto user){
        User userentity = findByEmail(user.getEmail());
        userentity.delete();
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

    public UserDto apply2UserForPassword(RequestUserDto requestUserDto, UserDto user){
        String nickname = user.getNickname();
        String email = user.getEmail();
        String password = requestUserDto.getPassword();
        String image = user.getImageurl();

        return new UserDto(nickname, email, password, image);
    }

    public boolean existemail(String email){
        return userJpaRepository.existsByEmail(email);
    }

    public boolean existnickname(String nickname){
        return userJpaRepository.existsByNickname(nickname);
    }
}
