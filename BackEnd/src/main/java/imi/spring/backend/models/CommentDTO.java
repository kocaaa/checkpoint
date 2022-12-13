package imi.spring.backend.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private Long authorId;
    private String authorUsername;
    private Long postId;
    private List<CommentDTO> subCommentList;
    private String text;
    private Boolean canDelete;
    //private LocalDateTime time;
    //private Boolean isLiked; //da li je logovani user lajkovao ovaj komentar
    private String image;

    private String date;
}