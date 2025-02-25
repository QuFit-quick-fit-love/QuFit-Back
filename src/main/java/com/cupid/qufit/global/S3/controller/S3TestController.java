package com.cupid.qufit.global.S3.controller;

import com.cupid.qufit.global.S3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/*
* * S3Service 메소드
* */
@RestController
@RequiredArgsConstructor
@RequestMapping("/test/s3")
@Log4j2
public class S3TestController {

    private final S3Service s3Service;

    /*
    * * s3 에 프로필 사진을 업로드함
    *
    * @param : file 업로드할 사진
    * TODO : 현재는 프로필 사진만 업로드할 예정이므로 foldername은 "profile"로 고정, 추후 변경 필요
    * */
    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestPart(value="file",required = false)  MultipartFile file) {
        log.info("------upload file---------");
        log.info("[file] : " + file.getOriginalFilename());
        String profileImage = s3Service.upload(file, "profile");
        return new ResponseEntity<>(profileImage, HttpStatus.OK);
    }

    /*
    * * s3에 있는 파일 삭제
    *
    * @param : fileUrl s3에 업로드된 사진의 url
    * */
    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(@RequestParam("file") String fileUrl) {
        log.info("------delete file---------");
        s3Service.deleteFile(fileUrl);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
