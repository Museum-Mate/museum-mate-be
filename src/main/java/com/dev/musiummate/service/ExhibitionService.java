package com.dev.musiummate.service;

import com.dev.musiummate.domain.dto.ExhibitionResponse;
import com.dev.musiummate.domain.entity.ExhibitionEntity;
import com.dev.musiummate.exception.AppException;
import com.dev.musiummate.exception.ErrorCode;
import com.dev.musiummate.repository.ExhibitionRepository;
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
