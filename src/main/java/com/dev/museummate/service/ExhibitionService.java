package com.dev.museummate.service;

import com.dev.museummate.domain.dto.bookmark.BookmarkResponse;
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

    // 해당하는 exhibition을 Bookmark에 추가
    public BookmarkResponse addToBookmark(long exhibitionId, String userName) {
        UserEntity user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, "존재하지 않는 유저입니다."));
        ExhibitionEntity selectedExhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new AppException(ErrorCode.NOT_FOUND_POST, "존재하지 않는 게시물입니다."));
        Optional<BookmarkEntity> selected = bookmarkRepository.findByExhibitionAndUser(selectedExhibition, user);

        if(!selected.isPresent()) {
            BookmarkEntity bookmark = BookmarkEntity.of(selectedExhibition, user);
            bookmarkRepository.save(bookmark);
            return new BookmarkResponse("북마크에 추가되었습니다!");
        }else {
            bookmarkRepository.delete(selected.get());
            return new BookmarkResponse("북마크에서 삭제되었습니다!");
        }
    }

}
