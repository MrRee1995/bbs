package com.ccnu.bbs.repository;

import com.ccnu.bbs.entity.Authorization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorizationRepository extends JpaRepository<Authorization, Integer> {
}
