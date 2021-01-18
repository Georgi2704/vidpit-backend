package com.its.springjwt.repository;

import com.its.springjwt.models.User;
import com.its.springjwt.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    Optional<Video> findByTitle(String title);

    List <Video> findTop12ByOrderByIdDesc();

    Optional<Video> findByVideoContent(String videoContent);

    void deleteByVideoContent(String videoContent);

}