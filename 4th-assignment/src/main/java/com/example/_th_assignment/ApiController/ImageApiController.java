package com.example._th_assignment.ApiController;


import com.example._th_assignment.ApiController.ImageType.ImageType;
import com.example._th_assignment.ApiResponse.ApiResponse;
import com.example._th_assignment.CustomAnnotation.LoginUser;
import com.example._th_assignment.Dto.UserDto;
import com.example._th_assignment.Service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Controller
@RequestMapping("/api/images")
public class ImageApiController {

    private final FileStorageService fileStorageService;

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



    public void deleteImage(@LoginUser UserDto userDto) {

        fileStorageService.deleteImage(userDto.getEmail());
    }

    @PutMapping("/{type}")
    public ResponseEntity<?> updateImage(@LoginUser UserDto user,
                                         @PathVariable String type,
                                         @RequestParam MultipartFile image) {
        deleteImage(user);
        return postImage(type, image);
    }




}
