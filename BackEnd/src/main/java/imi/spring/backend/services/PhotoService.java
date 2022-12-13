package imi.spring.backend.services;

import imi.spring.backend.models.mongo.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PhotoService {
    String addPhoto(Long postId, Integer order, MultipartFile file) throws IOException;
    String addCompressedPhoto(Long postId, Integer order, MultipartFile file) throws IOException;
    List<Photo> getPhotosByPostId(Long postId);
    List<String> getEncodedPhotos(Long postId);
    Photo getPhotoByPostIdAndOrder(Long postId, Integer order) throws IOException;
    String deletePhotosByPostId(Long postId);
}
