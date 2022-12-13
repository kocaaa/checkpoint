package imi.spring.backend.services;

import imi.spring.backend.models.LocationSearch;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface LocationSearchService {

    List<LocationSearch> getAllLocationSearches();

    LocationSearch getLocationSearchById(Long id);

    String saveSearchForLocation(HttpServletRequest request, Long locationId) throws ServletException;

    List<LocationSearch> getLocationSearchesByUser(HttpServletRequest request) throws ServletException;

    String deleteLocationSearchById(HttpServletRequest request, Long id) throws ServletException;
}
