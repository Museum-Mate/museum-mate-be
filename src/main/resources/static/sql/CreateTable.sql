create table museum_mate_db.gallery
(
    id         bigint auto_increment comment 'id'
        primary key,
    name       varchar(45) null comment '전시장 이름',
    address    varchar(45) null comment '도로명 주소',
    open_time  varchar(45) null comment '개장 시간',
    close_time varchar(45) null comment '마감 시간'
)
    comment '갤러리';

create table museum_mate_db.user
(
    id               bigint auto_increment
        primary key,
    email            varchar(45)                     not null,
    user_name        varchar(10)                     not null,
    password         varchar(45)                     not null,
    name             varchar(10)                     not null,
    birth            varchar(6)                      not null,
    phone_number     varchar(11)                     not null,
    address          varchar(45) collate utf8mb4_bin not null,
    role             varchar(10)                     not null,
    created_at       datetime                        not null,
    last_modified_at datetime                        not null,
    deleted_at       datetime                        null
);

create table museum_mate_db.comment
(
    id               bigint auto_increment comment 'id'
        primary key,
    user_id          bigint      null comment '댓글 작성자 id',
    parent_id        bigint      null comment '부모 댓글 id',
    content          varchar(50) null,
    created_at       datetime    not null comment '생성 시간',
    last_modified_at datetime    not null comment '최종 수정 시간',
    deleted_at       datetime    null comment '삭제 시간',
    constraint comment_user_id_fk
        foreign key (user_id) references museum_mate_db.user (id)
)
    comment '댓글 (모집 글)';

create table museum_mate_db.exhibition
(
    id                  bigint auto_increment
        primary key,
    name                varchar(45)  not null comment '전시회 명',
    start_at            varchar(10)  not null comment '시작 날짜',
    end_at              varchar(10)  not null comment '종료 날짜',
    price               varchar(10)  not null comment '가격',
    age_limit           varchar(2)   null comment '관람 연령',
    detail_info         longtext     null comment '이용 정보',
    detail_info_url     varchar(255) not null comment '상세페이지 URL',
    notice              longtext     null comment '공지',
    gallery_location    varchar(45)  null comment '갤러리 위치 정보',
    gallery_id          bigint       null comment '갤러리 ID (FK)',
    user_id             bigint       null comment '전시 등록 유저 ID(FK)',
    stat_male           varchar(4)   null comment '남성 통계',
    stat_female         varchar(4)   null comment '여성 통계',
    stat_age_10         varchar(4)   null comment '10대 통계',
    stat_age_20         varchar(4)   null comment '20대 통계',
    stat_age_30         varchar(4)   null comment '30대 통계',
    stat_age_40         varchar(4)   null comment '40대 통계',
    stat_age_50         varchar(4)   null comment '50대 통계',
    main_img_url        varchar(255) null comment '메인 Img URL',
    notice_img_url      varchar(255) null comment '공지 Img URL',
    detail_info_img_url varchar(255) null comment '세부 정보 Img URL',
    created_at          datetime     not null comment '생성시간',
    last_modified_at    datetime     not null comment '수정 시간',
    deleted_at          datetime     null comment '삭제 시간',
    created_by          varchar(10)  not null comment '생성자',
    last_modified_by    varchar(10)  not null comment '최종 수정자',
    constraint exhibition_gallery_id_fk
        foreign key (gallery_id) references museum_mate_db.gallery (id),
    constraint exhibition_user_id_fk
        foreign key (user_id) references museum_mate_db.user (id)
)
    comment '전시 테이블';

create table museum_mate_db.alarm
(
    id               bigint auto_increment comment 'id'
        primary key,
    user_id          bigint      not null comment '알람을 보낼 유저 id',
    exhibition_id    bigint      not null,
    alarm_message    varchar(45) not null,
    created_at       datetime    not null comment '생성 시간',
    last_modified_at datetime    not null comment '최종 수정 시간',
    deleted_at       datetime    null comment '삭제 시간',
    constraint alarm_exhibition_id_fk
        foreign key (exhibition_id) references museum_mate_db.exhibition (id),
    constraint alarm_user_id_fk
        foreign key (user_id) references museum_mate_db.user (id)
)
    comment '알람';

create table museum_mate_db.bookmark
(
    id               bigint auto_increment comment 'id'
        primary key,
    user_id          bigint   not null comment '북마크한 유저 id',
    exhibition_id    bigint   not null comment '북마크한 전시 id',
    created_at       datetime not null comment '생성 시간',
    last_modified_at datetime not null comment '최종 수정 시간',
    deleted_at       datetime null comment '삭제 시간',
    constraint bookmark_exhibition_id_fk
        foreign key (exhibition_id) references museum_mate_db.exhibition (id),
    constraint bookmark_user_id_fk
        foreign key (user_id) references museum_mate_db.user (id)
)
    comment '북마크';

create table museum_mate_db.review
(
    id               bigint auto_increment comment 'id'
        primary key,
    title            varchar(45) not null comment '리뷰제목',
    content          longtext    not null comment '리뷰 내용',
    star             tinyint     not null comment '리뷰 별점(5/5)',
    user_id          bigint      not null comment '리뷰 작성 유저 id',
    exhibition_id    bigint      not null comment '해당하는 전시 id',
    visited_date     varchar(10) null comment '방문 날짜',
    created_at       datetime    not null comment '생성 시간',
    last_modified_at int         not null,
    deleted_at       datetime    null,
    created_by       varchar(10) not null comment '생성 유저',
    last_modifyed_by varchar(10) not null comment '최종 수정 유저',
    constraint review_exhibition_id_fk
        foreign key (exhibition_id) references museum_mate_db.exhibition (id),
    constraint review_user_id_fk
        foreign key (user_id) references museum_mate_db.user (id)
)
    comment '리뷰';

