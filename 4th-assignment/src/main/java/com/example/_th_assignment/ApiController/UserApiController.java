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
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserApiController {

    private final UserService userService;
    private final SessionManager sessionManager;
    private final FileStorageService fileStorageService;
    private final String USER = "user";

    @Autowired
    public UserApiController (UserService userService,
                              SessionManager sessionManager,
                              FileStorageService fileStorageService) {
        this.userService = userService;
        this.sessionManager = sessionManager;
        this.fileStorageService = fileStorageService;

    }


//    @PostMapping("/session")
//    public ResponseEntity<Object> login(
//            @Validated(ValidationGroup.Login.class) @RequestBody UserDto tryuser,
//                                   HttpServletRequest request) {
//        UserDto user = userService.checkUser(tryuser.getEmail(), tryuser.getPassword());
//        HttpSession session = request.getSession(false);
//        if (session != null) {
//            session.removeAttribute("user");
//            session.invalidate();
//        }
//        session = request.getSession();
//        session.setAttribute("user", user);
//        return ResponseEntity.
//                status(HttpStatus.OK)
//                .body(ApiResponse.success("login success", "good"));
//    }
//    @DeleteMapping("/session")
//    public ResponseEntity<Object> logout(HttpServletRequest request) {
//        HttpSession session = sessionManager.access2Auth(request);
//        session.invalidate();
//
//        return ResponseEntity.
//                status(HttpStatus.OK).
//                body(ApiResponse.success("logout success"));
//    }

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
            HttpServletRequest request) {
        HttpSession session = sessionManager.access2Auth(request);
        UserDto user = (UserDto) session.getAttribute("user");


        user = userService.updateUser(newuser,user);
        session.setAttribute("user", user);

        return ResponseEntity.ok(ApiResponse.success("update success", user));

    }

    @PutMapping("/password")
    public ResponseEntity<?> updateUserPassword(
            @Validated(ValidationGroup.UpdatePassword.class) @RequestBody RequestUserDto newuser,
            HttpServletRequest request
    ){

        HttpSession session = sessionManager.access2Auth(request);

        UserDto user = (UserDto) session.getAttribute("user");




        user = userService.updateUserPassword(newuser,user);

        session.setAttribute("user", user);

        return ResponseEntity.ok().body(ApiResponse.success("password updated", user));

    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteUser(HttpServletRequest request) {
        HttpSession session = sessionManager.access2Auth(request);

        UserDto user = (UserDto) session.getAttribute("user");
        userService.deleteUser(user);
        session.removeAttribute("user");
        session.invalidate();
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
