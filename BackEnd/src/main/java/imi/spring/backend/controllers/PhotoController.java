package imi.spring.backend.controllers;

import imi.spring.backend.services.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bson.types.Binary;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
@Slf4j
@RequestMapping("/photos")
public class PhotoController {
    private final PhotoService photoService;
    private final Environment environment;

    @GetMapping
    public String photos(){
        return "uploadPhoto";
    }

    @PostMapping("/add/{postId}/{order}")
    public @ResponseBody String addPhoto(@PathVariable("postId") Long postId, @PathVariable("order") Integer order, @RequestParam("photo") MultipartFile image) throws IOException {


        File file = multipartToFile(image, image.getOriginalFilename());

        //Integer numberOfFaces = getNumberOfFaces(file);
        //log.info("Python has found {} faces on received picture. ", numberOfFaces);

        FileInputStream inputStream = new FileInputStream(file);
        MultipartFile multipartFile = new MockMultipartFile("file", file.getName(), "text/plain", inputStream.readAllBytes());

        String id = photoService.addPhoto(postId, order, multipartFile);
        log.info("Added compressed image to MongoDB with id {}", id);

        return "Zavrseno";
    }

    @GetMapping("/{postId}")
    public String getPhoto(@PathVariable Long postId, Model model) {
        List<String> encodedPhotos = photoService.getEncodedPhotos(postId);

        model.addAttribute("postId", postId);
        model.addAttribute("photos", encodedPhotos);

        return "photos";
    }

    @GetMapping("/photoByPostIdAndOrder/{postId}/{order}")
    @ResponseBody
    public Binary getPhotoByPostIdAndOrder(@PathVariable("postId") Long postId, @PathVariable("order") Integer order) throws IOException {
        return photoService.getPhotoByPostIdAndOrder(postId, order).getPhoto();
    }

    private Integer getNumberOfFaces(File file) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost uploadFile = new HttpPost(environment.getProperty("url.server.python") + "/count_faces");
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("field1", "yes", ContentType.TEXT_PLAIN);

        builder.addBinaryBody(
                "file",
                new FileInputStream(file),
                ContentType.APPLICATION_OCTET_STREAM,
                file.getName()
        );

        HttpEntity multipart = builder.build();
        uploadFile.setEntity(multipart);
        CloseableHttpResponse response = httpClient.execute(uploadFile);

        String responseXml = EntityUtils.toString(response.getEntity());
        EntityUtils.consume(response.getEntity());

        return Integer.parseInt(responseXml);
    }

    private File multipartToFile(MultipartFile multipart, String fileName) throws IllegalStateException, IOException {
        File convFile = new File(System.getProperty("java.io.tmpdir")+"/"+fileName);
        multipart.transferTo(convFile);
        return convFile;
    }
}