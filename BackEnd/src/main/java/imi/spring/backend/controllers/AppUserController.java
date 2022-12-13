package imi.spring.backend.controllers;

import imi.spring.backend.models.AppUser;
import imi.spring.backend.models.UserDTO;
import imi.spring.backend.services.AppUserService;
import imi.spring.backend.services.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class AppUserController {
    private final AppUserService appUserService;
    private final JWTService jwtService;
    private final Environment env;

    @GetMapping("/users")
    public ResponseEntity<List<AppUser>> getAllUsers(){
        return ResponseEntity.ok().body(appUserService.getAllUsers());
    }

    @PostMapping("/user/save")
    public ResponseEntity<AppUser> saveUser(@RequestBody UserDTO user) throws IOException {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/save").toUriString());
        AppUser appUser = new AppUser(user.getEmail(), user.getUsername(), user.getPassword());
        return ResponseEntity.created(uri).body(appUserService.saveUser(appUser));
    }

    @GetMapping("/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        try{
            AppUser appUser = jwtService.getAppUserFromJWT(request);
            String accessToken = jwtService.createNewJWT(request, appUser.getUsername());
            String refreshToken = request.getHeader(AUTHORIZATION).substring("Bearer".length());

            jwtService.returnJWTokens(response, accessToken, refreshToken);
        }catch (Exception exception){
            jwtService.tokenErrorResponse(response, exception);
        }
    }   

    @GetMapping("/register")
    public String registerForm(Model model){
        model.addAttribute("appUser", new AppUser());
        return "register";
    }

    @PostMapping("/register")
    @ResponseBody
    public String registerProcess(@RequestBody AppUser appUser, @RequestParam(name = "profile_image", required = false) MultipartFile profileImage) throws IOException {

        if(profileImage!=null && !profileImage.isEmpty()){
            appUser.setImage(profileImage.getBytes());
        }
        else {
            appUser.setImage(Files.readAllBytes(Path.of(env.getProperty("profile.image.default"))));
        }

        try {
            appUserService.saveUser(appUser);
        }
        catch (BadCredentialsException badCredentialsException){
            return badCredentialsException.getMessage();
        }

        return "Successfully registered.";
    }

    @GetMapping("/getMyProfilePicture")
    @ResponseBody
    public String getMyProfilePicture(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try{
            byte[] bytePicture = jwtService.getAppUserFromJWT(request).getImage();
            return new String(Base64.getEncoder().encode(bytePicture));
        } catch (ServletException e) {
            throw new ServletException(e);
        }
    }

    @GetMapping("/getProfilePictureByUserId/{userId}")
    @ResponseBody
    public String getProfilePictureByUserId(@PathVariable("userId") Long userId) throws IOException {
        log.info("GET PROFILE PICTURE");
        AppUser user = appUserService.getUserById(userId);

        if(user != null && user.getImage()!=null){
            log.info("Getting picture for user with id {}", userId);
            return new String(Base64.getEncoder().encode(user.getImage()));
        }
        else{
            log.error("Error getting picture for user with id {}", userId);
            throw new IOException("Error getting image for that user.");
        }
    }

    @PutMapping("/changeProfilePicture")
    @ResponseBody
    public String changeProfilePicture(HttpServletRequest request, HttpServletResponse response, @RequestParam(name = "profile_image") MultipartFile profileImage) throws Exception {
        try {
            AppUser appUser = jwtService.getAppUserFromJWT(request);
            if (appUser != null && profileImage != null && !profileImage.isEmpty()) {
                appUser.setImage(profileImage.getBytes());
            }
            appUserService.updateUser(appUser);
            log.info("Successfully changed profile picture for user [{}]", appUser.getUsername());
            return "Success";
        } catch (ServletException | IOException e) {
            log.error("Error changing profile picture, received message [{}]", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    @GetMapping("/getUserId")
    @ResponseBody
    public Long getUserIdByUsername(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            AppUser appUser = jwtService.getAppUserFromJWT(request);

            log.info("Getting id for user [{}]", appUser.getUsername());

            if (appUser != null ) {
                return appUser.getId();
            }else{
                throw new IOException("User not found.");
            }
        } catch (ServletException | IOException e) {
            log.error("Error finishing request. [{}]", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    @PutMapping("/user/info") //email and username
    @ResponseBody
    public String changeUserEmail(HttpServletRequest request, @RequestBody String newEmail) throws ServletException {
        try {
            AppUser appUser = jwtService.getAppUserFromJWT(request);
            return appUserService.changeUserEmail(appUser, newEmail);
        } catch (ServletException e) {
            log.error("Error changing user's email, received message [{}]", e.getMessage());
            throw new ServletException(e.getMessage());
        }
    }

    @PutMapping("/user/password")
    @ResponseBody
    public String changeUserPassword(HttpServletRequest request, @RequestBody String[] passwords) throws ServletException {
        try {
            AppUser appUser = jwtService.getAppUserFromJWT(request);
            return appUserService.changeUserPassword(appUser, passwords);
        } catch (ServletException e) {
            log.error("Error changing user password, received message [{}]", e.getMessage());
            throw new ServletException(e.getMessage());
        }
    }

    @GetMapping("/user")
    @ResponseBody
    public AppUser getUserFromJWT(HttpServletRequest request) throws ServletException {
        return jwtService.getAppUserFromJWT(request);
    }

    @GetMapping("/user/{id}")
    @ResponseBody
    public AppUser getUserByUserId(@PathVariable("id") Long id) {
        return appUserService.getUserById(id);
    }
}