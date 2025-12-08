package com.example._th_assignment.ApiController;

import com.example._th_assignment.Dto.Request.RequestUserDto;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Security.CustomUserDetails;
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


@WebMvcTest(UserApiController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService; // Controller의 의존성 Mocking

    @MockitoBean
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공, 200_반환")
    void register_Success() throws Exception {


        //given
        RequestUserDto requestUser = RequestUserDto.builder()
                .nickname("닉네임")
                .email("e@example.com")
                .password("Mypassword1!")
                .checkingpassword("Mypassword1!")
                .image("image")
                .build();


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
                .andExpect(jsonPath("$.data.nickname").value("닉네임"))
                .andExpect(jsonPath("$.data.email").value("e@example.com"))
                        .andExpect(jsonPath("$.data.image").value("image"));


        verify(userService).saveUser(any(RequestUserDto.class));
    }

    @Test
    @DisplayName("회원가입 실패, 400반환, 규칙에 맞지않는 json 요소")
    void register_validation() throws Exception {

        RequestUserDto requestUser = RequestUserDto.builder()
                .nickname("")
                .email("e@exmaple.com")
                .password("Mypassword1!")
                .checkingpassword("Mypassword1!")
                .image("image")
                .build();

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
        UserDto userProperty = new UserDto("닉네임",
                "exam@example.com",
                "Mypassword1!",
                "img_url");

        CustomUserDetails userDetails = new CustomUserDetails(userProperty);
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );







        //when + then
        mockMvc.perform(get("/api/user").principal(auth))
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
        RequestUserDto requestUser = RequestUserDto.builder()
                .nickname("닉네임")
                .email("e@exmaple.com")
                .password("Mypassword1!")
                .checkingpassword("Mypassword1!")
                .image("image")
                .build();

        UserDto oldUserProperty = new UserDto("닉네임11",
                "exam@example.com222",
                "Mypassword1!",
                "img_url333");

        UserDto newUserProperty = new UserDto("닉네임",
                "exam@example.com",
                "Mypassword1!",
                "img_url");

        CustomUserDetails userDetails = new CustomUserDetails(oldUserProperty);

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );


        String content = objectMapper.writeValueAsString(requestUser);


        //when
        when(userService.updateUser(any(RequestUserDto.class), any(UserDto.class))).thenReturn(newUserProperty);


        //when+then
        mockMvc.perform(put("/api/user").principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("update success"))
                .andExpect(jsonPath("$.data.nickname").value(newUserProperty.getNickname()))
                .andExpect(jsonPath("$.data.email").value(newUserProperty.getEmail()))
                .andExpect(jsonPath("$.data.image").value(newUserProperty.getImage()));

        verify(userService).updateUser(any(), any());

    }



    @Test
    @DisplayName("유저정보 갱신 실패, 400 반환, 맞지않는 json 요소")
    void updateUserProperty_failure_validation() throws Exception {
        RequestUserDto requestUser = RequestUserDto.builder()
                .nickname("")
                .image("image")
                .build();



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

        RequestUserDto requestUser = RequestUserDto.builder()
                .nickname("")
                .email("e@exmaplecom")
                .password("Mypassword1!")
                .checkingpassword("Mypassword1!")
                .image("image")
                .build();

        CustomUserDetails userDetails = new CustomUserDetails(new UserDto());
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );



        String content = objectMapper.writeValueAsString(requestUser);
        when(userService.updateUserPassword(any(RequestUserDto.class), any(UserDto.class))).thenReturn(new UserDto());

        mockMvc.perform(put("/api/user/password")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk());

        verify(userService).updateUserPassword(any(RequestUserDto.class), any(UserDto.class));
    }

    @Test
    @DisplayName("유저정보(비밀번호) 실패, 400반환, 확인용비밀번호 == null")
    void updateUserPassword_failiure_validation() throws Exception {

        RequestUserDto requestUser = RequestUserDto.builder()
                .nickname("")
                .email("e@exmaplecom")
                .password("Mypassword1!")
                .checkingpassword("")
                .image("image")
                .build();





        String content = objectMapper.writeValueAsString(requestUser);

        mockMvc.perform(put("/api/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest());

    }

    @Test
    @DisplayName("유저정보 삭제, 204반환")
    void deleteUserProperty_success() throws Exception {

        CustomUserDetails userDetails = new CustomUserDetails(new UserDto());
        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        mockMvc.perform(delete("/api/user").principal(auth))
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






    //controller는 검증, 전달, 서비스 결과값만 반환, json 매칭




}