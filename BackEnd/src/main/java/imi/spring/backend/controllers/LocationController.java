package imi.spring.backend.controllers;

import imi.spring.backend.models.Location;
import imi.spring.backend.services.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping(path = "/location")
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/all")
    @ResponseBody
    public List<Location> getAllLocations() { return locationService.getLocations(); }

    @GetMapping("/{id}")
    @ResponseBody
    public Location getLocationById(@PathVariable Long id) { return locationService.getLocationById(id); }

    @GetMapping("/keyword/{name}")
    @ResponseBody
    public List<Location> getLocationsById(@PathVariable String name) { return locationService.getLocationsByNameKeyword(name); }

    @PostMapping("/save")
    @ResponseBody
    public Location saveLocation(@RequestBody Location location) { return locationService.saveLocation(location); }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public String deleteLocation(@PathVariable Long id) { return locationService.deleteLocation(id); }
}
