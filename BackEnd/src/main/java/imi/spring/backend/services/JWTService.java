package imi.spring.backend.services;

import imi.spring.backend.models.AppUser;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface JWTService {
    AppUser getAppUserFromJWT(HttpServletRequest request) throws ServletException;
    String createNewJWT(HttpServletRequest request, String username);
    String createNewRefreshJWT(HttpServletRequest request, String username);
    void tokenErrorResponse(HttpServletResponse response, Exception exception) throws IOException;
    void returnJWTokens(HttpServletResponse response, String accessToken, String refreshToken) throws IOException;
}
