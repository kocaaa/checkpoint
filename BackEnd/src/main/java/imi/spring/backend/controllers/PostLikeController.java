package imi.spring.backend.controllers;

import imi.spring.backend.models.AppUser;
import imi.spring.backend.models.PostLike;
import imi.spring.backend.services.JWTService;
import imi.spring.backend.services.PostLikeService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping(path = "/post_likes")
public class PostLikeController {

    private final PostLikeService postLikeService;
    private final JWTService jwtService;

    @GetMapping("/all")
    @ResponseBody
    public List<PostLike> getAllPostLikes() { return postLikeService.getAllPostLikes(); }

    @GetMapping("/post/{postId}")
    @ResponseBody
    public List<PostLike> getAllPostLikes(@PathVariable Long postId) { return postLikeService.getAllLikesByPostId(postId); }

    @GetMapping("/count/{postId}")
    @ResponseBody
    public Integer getNumberOfLikesByPostId(@PathVariable Long postId) { return postLikeService.getNumberOfLikesByPostId(postId); }

    @PostMapping("/save/{postId}")
    @ResponseBody
    public String likeOrUnlikePostById(HttpServletRequest request, @PathVariable Long postId) throws ServletException {
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null)
            return postLikeService.likeOrUnlikePostById(user.getId(), postId);
        return "Invalid user!";
    }

    @GetMapping("/id_s/{postId}")
    @ResponseBody
    public List<Long> getAllLikedPostsIdsByUser(HttpServletRequest request, @PathVariable Long postId) throws ServletException {
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null)
            return postLikeService.getAllLikedPostsIdsByUser(user, postId);
        return Collections.emptyList();
    }


}
