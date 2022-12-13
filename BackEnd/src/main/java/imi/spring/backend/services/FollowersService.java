package imi.spring.backend.services;

import imi.spring.backend.models.AppUser;

import java.util.List;

public interface FollowersService {
    List<AppUser> getAllFollowingByUser(Long userId);

    List<AppUser> getAllFollowersPerUser(Long userId);

    String followOrUnfollowUser(Long user1id, Long user2id);

    Integer countAllFollowingByUser(Long userId);

    Integer countAllFollowersPerUser(Long userId);

    List<AppUser> getFollowingByUsername(Long userId, String username);

    List<AppUser> getFollowersByUsername(Long userId, String username);

    List<AppUser> getMyFollowing(AppUser user);

    List<AppUser> getMyFollowers(AppUser user);

    Integer countMyFollowing(AppUser user);

    Integer countMyFollowers(AppUser user);

    List<AppUser> getMyFollowingByUsername(AppUser user, String username);

    List<AppUser> getMyFollowersByUsername(AppUser user, String username);
}
