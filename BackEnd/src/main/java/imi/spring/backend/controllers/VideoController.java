package imi.spring.backend.controllers;

import imi.spring.backend.models.mongo.Photo;
import imi.spring.backend.models.mongo.Video;
import imi.spring.backend.services.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/videos")
public class VideoController {

    private final VideoService videoService;

    @GetMapping
    public String videos(){
        return "uploadVideo";
    }

    @PostMapping("/add")
    public String addVideo(@RequestParam("postId") Long postId, @RequestParam("order") Integer order, @RequestParam("file") MultipartFile file) throws IOException {
        String id = videoService.addVideo(postId, order,  file);
        log.info("Video has been added for post with id {} and videoId is {}", postId, id);
        return "redirect:/videos/" + postId;
    }

    @GetMapping("/{postId}")
    public String getVideos(@PathVariable Long postId, Model model) throws IOException {

        List<Video> videos = videoService.getVideosByPostId(postId);

        model.addAttribute("videos", videos);

        return "videos";
    }

    @GetMapping("/watchVideo")
    public String getActualVideo(@RequestParam Long postId,@RequestParam Integer order, Model model) {

        model.addAttribute("url", "/videos/stream?postId=" + postId + "&order="+ order);
        return "actualVideo";
    }

    @GetMapping("/stream")
    public void streamVideo(@RequestParam Long postId,@RequestParam Integer order, HttpServletResponse response) throws IOException {
        List<Video> videos = videoService.getVideosByPostId(postId);
        Video actualVideo = null;

        for(Video video : videos){
            if(Objects.equals(video.getOrder(), order)){
                actualVideo = video;
                break;
            }
        }

        if(actualVideo == null){
            throw new IOException("No video with that order");
        }

        FileCopyUtils.copy(actualVideo.getInputStream(), response.getOutputStream());
    }

    @GetMapping("/videoByPostIdAndOrder")
    @ResponseBody
    public Video getVideoByPostIdAndOrder(@RequestParam("postId") Long postId, @RequestParam("order") Integer order) throws IOException {
        return videoService.getVideoByPostIdAndOrder(postId, order);
    }
}