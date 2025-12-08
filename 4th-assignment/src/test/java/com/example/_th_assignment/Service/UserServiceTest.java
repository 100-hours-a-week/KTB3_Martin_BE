package com.example._th_assignment.Service;

import com.example._th_assignment.CustomException.UserConflictException;
import com.example._th_assignment.CustomException.UserNicknameConflictException;
import com.example._th_assignment.CustomException.UserUnAuthorizedException;
import com.example._th_assignment.Dto.Request.RequestUserDto;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Entity.User;
import com.example._th_assignment.JpaRepository.UserJpaRepository;
import com.example._th_assignment.Service.Mapper.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserJpaRepository userJpaRepository;

    @Mock
    private Validator validator;

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Spy
    private final UserMapper userMapper = new UserMapper();



    @InjectMocks
    private UserService userService;





    @Test
    @DisplayName("이메일로 유저찾기_성공")
    void findByEmail_success(){
        String email = "email";

        User user = new User();
        when(userJpaRepository.findByEmailAndIsdeletedFalse(email)).thenReturn(Optional.of(user));

        User result = userService.findByEmail(email);


        assertEquals(user, result);
        verify(userJpaRepository).findByEmailAndIsdeletedFalse(email);

    }

    @Test
    @DisplayName("이메일로 유저찾기_실패")
    void findByEmail_failure(){
        String email = "email";



        when(userJpaRepository.findByEmailAndIsdeletedFalse(email)).thenReturn(Optional.empty());


        assertThrows(UserUnAuthorizedException.class, ()->{
            userService.findByEmail(email);
        });
        verify(userJpaRepository).findByEmailAndIsdeletedFalse(email);
    }

    @Test
    @DisplayName("유저저장_성공")
    //중복검사와 세이브는 반드시 이루어져야 한다.
    void saveUser_success(){

        //given
        RequestUserDto requestUSerDto = new RequestUserDto();
        requestUSerDto.setEmail("email");
        requestUSerDto.setPassword("password");
        requestUSerDto.setNickname("nickname");
        requestUSerDto.setCheckingpassword("password");

        UserDto expected = UserDto.builder()
                .nickname("nickname")
                .password("passwordEN")
                .email("email")
                .build();

        when(userJpaRepository.existsByEmail(anyString())).thenReturn(false);
        when(userJpaRepository.existsByNickname(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("passwordEN");

        //when
        UserDto result = userService.saveUser(requestUSerDto);


        //then
        assertThat(result.getEmail()).isEqualTo(expected.getEmail());
        assertThat(result.getNickname()).isEqualTo(expected.getNickname());
        assertThat(result.getPassword()).isEqualTo(expected.getPassword());

        verify(userJpaRepository).existsByEmail(anyString());
        verify(userJpaRepository).existsByNickname(anyString());
        verify(userJpaRepository).save(any());

    }


    @Test
    @DisplayName("유저생성 실패 : validator 검출(부적절한 닉네임, 비밀번호 mismatch")
    //validator 이후에 모든 로직은 끊김
    void saveUser_failure_validator(){
        RequestUserDto requestUSerDto = new RequestUserDto();


        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "nickname cannot be unknown"))
                .when(validator).checkSignUpUser(any(RequestUserDto.class));

        assertThrows(ResponseStatusException.class, () ->
                userService.saveUser(requestUSerDto));

        verify(validator).checkSignUpUser(any(RequestUserDto.class));
        verify(userJpaRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userMapper, never()).apply2User(any());
    }


    @Test
    @DisplayName("유저생성 실패: 중복 email ")
    void saveUser_failure_duplicate(){
        RequestUserDto requestUSerDto = new RequestUserDto();
        requestUSerDto.setEmail("email");
        requestUSerDto.setPassword("password");
        requestUSerDto.setNickname("nickname");
        requestUSerDto.setCheckingpassword("password");




        when(userJpaRepository.existsByEmail(anyString())).thenReturn(true);


        assertThrows(UserConflictException.class,  () ->{
            userService.saveUser(requestUSerDto);
        });

        verify(userJpaRepository, never()).save(any());
    }

    @Test
    @DisplayName("유저생성 실패: 중복 nickname ")
    void saveUser_failure_duplicate_nickname(){
        RequestUserDto requestUSerDto = new RequestUserDto();
        requestUSerDto.setEmail("email");
        requestUSerDto.setPassword("password");
        requestUSerDto.setNickname("nickname");
        requestUSerDto.setCheckingpassword("password");



        when(userJpaRepository.existsByEmail(anyString())).thenReturn(false);
        when(userJpaRepository.existsByNickname(anyString())).thenReturn(true);


        assertThrows(UserNicknameConflictException.class,  () ->{
            userService.saveUser(requestUSerDto);
        });

        verify(userJpaRepository, never()).save(any());
    }


    @Test
    @DisplayName("유저정보 수정 성공")
    void updateUser_Success(){
        RequestUserDto requestUserDto = new RequestUserDto();
        requestUserDto.setEmail("email");
        requestUserDto.setNickname("nickname");
        requestUserDto.setImage("image");

        UserDto expected = UserDto.builder()
                .email("email")
                .nickname("nickname")
                .image("image")
                .build();

        doNothing().when(validator).checkValidNickname(any(RequestUserDto.class));
        when(userJpaRepository.findByEmailAndIsdeletedFalse(anyString()))
                .thenReturn(Optional.of(new User()));

        UserDto userDtoForEmail = UserDto.builder()
                .email("email")
                .build();

        UserDto result = userService.updateUser(requestUserDto, userDtoForEmail);



        assertThat(result.getNickname()).isEqualTo(expected.getNickname());
        assertThat(result.getImage()).isEqualTo(expected.getImage());

        verify(userJpaRepository).findByEmailAndIsdeletedFalse(anyString());


    }

    @Test
    @DisplayName("유저정보 수정 실패_부적절한 닉네임")
    void updateUser_failure_nickname() {
        RequestUserDto requestUserDto = new RequestUserDto();
        requestUserDto.setEmail("email");
        requestUserDto.setNickname("nickname");
        requestUserDto.setImage("image");


        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "nickname cannot be unknown"))
                .when(validator).checkValidNickname(any(RequestUserDto.class));


        assertThrows(ResponseStatusException.class, () -> {
            userService.updateUser(requestUserDto, new UserDto());
        });


        verify(userJpaRepository, never()).findByEmailAndIsdeletedFalse(anyString());
        verify(fileStorageService, never()).deleteImage(anyString());

    }

    //Todo: 유저 비밀번호 수정, 유저 삭제

    @Test
    @DisplayName("유저 비밀번호 수정 성공")
    void updateUser_Password_Success(){

        //given
        RequestUserDto requestUserDto = new RequestUserDto();
        requestUserDto.setPassword("password");
        requestUserDto.setCheckingpassword("password");

        UserDto expected = UserDto.builder()
                        .password("passwordEncoded").build();

        doNothing().when(validator).checkValidPassword(any(RequestUserDto.class));
        when(userJpaRepository.findByEmailAndIsdeletedFalse(anyString()))
                .thenReturn(Optional.of(new User()));
        when(passwordEncoder.encode(anyString())).thenReturn("passwordEncoded");
        UserDto userDtoForEmail = UserDto.builder()
                .email("email")
                .build();

        //when
        UserDto result = userService.updateUserPassword(requestUserDto, userDtoForEmail);



        //then
        assertThat(result.getPassword()).isEqualTo(expected.getPassword());
        verify(userJpaRepository).findByEmailAndIsdeletedFalse(anyString());
    }

    @Test
    @DisplayName("유저 비밀번호 수정 실패_확인 비밀번호과 비밀번호가 맞지않음")
    void updateUser_Password_Failure_Mismatch(){

        //given
        RequestUserDto requestUserDto = new RequestUserDto();
        doThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "password and checkingpassword are not same"))
                .when(validator).checkValidPassword(any(RequestUserDto.class));

        //then
        assertThrows(ResponseStatusException.class, () -> {
            userService.updateUserPassword(requestUserDto, new UserDto());
        });
        verify(userJpaRepository, never()).findByEmailAndIsdeletedFalse(anyString());
    }

    @Test
    @DisplayName("유저 비밀번호 수정 실패_이메일로 계정을 찾을수 없음")
    void updateUser_Password_Failure_Not_Found(){

        //given
        RequestUserDto requestUserDto = new RequestUserDto();
        requestUserDto.setPassword("password");
        requestUserDto.setCheckingpassword("password");

        UserDto userDtoForEmail = UserDto.builder()
                .email("email")
                .build();


        doNothing().when(validator).checkValidPassword(any(RequestUserDto.class));
        when(userJpaRepository.findByEmailAndIsdeletedFalse(anyString()))
                .thenReturn(Optional.empty());


        //when
//        UserDto result = userService.updateUserPassword(requestUserDto, userDtoForEmail);



        //then
        assertThrows(UserUnAuthorizedException.class, () ->{
            userService.updateUserPassword(requestUserDto, userDtoForEmail);
        });
        verify(validator).checkValidPassword(any(RequestUserDto.class));
    }


    @Test
    @DisplayName("유저정보 삭제 성공")
    void deleteUser_Success(){

        UserDto userDto = UserDto.builder()
                .email("email")
                .image("image")
                .build();





        User spyUser = spy(new User());
        spyUser.setEmail("email");
        spyUser.setImage_path("image");



        when(userJpaRepository.findByEmailAndIsdeletedFalse(anyString()))
                .thenReturn(Optional.of(spyUser));



        userService.deleteUser(userDto);


        assertThat(spyUser.getIsdeleted()).isTrue();

        InOrder inOrder = Mockito.inOrder(userJpaRepository, spyUser, fileStorageService);
        inOrder.verify(userJpaRepository).findByEmailAndIsdeletedFalse(anyString());
        inOrder.verify(spyUser).delete();
        inOrder.verify(fileStorageService).deleteImage(spyUser.getImage_path());
    }

    @Test
    @DisplayName("유저정보 삭제 실패_인증되지 않음")
    void deleteUser_Failure_UnAuthorized(){
        UserDto userDto = UserDto.builder()
                .email("email")
                .build();

        when(userJpaRepository.findByEmailAndIsdeletedFalse(anyString()))
                .thenThrow(UserUnAuthorizedException.class);

        User spyUser = spy(new User());


        assertThrows(UserUnAuthorizedException.class, () ->{
            userService.deleteUser(userDto);
        });

        verify(fileStorageService, never()).deleteImage(spyUser.getImage_path());



    }






}