package imi.spring.backend.services.implementations;

import imi.spring.backend.models.AppUser;
import imi.spring.backend.models.Post;
import imi.spring.backend.models.PostLike;
import imi.spring.backend.repositories.PostLikeRepository;
import imi.spring.backend.services.AppUserService;
import imi.spring.backend.services.JWTService;
import imi.spring.backend.services.PostLikeService;
import imi.spring.backend.services.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@Service
public class PostLikeServiceImpl implements PostLikeService {

    private final PostLikeRepository postLikeRepository;
    private final AppUserService appUserService;
    private final PostService postService;

    @Override
    public List<PostLike> getAllPostLikes() { return postLikeRepository.findAll(); }

    @Override
    public List<PostLike> getAllLikesByPostId(Long postId) {
        return postLikeRepository.findAllByPostId(postId);
    }

    @Override
    public Integer getNumberOfLikesByPostId(Long postId) {
        return postLikeRepository.countByPostId(postId);
    }

    @Override
    public String likeOrUnlikePostById(Long userId, Long postId) throws ServletException {
        AppUser user = appUserService.getUserById(userId);
        Post post = postService.getPostById(postId);
        if (post == null)
            return "Post with that id does not exist!";
        PostLike postLike = postLikeRepository.findByPostIdAndUserId(postId, user.getId());
        if (postLike == null){ //like
            postLikeRepository.save(new PostLike(post, user, LocalDateTime.now()));
            //log.info("Liked post with id {}", post.getId());
            return "Liked";
        }
        postLikeRepository.delete(postLike); //unlike
        //log.info("Unliked post with id {}", post.getId());
        return "Unliked";
    }

    @Override
    public List<Long> getAllLikedPostsIdsByUser(AppUser user, Long postId) {
        List<PostLike> postLikeList = user.getPostLikeList();
        List<Long> postIds = new ArrayList<>();
        for (PostLike pl : postLikeList)
        {
            postIds.add(pl.getPost().getId());
        }
        return postIds;
    }
}
