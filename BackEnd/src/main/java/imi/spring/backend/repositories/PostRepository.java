package imi.spring.backend.repositories;

import imi.spring.backend.models.AppUser;
import imi.spring.backend.models.Location;
import imi.spring.backend.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PostRepository extends JpaRepository<Post, Long> {

    Long countAllByUser(AppUser user);
    List<Post> findAllByLocation(Location location);
}
