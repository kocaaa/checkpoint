package imi.spring.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;



    @JsonIgnore
    private LocalDateTime time;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private AppUser user;
    @ManyToOne()
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostLike> postLikeList = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    //dodatno: slike i videi, ocene

    public Post(String description) {
        this.description = description;
    }

    public Post(String description, LocalDateTime time, AppUser user, Location location) {
        this(description);
        this.time = time;
        this.user = user;
        this.location = location;
    }
}
