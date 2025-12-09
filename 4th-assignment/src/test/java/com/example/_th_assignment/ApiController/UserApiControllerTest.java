package com.example._th_assignment.ApiController;

import com.example._th_assignment.Dto.Request.RequestUserDto;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Security.CustomUserDetails;
import com.example._th_assignment.Service.AuthenticationProcessor;
import com.example._th_assignment.Service.FileStorageService;
import com.example._th_assignment.Service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



//Todo: SecurityContextHolder에 Authentication 저장하기
//다만 이부분이 반복되기에 setup을 할까 고민중
//메소드화?
@WebMvcTest(UserApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService; // Controller의 의존성 Mocking

    @MockitoBean
    private FileStorageService fileStorageService;

    @MockitoBean
    private AuthenticationProcessor authenticationProcessor;

    @Autowired
    private ObjectMapper objectMapper;






    @Test
    @DisplayName("회원가입 성공, 200_반환")
    void register_Success() throws Exception {


        //given
        RequestUserDto requestUser = createRequestUser("nickname");


        UserDto savedUser = new UserDto(requestUser);
        given(userService.saveUser(any(RequestUserDto.class))).willReturn(savedUser);


        //when
        String content = objectMapper.writeValueAsString(requestUser);
        ResultActions resultActions = mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)

        );

        //then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("register success"))
                .andExpect(jsonPath("$.data.nickname").value("nickname"));


        verify(userService).saveUser(any(RequestUserDto.class));
    }

    @Test
    @DisplayName("회원가입 실패, 400반환, 규칙에 맞지않는 json 요소")
    void register_validation() throws Exception {

        RequestUserDto requestUser = createRequestUser("");

        String content = objectMapper.writeValueAsString(requestUser);


        mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                        .andExpect(status().isBadRequest());


    }

    @Test
    @DisplayName("유저정보 획득 성공  200 반환")
    void getUserProperty_success() throws Exception {


        //given
        UserDto userProperty = createUser("nickname");
        createAuth(userProperty);



        //when + then
        mockMvc.perform(get("/api/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("user found"))
                .andExpect(jsonPath("$.data.nickname").value(userProperty.getNickname()))
                .andExpect(jsonPath("$.data.email").value(userProperty.getEmail()))
                .andExpect(jsonPath("$.data.image").value(userProperty.getImage()));

    }

    @Test
    @DisplayName("유저정보 갱신 성공 200 반환")
    void updateUserProperty_success() throws Exception {

        //given
        RequestUserDto updateRequest = createRequestUser("newnickname");

        UserDto oldUserProperty = createUser("oldnickname");
        UserDto expectedUserProperty = createUser("newnickname");

        createAuth(oldUserProperty);
        String content = objectMapper.writeValueAsString(updateRequest);
        when(userService.updateUser(any(RequestUserDto.class), any(UserDto.class))).thenReturn(expectedUserProperty);


        //when+then
        mockMvc.perform(put("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("update success"))
                .andExpect(jsonPath("$.data.nickname").value(expectedUserProperty.getNickname()))
                .andExpect(jsonPath("$.data.email").value(expectedUserProperty.getEmail()))
                .andExpect(jsonPath("$.data.image").value(expectedUserProperty.getImage()));

        verify(userService).updateUser(any(), any());

    }



    @Test
    @DisplayName("유저정보 갱신 실패, 400 반환, 맞지않는 json 요소")
    void updateUserProperty_failure_validation() throws Exception {
        RequestUserDto requestUser = createRequestUser("");



        String content = objectMapper.writeValueAsString(requestUser);

        mockMvc.perform(put("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }






    @Test
    @DisplayName("유저정보(비밀번호) 갱신, 200반환")
    void updateUserPassword_success() throws Exception {

        //given
        RequestUserDto requestUser = createRequestForPasswordUpdate("Mypassword1!");
        createAuth(new UserDto());
        String content = objectMapper.writeValueAsString(requestUser);
        when(userService.updateUserPassword(any(RequestUserDto.class), any(UserDto.class)))
                .thenReturn(new UserDto());

        //when+then
        mockMvc.perform(put("/api/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
        ;

        verify(userService).updateUserPassword(any(RequestUserDto.class), any(UserDto.class));
    }

    @Test
    @DisplayName("유저정보(비밀번호) 실패, 400반환, 확인용비밀번호 == null")
    void updateUserPassword_failiure_validation() throws Exception {

        RequestUserDto requestUser = createRequestForPasswordUpdate("Mypassword1!", "");





        String content = objectMapper.writeValueAsString(requestUser);

        mockMvc.perform(put("/api/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("유저정보 삭제, 204반환")
    void deleteUserProperty_success() throws Exception {

        createAuth(new UserDto());


        mockMvc.perform(delete("/api/user"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(any(UserDto.class));


    }


    @Test
    @DisplayName("이메일 존재유무 확인성공(O), 200반환")
    void existEmail_success_withTrue() throws Exception {


        when(userService.existemail(any(String.class))).thenReturn(true);


        mockMvc.perform(get("/api/user/email-conflict")
                .param("email" , "example@"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
        verify(userService).existemail(any(String.class));
    }

    @Test
    @DisplayName("이메일 존재유무 확인성공(X), 200반환")
    void existEmail_success_withFalse() throws Exception {


        when(userService.existemail(any(String.class))).thenReturn(false);


        mockMvc.perform(get("/api/user/email-conflict")
                        .param("email" , "example@"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
        verify(userService).existemail(any(String.class));
    }


    //헬퍼메서드

    RequestUserDto createRequestUser(String nickname){
        RequestUserDto requestUser = RequestUserDto.builder()
                .nickname(nickname)
                .email("e@example.com")
                .password("Mypassword1!")
                .checkingpassword("Mypassword1!")
                .image("image")
                .build();

        return requestUser;
    }

    RequestUserDto createRequestForPasswordUpdate(String password){

        return RequestUserDto.builder()
                .nickname("nickname")
                .email("e@example.com")
                .password(password)
                .checkingpassword(password)
                .image("image")
                .build();
    }
    RequestUserDto createRequestForPasswordUpdate(String password, String checkingpassword){

        return RequestUserDto.builder()
                .nickname("nickname")
                .email("e@example.com")
                .password(password)
                .checkingpassword(checkingpassword)
                .image("image")
                .build();
    }

    UserDto createUser(String nickname) {
        UserDto oldUserProperty = new UserDto(nickname,
                "exam@example.com222",
                "Mypassword1!",
                "img_url333");
        return oldUserProperty;

    }

    void createAuth(UserDto user){
        CustomUserDetails userDetails = new CustomUserDetails(user);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
    }











}