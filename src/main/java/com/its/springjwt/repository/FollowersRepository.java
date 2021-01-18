package com.its.springjwt.repository;

import com.its.springjwt.models.Category;
import com.its.springjwt.models.Followers;
import com.its.springjwt.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface FollowersRepository extends JpaRepository<Followers, Long> {
    Optional <Followers> findTopByFollowed_IdAndFollowedBy_IdOrderByIdDesc(Long followedId, Long followedById);

    List<Followers> findAllByFollowed_IdAndIsFollowAndDateOfFollowBetween(Long id, boolean isFollow, Date start, Date end);

    List<Followers> findAllByFollowed_IdAndIsFollow(Long id, boolean isFollow);

    List<Followers> findAllByFollowedBy_Id(Long id);

}