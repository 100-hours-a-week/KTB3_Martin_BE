package com.example._th_assignment.ApiController;


import com.example._th_assignment.ApiResponse.ApiResponse;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.ApiController.ImageType.ImageType;
import com.example._th_assignment.Service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Controller
@RequestMapping("/api/images")
public class ImageApiController {

    private final FileStorageService fileStorageService;

    private static final String TYPE_POSTS = "posts";
    private static final String TYPE_PROFILES = "profiles";


    @Autowired
    public ImageApiController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }


    @PostMapping("/{type}")
    public ResponseEntity<?> postImage (@PathVariable String type,
                                         @RequestParam MultipartFile image) {
        ImageType imageType = ImageType.fromString(type);
        if (image.isEmpty()) return ResponseEntity.badRequest()
                .body(ApiResponse.failed("image cannot be empty", "image is empty"));

        String url = fileStorageService.saveImage(image, imageType.name().toLowerCase());
        return ResponseEntity.ok(Map.of("url", url));

    }



    public void deleteImage(HttpServletRequest request ) {
        HttpSession session = request.getSession(false);
        if (session != null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Session is null");
        UserDto userDto = (UserDto) session.getAttribute("user");

        String oldimageurl = userDto.getImage();

        fileStorageService.deleteImage(oldimageurl);
    }

    @PutMapping("/{type}")
    public ResponseEntity<?> updateImage(HttpServletRequest request,
                                         @PathVariable String type,
                                         @RequestParam MultipartFile image) {
        deleteImage(request);
        return postImage(type, image);
    }




}
