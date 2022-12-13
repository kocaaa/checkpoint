package imi.spring.backend.services.implementations;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import imi.spring.backend.models.AppUser;
import imi.spring.backend.services.AppUserService;
import imi.spring.backend.services.JWTService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class JWTServiceImpl implements JWTService {

    private final AppUserService appUserService;

    @Override
    public AppUser getAppUserFromJWT(HttpServletRequest request) throws ServletException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            try {
                String refreshToken = authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("sEcReT".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);
                String username = decodedJWT.getSubject();

                AppUser appUser = appUserService.getUserByUsername(username);

                log.info("Found user {} with received JWT.", username);

                return appUser;
            } catch (Exception exception) {
                log.error("Exception getting user from JWT.");
            }
        }
        else{
            log.error("Problem with token.");
            throw new ServletException("Problem with token.");
        }
        return null;
    }

    @Override
    public String createNewJWT(HttpServletRequest request, String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("role", "user")
                .withClaim("userId", appUserService.getUserByUsername(username).getId())
                .sign(getAlgorithm());
    }

    @Override
    public String createNewRefreshJWT(HttpServletRequest request, String username) {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + 7*24*60*60*1000))
                .withIssuer(request.getRequestURL().toString())
                .sign(getAlgorithm());
    }

    @Override
    public void tokenErrorResponse(HttpServletResponse response, Exception exception) throws IOException {
        response.setHeader("error",  exception.getMessage());
        response.setStatus(FORBIDDEN.value());
        Map<String,String> error = new HashMap<>();
        error.put("error_message", exception.getMessage());
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }

    @Override
    public void returnJWTokens(HttpServletResponse response, String accessToken, String refreshToken) throws IOException {
        Map<String,String> tokens = new HashMap<>();
        tokens.put("access_token", accessToken);
        tokens.put("refresh_token", refreshToken);
        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }


    private Algorithm getAlgorithm(){
        return Algorithm.HMAC256("sEcReT".getBytes());
    }
}
