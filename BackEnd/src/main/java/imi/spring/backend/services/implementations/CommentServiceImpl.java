package imi.spring.backend.services.implementations;

import imi.spring.backend.models.*;
import imi.spring.backend.repositories.CommentRepository;
import imi.spring.backend.services.AppUserService;
import imi.spring.backend.services.CommentService;
import imi.spring.backend.services.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final AppUserService appUserService;
    private final PostService postService;


    @Override
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
    @Override
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Comment> getAllCommentsByPostId(Long postId) {
        if (postService.getPostById(postId) == null)
            return Collections.emptyList();
        return commentRepository.findAllByPostIdOrderByTimeAsc(postId);
    }

    @Override
    public List<Comment> getAllSubcommentsByCommentId(Long id) {
        Comment comment = getCommentById(id);
        if (comment == null)
            return Collections.emptyList();
        return comment.getSubCommentList();
    }

    @Override
    public String addComment(String commentText, Long userId, Long postId, Long parentCommentId) {
        Comment comment = new Comment();
        comment.setText(commentText.trim());
        comment.setTime(LocalDateTime.now());
        comment.setUser(appUserService.getUserById(userId));
        comment.setPost(postService.getPostById(postId));
        if (parentCommentId == 0) // PARENTCOMMENTID = 0 KADA NEMA PARENTCOMMENT !!!
            comment.setParentComment(null);
        else {
            Comment parentComment = getCommentById(parentCommentId);
            if (parentComment == null)
                return "Parent comment with that id does not exist!";
            comment.setParentComment(parentComment);
        }
        commentRepository.save(comment);
        log.info("Comment saved");
        return "Saved";
    }

    @Override
    public String deleteCommentById(AppUser userFromJWT, Long id) {
        Comment comment = getCommentById(id);
        if (comment == null)
            return "Comment with that id does not exist!";

        if (userFromJWT.equals(comment.getUser()) || userFromJWT.equals(comment.getPost().getUser())) { //sme da obrise ako je on autor komentara ili posta ciji je komentar
            commentRepository.deleteById(id);
            log.info("Deleted a comment with id {}.", id);
            return "Deleted.";
        }
        return "This user is not the owner!";
    }


    @Override
    public CommentDTO convertCommentToCommentDTO(AppUser userFromJWT, Comment comment) throws IOException {
        CommentDTO commentDTO = new CommentDTO();
        commentDTO.setId(comment.getId());
        commentDTO.setText(comment.getText());
        AppUser commentAuthor = comment.getUser();
        commentDTO.setAuthorId(commentAuthor.getId());
        commentDTO.setAuthorUsername(commentAuthor.getUsername());
        commentDTO.setPostId(comment.getPost().getId());
        List<Comment> sortedChildren = comment.getSubCommentList().stream()
                .sorted(Comparator.comparing(Comment::getTime))
                .collect(Collectors.toList());
        commentDTO.setSubCommentList(convertListOfCommentsToCommentDTOs(userFromJWT, sortedChildren));
        if (userFromJWT.equals(comment.getUser()) || userFromJWT.equals(comment.getPost().getUser()))
            commentDTO.setCanDelete(true);
        else
            commentDTO.setCanDelete(false);

        commentDTO.setImage(
                new String(Base64.getEncoder().encode(
                        comment.getUser().getImage()
                    )
                )
        );

        for (CommentDTO subcommentDTO : commentDTO.getSubCommentList())
        {
            subcommentDTO.setImage(
                    new String(Base64.getEncoder().encode(
                            appUserService.getUserById(subcommentDTO.getAuthorId()).getImage()
                    )
            ));

            LocalDate onlyDateSub = getCommentById(subcommentDTO.getId()).getTime().toLocalDate();
            String formattedDateSub = onlyDateSub.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
            subcommentDTO.setDate(formattedDateSub);
        }

        LocalDate onlyDate = comment.getTime().toLocalDate();
        String formattedDate = onlyDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
        commentDTO.setDate(formattedDate);

        /*CommentLike like = comment.getCommentLikeList()
                .stream()
                .filter(commentLike -> commentLike.getUser().equals(userFromJWT))
                .collect(Collectors.toList()).stream().findFirst().orElse(null);
        commentDTO.setIsLiked(like != null);*/

        return  commentDTO;
    }

    @Override
    public List<CommentDTO> convertListOfCommentsToCommentDTOs(AppUser userFromJWT, List<Comment> comments) throws IOException {
        List<CommentDTO> commentDTOs = new ArrayList<>();

        for(Comment comment : comments){
            commentDTOs.add(convertCommentToCommentDTO(userFromJWT, comment));
        }

        return  commentDTOs;
    }

    @Override
    public List<Comment> getFirstCommentsByPostId(Long postId) {
        if (postService.getPostById(postId) == null)
            return Collections.emptyList();
        return commentRepository.findAllByPostIdOrderByTimeAsc(postId).stream()
                .filter(comment -> comment.getParentComment() == null)
                .collect(Collectors.toList());
    }
}
