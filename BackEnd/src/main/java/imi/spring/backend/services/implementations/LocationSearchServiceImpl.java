package imi.spring.backend.services.implementations;

import imi.spring.backend.models.AppUser;
import imi.spring.backend.models.Location;
import imi.spring.backend.models.LocationSearch;
import imi.spring.backend.repositories.LocationSearchRepository;
import imi.spring.backend.services.JWTService;
import imi.spring.backend.services.LocationSearchService;
import imi.spring.backend.services.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@Service
public class LocationSearchServiceImpl implements LocationSearchService {

    private final LocationSearchRepository locationSearchRepository;
    private final LocationService locationService;
    private final JWTService jwtService;

    @Override
    public List<LocationSearch> getAllLocationSearches() {
        return locationSearchRepository.findAll();
    }

    @Override
    public List<LocationSearch> getLocationSearchesByUser(HttpServletRequest request) throws ServletException {
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user != null)
            return locationSearchRepository.findLocationSearchesByUserOrderByTimeDesc(user);
        return Collections.emptyList();
    }

    @Override
    public LocationSearch getLocationSearchById(Long id) {
        return locationSearchRepository.findById(id).orElse(null);
    }

    @Override
    public String saveSearchForLocation(HttpServletRequest request, Long locationId) throws ServletException {
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user == null)
            return "Invalid user!";
        Location location = locationService.getLocationById(locationId);
        if (location == null)
            return "Location with that id does not exist!";
        locationSearchRepository.save(new LocationSearch(null, LocalDateTime.now(), location, user));
        return "Location search has been successfully saved.";
    }

    @Override
    public String deleteLocationSearchById(HttpServletRequest request, Long id) throws ServletException {
        LocationSearch locationSearch = locationSearchRepository.findById(id).orElse(null);
        if (locationSearch == null)
            return "Location search with that id does not exist!";
        AppUser user = jwtService.getAppUserFromJWT(request);
        if (user == null || !(user.getId().equals(locationSearch.getUser().getId())))
            return "Invalid user!";
        locationSearchRepository.deleteById(id);
        return "Location search has been successfully deleted.";
    }

}
