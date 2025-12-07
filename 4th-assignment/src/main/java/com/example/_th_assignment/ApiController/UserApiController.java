package com.example._th_assignment.ApiController;

import com.example._th_assignment.ApiResponse.ApiResponse;
import com.example._th_assignment.Dto.Request.RequestUserDto;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Dto.ValidationGroup;
import com.example._th_assignment.Security.CustomUserDetails;
import com.example._th_assignment.Service.FileStorageService;
import com.example._th_assignment.Service.SessionManager;
import com.example._th_assignment.Service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserApiController {

    private final UserService userService;

    private final FileStorageService fileStorageService;
    private final String USER = "user";

    @Autowired
    public UserApiController (UserService userService,
                              FileStorageService fileStorageService) {
        this.userService = userService;
        this.fileStorageService = fileStorageService;

    }



    @PostMapping
    @Operation(summary = "회원 가입", description = "같은 이메일이 없다면 회원가입 가능")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 있는 이메일")
    })
    public ResponseEntity<Object> register(
            @Validated(ValidationGroup.Register.class) @RequestBody RequestUserDto newuser){

        UserDto user = userService.saveUser(newuser);
        return ResponseEntity.ok(ApiResponse.success("register success", user));

    }

    @GetMapping
    public ResponseEntity<Object> getUserProperty(Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        UserDto user = customUserDetails.getUser();
        return ResponseEntity.ok(ApiResponse.success("user found", user));

    }

    @PutMapping
    public ResponseEntity<Object> updateUser(
            @Validated(ValidationGroup.UpdateProperty.class ) @RequestBody RequestUserDto newuser,
            Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        UserDto user = customUserDetails.getUser();

        user = userService.updateUser(newuser,user);
        CustomUserDetails newDetails = new CustomUserDetails(user);
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                newDetails,
                null,
                newDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);

        return ResponseEntity.ok(ApiResponse.success("update success", user));

    }

    @PutMapping("/password")
    public ResponseEntity<?> updateUserPassword(
            @Validated(ValidationGroup.UpdatePassword.class) @RequestBody RequestUserDto newuser,
            Authentication authentication
    ){
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        UserDto user = customUserDetails.getUser();

        user = userService.updateUserPassword(newuser,user);
        CustomUserDetails newDetails = new CustomUserDetails(user);
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                newDetails,
                null,
                newDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);


        return ResponseEntity.ok().body(ApiResponse.success("password updated", user));

    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteUser(Authentication authentication,
                                                          HttpServletRequest request,
                                                          HttpServletResponse response) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        UserDto user = customUserDetails.getUser();


        userService.deleteUser(user);
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();


        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return ResponseEntity.noContent().build();

    }


    @GetMapping("/email-conflict")
    public ResponseEntity<?> existEmail(@RequestParam(value = "email") String email) {
        boolean exists = userService.existemail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/nickname-conflict")
    public ResponseEntity<?> existNickname(@RequestParam(value = "nickname") String name) {
        boolean exists = userService.existnickname(name);
        return ResponseEntity.ok(exists);
    }

    @PostMapping("/profile")
    public ResponseEntity<?> uploadProfile(@RequestParam("image") MultipartFile image) {

        if (image.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "empty file"));
        }


        String url = fileStorageService.saveImage(image, "profile");

        return ResponseEntity.ok(Map.of("url", url));
    }





}
