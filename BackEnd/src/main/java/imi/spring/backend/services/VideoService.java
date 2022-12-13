package imi.spring.backend.services;

import imi.spring.backend.models.mongo.Video;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VideoService {
    String addVideo(Long postId, Integer order, MultipartFile file) throws IOException;
    List<Video> getVideosByPostId(Long postId) throws IllegalStateException, IOException;
    Video getVideoByPostIdAndOrder(Long postId, Integer order) throws IOException;
}