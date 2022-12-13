package imi.spring.backend.controllers;

import imi.spring.backend.models.LocationSearch;
import imi.spring.backend.services.LocationSearchService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping(path = "/location_searches")
public class LocationSearchController {

    private final LocationSearchService locationSearchService;

    @GetMapping("/all")
    @ResponseBody
    public List<LocationSearch> getAllLocationSearches() { return locationSearchService.getAllLocationSearches(); }

    @GetMapping("/my")
    @ResponseBody
    public List<LocationSearch> getMyLocationSearches(HttpServletRequest request) throws ServletException {
        return locationSearchService.getLocationSearchesByUser(request);
    }

    @GetMapping("/{id}")
    @ResponseBody
    public LocationSearch getLocationSearchById(@PathVariable Long id) { return locationSearchService.getLocationSearchById(id); }

    @PostMapping("/save_search/{locationId}")
    @ResponseBody
    String saveLocationSearch(HttpServletRequest request, @PathVariable Long locationId) throws ServletException {
        return locationSearchService.saveSearchForLocation(request, locationId);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String deleteLocationSearchById(HttpServletRequest request, @PathVariable Long id) throws ServletException {
        return locationSearchService.deleteLocationSearchById(request, id);
    }

}
