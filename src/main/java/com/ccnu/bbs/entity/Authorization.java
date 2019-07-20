package com.ccnu.bbs.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class Authorization {

    /** 授权id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer authorizationId;
    /** 授权角色id. */
    private Integer authorizationRoleId;
    /** 授权权限id. */
    private Integer authorizationAuthorityId;
}
