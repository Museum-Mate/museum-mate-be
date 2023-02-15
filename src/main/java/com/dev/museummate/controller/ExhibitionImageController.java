package com.dev.museummate.controller;

import com.dev.museummate.domain.dto.Response;
//import com.dev.museummate.domain.dto.exhibition.ExhibitionFileUrlResponse;
import com.dev.museummate.service.ExhibitionImageService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@Slf4j
@RestController
@RequestMapping("/api/v1/exhibitions/images")
@RequiredArgsConstructor
public class ExhibitionImageController {

  private final ExhibitionImageService exhibitionImageService;
    /*
        DB
        -main_img_url
        -notice_img_url
        -detail_info_img_url
        -detail_img_url

        Entity
        - mainImgUrl;
        - noticeImgUrl;
        - detailInfoImgUrl;
     */

  @ApiResponse(description = "대표 이미지(Main Image)")
  @PostMapping(path = "/main", consumes = {"multipart/form-data"}, headers = ("content-type=multipart/*"))
  @ResponseBody
  public Response<String> uploadMainImage(
      @RequestParam("mainImg") MultipartFile multipartFile, Authentication authentication) throws IOException, MissingServletRequestPartException {
    String email = authentication.getName();
    String fileUrl = exhibitionImageService.uploadAndSaveToDB(email, multipartFile);
//    exhibitionImageService.SaveMainImgToDB(response.getExhibitionId(), response.getS3ImageUrl());
    return Response.success(fileUrl);
  }
/*
=======================================================================================
 */

    @ApiResponse(description = "안내사항 이미지(Notice Image)")
//    path
    @PostMapping(path = "/notice", consumes = {
      "multipart/form-data"}, headers = ("content-type=multipart/*"))
    @ResponseBody
    public Response<String> uploadNoticeImage(
      @RequestParam("noticeImg") MultipartFile multipartFile, Authentication authentication)
      throws IOException, MissingServletRequestPartException {
    String email = authentication.getName();
    String fileUrl = exhibitionImageService.uploadAndSaveToDB(email, multipartFile);
    return Response.success(fileUrl);
  }

/*
=======================================================================================
 */

    @ApiResponse(description = "상세정보 이미지(Detail Info Image)")
//    경로 수정
    @PostMapping(path = "/detailInfo", consumes = {
        "multipart/form-data"}, headers = ("content-type=multipart/*"))
    @ResponseBody
    public Response<String> uploadDetailInfoImage(
        @RequestParam("detailInfoImg") MultipartFile multipartFile, Authentication authentication)
      throws IOException, MissingServletRequestPartException {
      String email = authentication.getName();
      String fileUrl = exhibitionImageService.uploadAndSaveToDB(email, multipartFile);
      return Response.success(fileUrl);
  }
}
