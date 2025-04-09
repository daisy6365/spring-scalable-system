package com.study.comment.controller;

import com.study.comment.request.CommentCreateRequest;
import com.study.comment.response.CommentResponse;
import com.study.comment.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/comments")
public class CommentController {
    private final CommentService commentService;

    @GetMapping("{commentId}")
    public CommentResponse read(@PathVariable("commentId") Long commentId){
        return commentService.read(commentId);
    }

    @PostMapping
    public CommentResponse create(@RequestBody CommentCreateRequest request){
        return commentService.create(request);
    }

    @DeleteMapping("{commentId}")
    public void delete(@PathVariable("commentId") Long commentId){
        commentService.delete(commentId);
    }

}

