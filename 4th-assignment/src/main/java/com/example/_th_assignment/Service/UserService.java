package com.example._th_assignment.Service;

import com.example._th_assignment.CustomException.DtoNotFoundException;
import com.example._th_assignment.CustomException.UserConflictException;
import com.example._th_assignment.CustomException.UserNicknameConflictException;
import com.example._th_assignment.CustomException.UserUnAuthorizedException;
import com.example._th_assignment.Dto.Request.RequestUserDto;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Entity.User;
import com.example._th_assignment.JpaRepository.UserJpaRepository;
import com.example._th_assignment.Service.Mapper.UserMapper;

import org.apache.coyote.BadRequestException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final UserJpaRepository userJpaRepository;
    private final Validator validator;
    private final FileStorageService fileStorageService;

    @Autowired
    public UserService(UserJpaRepository userJpaRepository,
                       Validator validator,
                       FileStorageService fileStorageService) {
        this.validator = validator;
        this.userJpaRepository = userJpaRepository;
        this.fileStorageService = fileStorageService;
    }

    public User findByEmail(String email){
        return userJpaRepository.findByEmailAndIsdeletedFalse(email)
                .orElseThrow(() -> new UserUnAuthorizedException(email));


    }

    @Transactional
    public UserDto saveUser (RequestUserDto request){
        validator.checkValidNickname(request);
        validator.checkValidPassword(request);
        UserDto userDto = UserMapper.apply2User(request);

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
        UserDto userdto = UserMapper.apply2UserForUpdate(request, sessionUser);
        User user = findByEmail(userdto.getEmail());
        fileStorageService.deleteImage(user.getImage_path());
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
        fileStorageService.deleteImage(user.getImage_path());
    }




    @Transactional(readOnly = true)
    public boolean existemail(String email){
        if(email == null ||email.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "email is empty");
        return userJpaRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existnickname(String nickname){
        if(nickname == null || nickname.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST , "nickname is empty");
        return userJpaRepository.existsByNickname(nickname);
    }
}
