package com.its.springjwt.repository;

import java.util.List;
import java.util.Optional;

import com.its.springjwt.models.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.its.springjwt.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

	@Query(value = "SELECT * FROM users ORDER BY id DESC LIMIT 0, 1", nativeQuery = true)
	Optional<User> getLastUser();

	List<User> findByUsernameContaining(String username);

	List<User> findTop2ByOrderByIdDesc();
}
