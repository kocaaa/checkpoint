package imi.spring.backend.services.implementations;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import imi.spring.backend.models.mongo.Video;
import imi.spring.backend.services.VideoService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class VideoServiceImpl implements VideoService {

    private final GridFsTemplate gridFsTemplate;
    private final GridFsOperations operations;

    @Override
    public String addVideo(Long postId, Integer order, MultipartFile file) throws IOException {
        DBObject metaData = new BasicDBObject();
        metaData.put("type", "video");
        metaData.put("postId", postId);
        metaData.put("order", order);

        ObjectId id = gridFsTemplate.store(
                file.getInputStream(), file.getName(), file.getContentType(), metaData
        );
        return id.toString();
    }

    @Override
    public List<Video> getVideosByPostId(Long postId) throws IllegalStateException, IOException {

        List<GridFSFile> gridFSFiles = new ArrayList<>();
        gridFsTemplate.find(new Query(Criteria.where("metadata.postId").is(postId))).into(gridFSFiles);

        List<Video> videos = new ArrayList<>();

        for(GridFSFile file : gridFSFiles){
            Video video = new Video();
            video.setPostId(postId);
            video.setInputStream(operations.getResource(file).getInputStream());

            if(file.getMetadata() != null){
                video.setOrder(Integer.parseInt(file.getMetadata().get("order").toString()));
            }

            videos.add(video);
        }

        return videos;
    }

    @Override
    public Video getVideoByPostIdAndOrder(Long postId, Integer order) throws IOException {
        List<Video> videos = getVideosByPostId(postId);
        Video actualVideo = null;

        for(Video video : videos){
            if(Objects.equals(video.getOrder(), order)){
                actualVideo = video;
                break;
            }
        }

        if(actualVideo == null){
            throw new IOException("No video with that order.");
        }

        return actualVideo;
    }
}
