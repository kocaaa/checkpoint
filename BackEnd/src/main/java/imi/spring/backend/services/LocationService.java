package imi.spring.backend.services;

import imi.spring.backend.models.Location;

import java.util.List;

public interface LocationService {
    List<Location> getLocations();

    Location saveLocation(Location location);

    String deleteLocation(Long id);

    Location getLocationById(Long id);

    List<Location> getLocationsByNameKeyword(String name);
}
