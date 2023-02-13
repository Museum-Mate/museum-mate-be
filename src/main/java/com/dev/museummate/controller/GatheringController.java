package com.dev.museummate.controller;

import com.dev.museummate.domain.dto.Response;
import com.dev.museummate.domain.dto.gathering.CommentDto;
import com.dev.museummate.domain.dto.gathering.CommentPostResponse;
import com.dev.museummate.domain.dto.gathering.CommentRequest;
import com.dev.museummate.domain.dto.gathering.CommentResponse;
import com.dev.museummate.domain.dto.gathering.GatheringDto;
import com.dev.museummate.domain.dto.gathering.GatheringParticipantResponse;
import com.dev.museummate.domain.dto.gathering.GatheringPostRequest;
import com.dev.museummate.domain.dto.gathering.GatheringPostResponse;
import com.dev.museummate.domain.dto.gathering.GatheringResponse;
import com.dev.museummate.domain.dto.gathering.ParticipantDto;
import com.dev.museummate.service.GatheringService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/gatherings")
public class GatheringController {

    private final GatheringService gatheringService;

    @PostMapping("/posts")
    public Response<GatheringPostResponse> posts(@RequestBody GatheringPostRequest gatheringPostRequest, Authentication authentication) {

        GatheringDto gatheringDto = gatheringService.posts(gatheringPostRequest, authentication.getName());
        return Response.success(new GatheringPostResponse(gatheringDto.getId()));
    }

    @PostMapping("/{gatheringId}/enroll")
    public Response<String> enroll(@PathVariable Long gatheringId, Authentication authentication) {
        String msg = gatheringService.enroll(gatheringId, authentication.getName());
        return Response.success(msg);
    }

    @GetMapping("/{gatheringId}/enroll/{participantId}")
    public Response<String> approve(@PathVariable Long gatheringId, @PathVariable Long participantId, Authentication authentication) {
        String msg = gatheringService.approve(gatheringId, participantId, authentication.getName());
        return Response.success(msg);
    }

    @GetMapping("/{gatheringId}/enroll/list")
    public Response<List<GatheringParticipantResponse>> enrollList(@PathVariable Long gatheringId, Authentication authentication) {
        List<ParticipantDto> participantDtos = gatheringService.enrollList(gatheringId, authentication.getName());
        List<GatheringParticipantResponse> gatheringParticipantResponses = participantDtos.stream()
                                                                                          .map(ParticipantDto::toResponse)
                                                                                          .collect(Collectors.toList());
        return Response.success(gatheringParticipantResponses);
    }

    @GetMapping("/{gatheringId}/approve/list")
    public Response<List<GatheringParticipantResponse>> approveList(@PathVariable Long gatheringId) {
        List<ParticipantDto> participantDtos = gatheringService.approveList(gatheringId);
        List<GatheringParticipantResponse> gatheringParticipantResponses = participantDtos.stream()
                                                                                          .map(ParticipantDto::toResponse)
                                                                                          .collect(Collectors.toList());
        return Response.success(gatheringParticipantResponses);
    }

    @DeleteMapping("/{gatheringId}/cancel")
    public Response<String> cancel(@PathVariable Long gatheringId, Authentication authentication) {
        String msg = gatheringService.cancel(gatheringId, authentication.getName());
        return Response.success(msg);
    }

    @GetMapping
    public Response<Page<GatheringResponse>> findAllGatherings(@PageableDefault(size = 20,
                                                                                sort = "id", direction = Direction.DESC) Pageable pageable) {
        Page<GatheringDto> gatheringDtos = gatheringService.findAllGatherings(pageable);

        List<GatheringResponse> gatheringResponseList = new ArrayList<>();

        for (GatheringDto gatheringDto : gatheringDtos) {

            GatheringResponse gatheringResponse = GatheringResponse.createGetOne(gatheringDto);

            gatheringResponseList.add(gatheringResponse);

        }

        return Response.success(new PageImpl<>(gatheringResponseList, pageable, gatheringDtos.getTotalElements()));
    }

    @GetMapping("/{gatheringId}")
    public Response getOne(@PathVariable Long gatheringId) {

        GatheringDto oneGatheringDto = gatheringService.getOne(gatheringId);
        GatheringResponse gatheringResponse = GatheringResponse.createGetOne(oneGatheringDto);
        return Response.success(gatheringResponse);
    }

    @PutMapping("/{gatheringId}")
    public Response edit(@PathVariable Long gatheringId, @RequestBody GatheringPostRequest gatheringPostRequest,
                         Authentication authentication) {

        GatheringDto gatheringDto = gatheringService.edit(gatheringId, gatheringPostRequest, authentication.getName());
        return Response.success(new GatheringPostResponse(gatheringDto.getId()));
    }

    @DeleteMapping("/{gatheringId}")
    public Response delete(@PathVariable Long gatheringId, Authentication authentication) {

        Long deletedId = gatheringService.delete(gatheringId, authentication.getName());
        return Response.success(new GatheringPostResponse(deletedId));
    }

    @PostMapping("/{gatheringId}/comments")
    public Response<CommentPostResponse> writeComment(@PathVariable Long gatheringId, @RequestBody CommentRequest commentRequest,
                                                      Authentication authentication) {
        CommentDto commentDto = gatheringService.writeComment(gatheringId, commentRequest, authentication.getName());
        return Response.success(new CommentPostResponse(commentDto.getId(), commentDto.getContent()));
    }

    @GetMapping("/{gatheringId}/comments")
    public Response<Page<CommentResponse>> getComments(@PageableDefault(size = 10)
                                                       @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                                       @PathVariable Long gatheringId) {
        Page<CommentDto> comments = gatheringService.getComments(pageable, gatheringId);
        List<CommentResponse> commentResponses = new ArrayList<>();
        for (CommentDto comment : comments) {
            List<CommentResponse> replyResponses = new ArrayList<>();
            List<CommentDto> replies = comment.getReplies();
            for (CommentDto reply : replies) {
                CommentResponse replyResponse = reply.toResponse();
                replyResponses.add(replyResponse);
            }
            CommentResponse commentResponse = comment.toParentResponse(replyResponses);
            commentResponses.add(commentResponse);

        }
        return Response.success(new PageImpl<>(commentResponses,pageable,commentResponses.size()));
    }

    @PutMapping("/{gatheringId}/comments/{commentId}")
    public Response<CommentPostResponse> modifyComment(@PathVariable Long gatheringId, @RequestBody CommentRequest commentRequest,
                                                       @PathVariable Long commentId, Authentication authentication) {
        CommentDto commentDto = gatheringService.modifyComment(gatheringId, commentId, authentication.getName(), commentRequest);
        return Response.success(new CommentPostResponse(commentDto.getId(), commentDto.getContent()));
    }

    @DeleteMapping("/{gatheringId}/comments/{commentId}")
    public Response<String> deleteComment(@PathVariable Long gatheringId,
                                          @PathVariable Long commentId, Authentication authentication) {
        String msg = gatheringService.deleteComment(gatheringId, commentId, authentication.getName());
        return Response.success(msg);
    }

    @PostMapping("//{gatheringId}/comments/{commentId}/reply")
    public Response<CommentPostResponse> postReply(@PathVariable Long gatheringId,
                                                   @PathVariable Long commentId, Authentication authentication,
                                                   @RequestBody CommentRequest commentRequest) {
        CommentDto commentDto = gatheringService.writeReply(gatheringId, commentId, authentication.getName(), commentRequest);
        return Response.success(new CommentPostResponse(commentDto.getId(), commentDto.getContent()));
    }
}