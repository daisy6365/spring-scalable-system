package com.study.comment.entity;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

class CommentPathTest {
    @Test
    void createChildCommentTest(){
        // 댓글이 아무것도 없는 최초 00000 생성
        createChildCommentTest(CommentPath.create(""), null, "00000");

        // 하위 댓글로 새로운 댓글이 생성
        // 00000 00000 생성
        createChildCommentTest(CommentPath.create("00000"), null, "0000000000");

        // 댓글 00001 생성
        createChildCommentTest(CommentPath.create(""), "00000", "00001");

        // 댓글 0000z abcdz zzzzz zzzzz 존재 상태에서
        // 댓글 0000z abce0 생성
        createChildCommentTest(CommentPath.create("0000z"), "0000zabcdzzzzzzzzzzz", "0000zabce0");
    }

    void createChildCommentTest(CommentPath commentPath, String descendantsTopPath, String expectedChildPath){
        CommentPath childCommentPath = commentPath.createChildCommentPath(descendantsTopPath);
        assertEquals(childCommentPath.getPath(), expectedChildPath);
    }

    @Test
    void createChildCommentPathIfMaxDepthTest(){
        assertThatThrownBy(() ->
            CommentPath.create("zzzzz".repeat(5)).createChildCommentPath(null)
        ).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void createChildCommentPathIfOverflowTest(){
        CommentPath commentPath = CommentPath.create("");

        assertThatThrownBy(() ->
                commentPath.createChildCommentPath("zzzzz")
        ).isInstanceOf(IllegalStateException.class);
    }


}