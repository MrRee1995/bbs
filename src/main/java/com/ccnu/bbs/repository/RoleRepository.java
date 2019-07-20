package com.ccnu.bbs.repository;

import com.ccnu.bbs.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    // 查看某用户的身份
    @Query("select r from Role r, com.ccnu.bbs.entity.User u where " +
            "r.roleType = u.userRoleType and u.userId = ?1")
    Role findUserRole(String userId);
}
