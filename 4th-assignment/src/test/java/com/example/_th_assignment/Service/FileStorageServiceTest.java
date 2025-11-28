package com.example._th_assignment.Service;

import com.example._th_assignment.Service.FileStorageService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

class FileStorageServiceTest {

    @Test
    void deleteImageTest() throws Exception {

        // FileStorageService는 @Service지만 new로 사용해도 문제 없음
        FileStorageService service = new FileStorageService();

        // 테스트용 디렉토리
        String dir = "uploads/test/";
        Files.createDirectories(Paths.get(dir));

        // 테스트 파일 생성
        String fileName = "testfile.png";
        Path testPath = Paths.get(dir, fileName);
        Files.createFile(testPath);

        // deleteImage()용 URL 준비
        String url = "/uploads/test/" + fileName;

        // 메서드 호출
        service.deleteImage(url);

        // 파일이 삭제됐는지 검증
        Assertions.assertFalse(Files.exists(testPath));
    }
}
