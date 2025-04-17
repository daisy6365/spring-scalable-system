package com.study.comment.controller;

import com.study.comment.request.CommentCreateRequest;
import com.study.comment.request.CommentCreateRequestV2;
import com.study.comment.response.CommentResponse;
import com.study.comment.service.CommentServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/comments")
public class CommentControllerV2 {
    private final CommentServiceV2 commentServiceV2;

    @GetMapping("/{commentId}")
    public CommentResponse read(@PathVariable("commentId") Long commentId){
        return commentServiceV2.read(commentId);
    }

    @PostMapping
    public CommentResponse create(@RequestBody CommentCreateRequestV2 request){
        return commentServiceV2.create(request);
    }

    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable("commentId") Long commentId){
        commentServiceV2.delete(commentId);
    }

}
