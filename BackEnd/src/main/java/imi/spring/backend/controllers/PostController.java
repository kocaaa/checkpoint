package imi.spring.backend.controllers;

import imi.spring.backend.models.AppUser;
import imi.spring.backend.models.Post;
import imi.spring.backend.models.PostDTO;
import imi.spring.backend.services.JWTService;
import imi.spring.backend.services.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping(path = "/post")
public class PostController {

    private final PostService postService;
    private final JWTService jwtService;

    @GetMapping("/all")
    @ResponseBody
    public List<PostDTO> getAllPosts(HttpServletRequest request) throws IOException, ServletException {
        log.info("Getting all posts.");
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null){
            return postService.convertListOfPostsToPostDTOs(user, postService.getAllPosts());
        }
        return Collections.emptyList();
    }

    @GetMapping("/{id}")
    @ResponseBody
    public PostDTO getPostById(HttpServletRequest request, @PathVariable Long id) throws IOException, ServletException {
        log.info("Getting post for user with id {}.", id);
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null){
            return postService.convertPostToPostDTO(user, postService.getPostById(id));
        }
        return null;
    }

    @GetMapping("/{id}/comments/count")
    @ResponseBody
    public Integer countCommentsByPostId(@PathVariable Long id) {
        log.info("Getting comments count for post with id {}.", id);
        return postService.countCommentsByPostId(id);
    }

    /*
    @PostMapping("/save/location/{locationId}")
    @ResponseBody
    public String savePost(HttpServletRequest request, @RequestBody Post post, @PathVariable Long locationId) throws ServletException {
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null)
            return postService.savePost(post, user.getId(), locationId);
        return "Invalid user!";
    }*/

    @PostMapping("/save/location/{locationId}")
    @ResponseBody
    public Long savePost(HttpServletRequest request, @RequestBody String description, @PathVariable Long locationId) throws ServletException {
        log.info("Saving new post.");
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null){
            log.info("Found user");
            return postService.savePost(description, user.getId(), locationId);
        }
        log.info("Error saving post");
        return -1l;
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String deletePostById(HttpServletRequest request, @PathVariable Long id) throws ServletException {
        log.info("Deleting post with id {}", id);
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null){
            return postService.deletePost(user.getId(), id);
        }
        return "Invalid user!";
    }

    @GetMapping("/user/{userId}")
    @ResponseBody
    public List<PostDTO> getPostsByUserId(HttpServletRequest request, @PathVariable Long userId) throws IOException, ServletException {
        log.info("Getting posts for user with id {}.", userId);
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null){
            return postService.convertListOfPostsToPostDTOs(user, postService.getPostsByUserId(userId));
        }
        return Collections.emptyList();
    }

    @GetMapping("/location/{locationId}")
    @ResponseBody
    public List<PostDTO> getPostsByLocationId(HttpServletRequest request, @PathVariable Long locationId) throws IOException, ServletException {
        log.info("Getting posts by location_id - {}.", locationId);
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null){
            return postService.convertListOfPostsToPostDTOs(user, postService.getPostsByLocationId(locationId));
        }
        return Collections.emptyList();
    }

    @GetMapping("/count/all")
    @ResponseBody
    public Long getNumberOfPostsInTotal() {
        log.info("Getting total number of posts.");
        return postService.getNumberOfPostsInTotal();
    }

    @GetMapping("/my/count")
    @ResponseBody
    public Long getNumberOfMyPosts(HttpServletRequest request) throws ServletException {
        log.info("Getting number of all posts for current user.");
        return postService.getNumberOfMyPosts(request);
    }

    @GetMapping("/user/{userId}/count")
    @ResponseBody
    public Long getNumberOfPostsByUserId(@PathVariable Long userId) {
        log.info("Getting number of posts for user with id {}", userId);
        return postService.getNumberOfPostsByUserId(userId);
    }

    @GetMapping("/following")
    @ResponseBody
    public List<PostDTO> getPostsOfUsersThatIFollow(HttpServletRequest request) throws IOException, ServletException {
        log.info("Getting all posts of users that user follows");
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null){
            return postService.convertListOfPostsToPostDTOs(user, postService.getPostsOfUsersThatIFollow(user));
        }
        return Collections.emptyList();
    }
}