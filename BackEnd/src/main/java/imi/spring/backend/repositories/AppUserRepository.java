package imi.spring.backend.repositories;

import imi.spring.backend.models.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
    AppUser findByEmail(String email);

    @Query("SELECT u " +
            "FROM AppUser u " +
            "WHERE u IN :#{#user.followingList} AND u.username LIKE %:username%")
    List<AppUser> findFollowingUsernameLike(@Param("user") AppUser user, @Param("username") String username);

    @Query("SELECT u " +
            "FROM AppUser u " +
            "WHERE u IN :#{#user.followersList} AND u.username LIKE %:username%")
    List<AppUser> findFollowersUsernameLike(@Param("user") AppUser user, @Param("username") String username);
}
