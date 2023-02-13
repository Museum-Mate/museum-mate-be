package com.dev.museummate.service;

import com.dev.museummate.domain.dto.exhibition.BookmarkAddResponse;
import com.dev.museummate.domain.dto.exhibition.ExhibitionDto;
import com.dev.museummate.domain.dto.exhibition.ExhibitionEditRequest;
import com.dev.museummate.domain.dto.exhibition.ExhibitionWriteRequest;
import com.dev.museummate.domain.entity.BookmarkEntity;
import com.dev.museummate.domain.entity.ExhibitionEntity;
import com.dev.museummate.domain.entity.UserEntity;
import com.dev.museummate.exception.AppException;
import com.dev.museummate.exception.ErrorCode;
import com.dev.museummate.repository.BookmarkRepository;
import com.dev.museummate.repository.ExhibitionRepository;
import com.dev.museummate.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final UserRepository userRepository;
    private final BookmarkRepository bookmarkRepository;

    public Page<ExhibitionDto> findAllExhibitions (Pageable pageable) {
        Page<ExhibitionEntity> exhibitionEntities = exhibitionRepository.findAll(pageable);
        return exhibitionEntities.map(exhibition -> ExhibitionDto.toDto(exhibition));
    }

    public Page<ExhibitionDto> findAllByPrice (Pageable pageable, String price) {
        if (price.equals("무료")) {
            Page<ExhibitionEntity> exhibitionEntities = exhibitionRepository.findAllByPrice(pageable, price);
            return exhibitionEntities.map(exhibition -> ExhibitionDto.toDto(exhibition));
        }

        String p = "무료";

        Page<ExhibitionEntity> exhibitionEntities = exhibitionRepository.findAllByPriceNot(pageable, p);
        return exhibitionEntities.map(exhibition -> ExhibitionDto.toDto(exhibition));
    }

    // 전시 상세 조회
    public ExhibitionDto getOne(long exhibitionId) {
        ExhibitionEntity selectedExhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new AppException(ErrorCode.EXHIBITION_NOT_FOUND, "존재하지 않는 전시회입니다."));

        ExhibitionDto selectedExhibitionDto = ExhibitionDto.toDto(selectedExhibition);

        return selectedExhibitionDto;
    }

    // 유저가 전시회 직접 등록
    public ExhibitionDto write(ExhibitionWriteRequest exhibitionWriteRequest, String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND, "존재하지 않는 유저입니다."));

        ExhibitionEntity savedExhibition = exhibitionRepository.save(exhibitionWriteRequest.toEntity(user));

        ExhibitionDto selectedExhibitionDto = ExhibitionDto.toDto(savedExhibition);

        return selectedExhibitionDto;
    }

    // 해당하는 exhibition을 Bookmark에 추가
    public BookmarkAddResponse addToBookmark(long exhibitionId, String email) {

        // 유저 네임 검증
        // TODO: 22.01.21 userName -> email로 변경
        UserEntity user = userRepository.findByEmail(email)
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

    public ExhibitionEntity getExihibitionById(Long exhibitionId) {

        return exhibitionRepository.findById(exhibitionId).orElseThrow(() ->
            new AppException(ErrorCode.NOT_FOUND_POST, String.format("해당 포스트는 존재하지 않습니다.")));
    }

    public ExhibitionDto edit(Long exhibitionId, ExhibitionEditRequest exhibitionEditRequest, String email) {

        UserEntity user = userRepository.findByEmail(email).orElseThrow(() ->
            new AppException(ErrorCode.EMAIL_NOT_FOUND, "존재하지 않는 유저입니다."));

        ExhibitionEntity exhibitionEntity = getExihibitionById(exhibitionId);

        ExhibitionEntity savedExhibition = exhibitionRepository.save(exhibitionEditRequest.toEntity(user));

        ExhibitionDto exhibitionDto = ExhibitionDto.toDto(savedExhibition);

        return exhibitionDto;
    }
}
