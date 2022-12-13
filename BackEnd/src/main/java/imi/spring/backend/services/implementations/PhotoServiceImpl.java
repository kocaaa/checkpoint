package imi.spring.backend.services.implementations;

import com.tinify.Options;
import com.tinify.Tinify;
import imi.spring.backend.models.mongo.Photo;
import imi.spring.backend.repositories.mongo.PhotoRepository;
import imi.spring.backend.services.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonBinarySubType;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;

    @Override
    public String addPhoto(Long postId, Integer order, MultipartFile file) throws IOException {
        Photo photo = new Photo();
        photo.setPostId(postId);
        photo.setOrder(order);
        photo.setPhoto(
                new Binary(BsonBinarySubType.BINARY, file.getBytes())
        );

        log.info("Size of bytes not compressed {}", file.getBytes().length);

        photo = photoRepository.insert(photo);
        return photo.getId();
    }

    @Override
    public String addCompressedPhoto(Long postId, Integer order, MultipartFile file) throws IOException {

        Photo photo = new Photo();
        photo.setPostId(postId);
        photo.setOrder(order);


        BufferedImage originalImage;

        try (InputStream inputStream = file.getInputStream()) {
            originalImage = ImageIO.read(inputStream);
        } catch (IOException e) {
            String info = String.format("compressImage - IOException - message: %s ",  e.getMessage());
            log.error(info);
            return null;
        }

        Options options;

        Integer originalHeight = originalImage.getHeight();
        Integer originalWidth = originalImage.getWidth();

        if(originalHeight > 1080 || originalWidth > 1920){
            options = new Options()
                    .with("method", "fit")
                    .with("width", 1920)
                    .with("height", 1080);
        }
        else{
            options = new Options()
                    .with("method", "fit")
                    .with("width", originalWidth)
                    .with("height", originalHeight);
        }

        byte[] compressedBytes = Tinify.fromBuffer(file.getBytes()).resize(options).toBuffer();

        photo.setPhoto(
                new Binary(BsonBinarySubType.BINARY, compressedBytes)
        );

        log.info("Size of bytes compressed {}", compressedBytes.length);

        photo = photoRepository.insert(photo);

        return photo.getId();
    }

    @Override
    public List<Photo> getPhotosByPostId(Long postId) {
        List<Photo> photos = photoRepository.getPhotosByPostId(postId);
        if(photos!=null && photos.size()>0){
            return photos;
        }
        return Collections.emptyList();
    }

    @Override
    public List<String> getEncodedPhotos(Long postId) {
        List<Photo> photos = getPhotosByPostId(postId);
        List<String> encodedPhotos = new ArrayList<>();

        for(Photo photo : photos){
            encodedPhotos.add(Base64.getEncoder().encodeToString(photo.getPhoto().getData()));
        }

        return encodedPhotos;
    }

    @Override
    public Photo getPhotoByPostIdAndOrder(Long postId, Integer order) throws IOException {
        List<Photo> photos = getPhotosByPostId(postId);
        Photo actualPhoto = null;

        for(Photo photo : photos){
            if(Objects.equals(photo.getOrder(), order)){
                actualPhoto = photo;
                break;
            }
        }

        if(actualPhoto == null){
            throw new IOException("No photo with that order.");
        }

        return actualPhoto;
    }

    @Override
    public String deletePhotosByPostId(Long postId) {
        photoRepository.deletePhotosByPostId(postId);
        return "Deleted";
    }
}
