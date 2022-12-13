package imi.spring.backend.models.mongo;

import lombok.Data;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Id;

@Data
@Document(collection = "photos")
public class Photo {
    @Id
    private String id;
    private Integer order;
    private Long postId;
//    @JsonIgnore
    private Binary photo;
}