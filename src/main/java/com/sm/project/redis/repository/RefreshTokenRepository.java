package com.sm.project.redis.repository;

import com.sm.project.redis.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
