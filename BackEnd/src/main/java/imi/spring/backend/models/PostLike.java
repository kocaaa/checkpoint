package imi.spring.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import imi.spring.backend.models.composite_pk_classes.PostLikeId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(PostLikeId.class)
public class PostLike {

    @Id
    @ManyToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUser user;

    @JsonIgnore
    private LocalDateTime time;
}
