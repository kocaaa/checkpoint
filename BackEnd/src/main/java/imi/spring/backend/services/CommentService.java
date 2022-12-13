package imi.spring.backend.services;

import imi.spring.backend.models.*;

import java.io.IOException;
import java.util.List;

public interface CommentService {
    List<Comment> getAllComments();
    Comment getCommentById(Long id);
    String addComment(String commentText, Long userId, Long postId, Long parentCommentId);
    String deleteCommentById(AppUser userFromJWT, Long id);
    List<Comment> getAllCommentsByPostId(Long postId);
    List<Comment> getAllSubcommentsByCommentId(Long id);
    CommentDTO convertCommentToCommentDTO(AppUser userFromJWT, Comment comment) throws IOException;
    List<CommentDTO> convertListOfCommentsToCommentDTOs(AppUser userFromJWT, List<Comment> comments) throws IOException;
    List<Comment> getFirstCommentsByPostId(Long postId);
}
