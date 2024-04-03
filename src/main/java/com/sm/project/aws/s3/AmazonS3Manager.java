package com.sm.project.aws.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sm.project.config.AmazonConfig;
import com.sm.project.domain.image.Uuid;
import com.sm.project.repository.member.UuidRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AmazonS3Manager{

    private final AmazonS3 amazonS3;

    private final AmazonConfig amazonConfig;

    private final UuidRepository uuidRepository;

    public String uploadFile(String path, Uuid uuid, MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        String keyName = "";
        switch (path) {
            case "receipt":
                keyName = generateReceiptKeyName(uuid);
                break;

            default:
                keyName = "./" + uuid.getUuid();
        }

        try {
            amazonS3.putObject(new PutObjectRequest(amazonConfig.getBucket(), keyName, file.getInputStream(), metadata));
        } catch (IOException e) {
            log.error("error at AmazonS3Manager uploadFile : {}", (Object) e.getStackTrace());
        }

        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public String getUrl(String keyName) {
        keyName = amazonConfig.getReceiptPath() + '/' + keyName;
        return amazonS3.getUrl(amazonConfig.getBucket(), keyName).toString();
    }

    public String generateReceiptKeyName(Uuid uuid) {
        return amazonConfig.getReceiptPath() + '/' + uuid.getUuid();
    }

}
