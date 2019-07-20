package com.ccnu.bbs.repository;

import com.ccnu.bbs.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AuthorityRepository extends JpaRepository<Authority, Integer> {

    // 查看某身份的权限
    @Query("select ay from Authority ay, com.ccnu.bbs.entity.Authorization an " +
            "where ay.authorityId = an.authorizationId and an.authorizationRoleId = ?1")
    Authority findRoleAuthority(Integer roleId);
}
