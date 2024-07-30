package com.cupid.qufit.global.S3.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.cupid.qufit.global.exception.ErrorCode;
import com.cupid.qufit.global.exception.exceptionType.S3Exception;
import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Transactional
@Log4j2
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucketName}")
    private String bucketName;

    /*
     * * s3에 파일 업로드함
     *
     * @param image s3에 올릴 파일
     * @param folerName 폴더 이름
     * - 현재 없는 폴더라면 새로 생성됩니다.
     * @return : s3에 저장된 image의 url
     * */
    public String upload(MultipartFile image, String folerName) {
        // 입력받은 이미지 파일이 빈 파일인지 검증
        if (image.isEmpty() || Objects.isNull(image.getOriginalFilename())) {
            throw new S3Exception(ErrorCode.EMPTY_FILE);
        }
        // 파일 확장자면 검증
        this.validateFileExtention(image.getOriginalFilename());
        // uploadImage를 호출하여 S3에 저장된 이미지의 public url을 반환
        return this.uploadFileToS3(image, folerName);
    }

    /*
     * * 파일 확장자명 검사
     *
     * @param image s3에 올릴 파일 이름
     * */
    private void validateFileExtention(String originalFilename) {
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            throw new S3Exception(ErrorCode.EMPTY_FILE_EXTENSION);
        }

        String extension = originalFilename.substring(lastDotIndex + 1).toLowerCase();
        List<String> allowedExtentionList = Arrays.asList("jpg", "jpeg", "png", "gif");

        if (!allowedExtentionList.contains(extension)) {
            throw new S3Exception(ErrorCode.INVALID_FILE_EXTENSION);
        }
    }

    /*
     * * 파일 S3에 업로드
     *
     * @param image s3에 올릴 파일
     * @param folerName 폴더 이름
     * */
    private String uploadFileToS3(MultipartFile file, String folerName) {
        log.info("--------upload file to s3--------");
        // 실제 업로드
        String originalFilename = file.getOriginalFilename(); // 원본 파일명
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")); // 확장자명
        String s3FileName =
                folerName + "/" + UUID.randomUUID().toString().substring(0, 10) + originalFilename; // 변경된 파일 명

        try (InputStream inputStream = file.getInputStream()) {

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // S3에 파일 올리기
            PutObjectRequest putObjectRequest =
                    new PutObjectRequest(bucketName, s3FileName, inputStream, metadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead);

            amazonS3.putObject(putObjectRequest);
            return amazonS3.getUrl(bucketName, s3FileName).toString();
        } catch (Exception e) {
            log.error("s3 upload : " + e.getMessage(), e);
            throw new S3Exception(ErrorCode.FILE_UPLOAD_FAILURE);
        }
    }

    /*
     * * S3에 업로드된 파일 삭제
     *
     * @param fileUrl s3에 저장된 image의 url
     * */
    public void deleteFile(String fileUrl) {
        log.info("--------delete file from s3--------");
        String keyName = this.getS3KeyFromFileUrl(fileUrl); // (형태) 폴더명/기존파일명.확장자명
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, keyName));
        } catch (Exception e) {
            throw new S3Exception(ErrorCode.FILE_DELETE_FAILURE);
        }
    }

    /*
     * * file url에서 폴더명 + 기존 파일명을 가져옵니다.
     *
     *  @param fileUrl s3에 저장된 image의 url
     * */
    private String getS3KeyFromFileUrl(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
            return decodingKey.substring(1);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            throw new S3Exception(ErrorCode.INVALID_FILE_URL);
        }
    }
}
