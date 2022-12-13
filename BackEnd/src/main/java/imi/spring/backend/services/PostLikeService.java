package imi.spring.backend.services;

import imi.spring.backend.models.AppUser;
import imi.spring.backend.models.PostLike;
import org.hibernate.ObjectNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface PostLikeService {
    List<PostLike> getAllPostLikes();

    List<PostLike> getAllLikesByPostId(Long postId) throws ObjectNotFoundException;

    Integer getNumberOfLikesByPostId(Long postId);

    String likeOrUnlikePostById(Long userId, Long postId) throws ServletException;

    List<Long> getAllLikedPostsIdsByUser(AppUser user, Long postId);
}
