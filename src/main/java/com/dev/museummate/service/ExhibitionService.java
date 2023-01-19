package com.dev.museummate.service;

import com.dev.museummate.domain.dto.exhibition.ExhibitionResponse;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.ExhibitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;

    public ExhibitionResponse getOne(long exhibitionId) {
        ExhibitionEntity selectedExhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_POST, "존재하지 않는 게시물입니다."));

        return new ExhibitionResponse(
                selectedExhibition.getName(),
                selectedExhibition.getStartsAt(),
                selectedExhibition.getEndsAt(),
                selectedExhibition.getPrice(),
                selectedExhibition.getAgeLimit(),
                selectedExhibition.getDetailInfo(),
                selectedExhibition.getGalleryDetail(),
                selectedExhibition.getGallery().getId()
        );
    }

}
