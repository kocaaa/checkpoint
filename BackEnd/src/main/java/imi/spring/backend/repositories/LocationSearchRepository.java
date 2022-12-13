package imi.spring.backend.repositories;

import imi.spring.backend.models.AppUser;
import imi.spring.backend.models.LocationSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationSearchRepository extends JpaRepository<LocationSearch, Long> {
    List<LocationSearch> findLocationSearchesByUserOrderByTimeDesc(AppUser user);
}
