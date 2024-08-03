package com.sm.project.service;

import com.sm.project.aws.s3.AmazonS3Manager;
import com.sm.project.domain.image.Uuid;
import com.sm.project.repository.member.UuidRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * UtilService는 다양한 유틸리티 기능을 제공하는 서비스 클래스입니다.
 * 주로 S3 이미지 업로드 기능을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class UtilService {

    private final UuidRepository uuidRepository;
    private final AmazonS3Manager s3Manager;

    /**
     * S3에 이미지를 업로드하는 메서드입니다.
     * 
     * @param path 업로드할 S3 경로
     * @param multipartFile 업로드할 이미지 파일
     * @return 업로드된 이미지의 URL
     */
    public String uploadS3Img(String path, MultipartFile multipartFile) {
        // UUID 생성
        String uuid = UUID.randomUUID().toString();
        
        // UUID를 데이터베이스에 저장
        Uuid saveUuid = uuidRepository.save(Uuid.builder()
                .uuid(uuid).build());
        
        // S3에 파일 업로드 및 URL 반환
        return s3Manager.uploadFile(path, saveUuid, multipartFile);
    }
}
