package com.dev.museummate.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
//import com.dev.museummate.domain.dto.exhibition.ExhibitionFileUrlResponse;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.ExhibitionRepository;
import com.dev.museummate.repository.UserRepository;
import com.sun.tools.attach.AttachOperationFailedException;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channel;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExhibitionImageService {
  private final AmazonS3Client amazonS3Client;
  private final ExhibitionRepository exhibitionRepository;
  private final UserRepository userRepository;
  private final ExhibitionService exhibitionService;

  @Value("${cloud.aws.s3.bucket}")
  private String bucket;

  @Value("${cloud.aws.s3.bucket.url}")
  private String defaultUrl;

  private final String directoryName = "/img";

  public UserEntity checkUserEmail(String email) {
    UserEntity userEntity = userRepository.findByEmail(email)
        .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND, "???????????? ?????? ??????????????????."));

    return userEntity;
  }

  public ExhibitionEntity checkExhibition(Long exhibitionId) {
    ExhibitionEntity exhibitionEntity = exhibitionRepository.findById(exhibitionId)
        .orElseThrow(() -> new AppException(ErrorCode.EXHIBITION_NOT_FOUND, "???????????? ?????? ???????????????."));

    return exhibitionEntity;
  }

  public String uploadAndSaveToDB(String email, MultipartFile multipartFile)
      throws MissingServletRequestPartException {

      // ?????? ??????
    if (multipartFile.isEmpty()) {
      throw new MissingServletRequestPartException("???????????? ???????????? ????????????!");
    }

    // user ??????
    UserEntity userEntity = checkUserEmail(email);
    log.info("????????? ?????? ??????: ", userEntity.getUserName());

    // metadata ??????
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType(multipartFile.getContentType());
    objectMetadata.setContentLength(multipartFile.getSize());

    // multipartFile ??????
    String uploadFileName = multipartFile.getOriginalFilename();
    log.debug("uploadFileName", uploadFileName);

    int index;

    try {
      index = uploadFileName.lastIndexOf(".");
    } catch (StringIndexOutOfBoundsException e) {
      throw new AppException(ErrorCode.DUPLICATE_EMAIL, "????????? ?????? ???????????????.");
    }

//    if (!userEntity.getUserName().equals(exhibitionEntity.getUser().getUserName())) {
//      throw new AppException(ErrorCode.INVALID_PERMISSION, "????????? ????????? ????????? ????????????.");
//    }

    String extension = uploadFileName.substring(index + 1);
    log.debug("extension:", extension);

    String awsS3FileName = UUID.randomUUID() + "." + extension;
    log.debug("awsS3FileName:", awsS3FileName);

    String key = "ExhibitionImage/" + awsS3FileName;
    log.debug("key:", key);

    try(InputStream inputStream = multipartFile.getInputStream()) {
      amazonS3Client.putObject(new PutObjectRequest(bucket, key, inputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));
    } catch (IOException e) {
      throw new AppException(ErrorCode.DATABASE_ERROR, "????????? ??????????????????.");
    }

    String fileUrl = amazonS3Client.getResourceUrl(directoryName, key);
    String imageName = FilenameUtils.getName(fileUrl);
    fileUrl = defaultUrl + "ExhibitionImage/" + imageName;
    log.debug("S3 url: ", fileUrl);

    return fileUrl;
  }

//  public void SaveMainImgToDB(Long exhibitionID, String S3ObejctUrl) {
//    ExhibitionEntity exhibitionEntity = exhibitionRepository.findById(exhibitionID)
//        .orElseThrow(() -> new AppException(ErrorCode.EXHIBITION_NOT_FOUND, "?????? ????????? ???????????? ????????????."));
//    exhibitionEntity.insertMainImgUrl(S3ObejctUrl);
//    exhibitionRepository.save(exhibitionEntity);
//    log.info("Main Image??? S3 ?????? Url??? DB??? ???????????? ????????????.");
//  }
//
//  public void SaveDetailInfoImgToDB(Long exhibitionID, String S3ObejctUrl) {
//    ExhibitionEntity exhibitionEntity = exhibitionRepository.findById(exhibitionID)
//        .orElseThrow(() -> new AppException(ErrorCode.EXHIBITION_NOT_FOUND, "?????? ????????? ???????????? ????????????."));
//    exhibitionEntity.insertDetailInfoImgUrl(S3ObejctUrl);
//    exhibitionRepository.save(exhibitionEntity);
//    log.info("Detail Information Image??? S3 ?????? Url??? DB??? ???????????? ????????????.");
//  }
//
//  public void SaveNoticeImgToDB(Long exhibitionID, String S3ObejctUrl) {
//    ExhibitionEntity exhibitionEntity = exhibitionRepository.findById(exhibitionID)
//        .orElseThrow(() -> new AppException(ErrorCode.EXHIBITION_NOT_FOUND, "?????? ????????? ???????????? ????????????."));
//    exhibitionEntity.insertNoticeImgUrl(S3ObejctUrl);
//    exhibitionRepository.save(exhibitionEntity);
//    log.info("Notice Image??? S3 ?????? Url??? DB??? ???????????? ????????????.");
//  }
}
