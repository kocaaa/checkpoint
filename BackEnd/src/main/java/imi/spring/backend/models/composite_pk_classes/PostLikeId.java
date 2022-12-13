package imi.spring.backend.models.composite_pk_classes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeId implements Serializable {

    private Long user;
    private Long post;
}
