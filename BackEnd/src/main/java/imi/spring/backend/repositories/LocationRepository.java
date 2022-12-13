package imi.spring.backend.repositories;

import imi.spring.backend.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByNameLike(String name);
}
