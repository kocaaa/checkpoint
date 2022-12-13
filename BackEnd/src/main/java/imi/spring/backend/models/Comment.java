package imi.spring.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
    Komentare je moguce dodati u dva nivoa (dubine) - komentar i odgovor na komentar.
    Svaki odgovor na odgovor na komentar sa fronta ce se tretirati samo kao odgovor na komentar na beku i u bazi.
*/
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUser user;
    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @JsonIgnore
    @ManyToOne()
    private Comment parentComment;
    @JsonIgnore
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    private List<Comment> subCommentList = new ArrayList<>();

    private String text;

    @JsonIgnore
    private LocalDateTime time;
}
