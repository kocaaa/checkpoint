package imi.spring.backend.repositories.mongo;

import imi.spring.backend.models.mongo.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PhotoRepository extends MongoRepository<Photo, String> {
    List<Photo> getPhotosByPostId(Long postId);
    String deletePhotosByPostId(Long postId);
}
