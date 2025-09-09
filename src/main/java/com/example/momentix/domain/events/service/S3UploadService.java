package com.example.momentix.domain.events.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3UploadService {

    // 1. S3Config에서 만든 S3 연결 객체(Bean)를 주입
    private final AmazonS3Client amazonS3Client;

    // 2. application.properties에 설정한 버킷 이름을 가져옴
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // 3. 파일과 디렉토리 이름을 받음
    public String upload(MultipartFile file, String dirName) throws IOException {

        // 4. 파일명이 중복되지 않도록 고유한 파일명을 생성
        String fileName = dirName + "/" + UUID.randomUUID() + "-" + file.getOriginalFilename();

        // 5. 파일의 메타데이터를 생성
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        // 6. S3에 실제로 파일을 업로드
        amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

        // 7. 파일의 최종 URL을 반환
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }
}

