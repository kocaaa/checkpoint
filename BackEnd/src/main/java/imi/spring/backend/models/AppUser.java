package imi.spring.backend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String username;
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<LocationSearch> locationSearchList = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Post> postList = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<PostLike> postLikeList = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "userfollow",
            joinColumns = {@JoinColumn(name = "user1_id", referencedColumnName = "id", nullable = false)},
            inverseJoinColumns = {@JoinColumn(name="user2_id", referencedColumnName = "id", nullable = false)})
    private List<AppUser> followingList = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "followingList")
    private List<AppUser> followersList = new ArrayList<>();

    @JsonIgnore
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] image = null;

    public AppUser(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public AppUser(String email, String username, String password, byte[] image) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.image = image;
    }

}
