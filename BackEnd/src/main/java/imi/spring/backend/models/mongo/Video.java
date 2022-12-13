package imi.spring.backend.models.mongo;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Id;
import java.io.InputStream;

@Data
@Document(collection = "videos")
public class Video {
    private Long postId;
    private Integer order;
    @JsonIgnore
    private InputStream inputStream;
}
