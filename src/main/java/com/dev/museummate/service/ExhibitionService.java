package com.dev.museummate.service;

import com.dev.museummate.domain.dto.exhibition.BookmarkAddResponse;
import com.dev.museummate.domain.dto.exhibition.BookmarkDto;
import com.dev.museummate.domain.dto.exhibition.ExhibitionDto;
import com.dev.museummate.domain.dto.exhibition.ExhibitionResponse;
import com.dev.museummate.domain.entity.BookmarkEntity;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.BookmarkRepository;
import com.dev.museummate.repository.ExhibitionRepository;
import com.dev.museummate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;

    // 전시 상세 조회
    public ExhibitionResponse getOne(long exhibitionId) {
        ExhibitionEntity selectedExhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_POST, "존재하지 않는 전시회입니다."));

        ExhibitionDto selectedExhibitionDto = ExhibitionDto.toDto(selectedExhibition);

        return new ExhibitionResponse(
                selectedExhibitionDto.getName(),
                selectedExhibitionDto.getStartsAt(),
                selectedExhibitionDto.getEndsAt(),
                selectedExhibitionDto.getPrice(),
                selectedExhibitionDto.getAgeLimit(),
                selectedExhibitionDto.getDetailInfo(),
                selectedExhibitionDto.getGalleryDetail(),
                selectedExhibitionDto.getGallery().getId()
        );
    }

    // 해당하는 exhibition을 Bookmark에 추가
    public BookmarkAddResponse addToBookmark(long exhibitionId, String email) {

        // 유저 네임 검증
        // TODO: 22.01.21 userName -> email로 변경
        UserEntity user = userRepository.findByUserName(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND, "존재하지 않는 유저입니다."));

        // 전시회 검증
        ExhibitionEntity selectedExhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_POST, "존재하지 않는 전시회입니다."));

        Optional<BookmarkEntity> selected = bookmarkRepository.findByExhibitionAndUser(selectedExhibition, user);

        if(!selected.isPresent()) {

            // Bookmark Entity 생성
            BookmarkEntity bookmark = BookmarkEntity.CreateBookmark(selectedExhibition, user);

            // Bookmark Entity 저장
            bookmarkRepository.save(bookmark);

            return BookmarkAddResponse.builder()
                    .message("북마크에 추가되었습니다.")
                    .exhibitionId(exhibitionId)
                    .build();
        }else {
            bookmarkRepository.delete(selected.get());

            return BookmarkAddResponse.builder()
                    .message("북마크에서 삭제되었습니다.")
                    .exhibitionId(exhibitionId)
                    .build();
        }
    }

}
