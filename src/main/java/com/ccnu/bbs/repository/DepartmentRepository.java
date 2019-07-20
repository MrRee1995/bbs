package com.ccnu.bbs.repository;

import com.ccnu.bbs.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    @Query("select d from Department d where d.departmentId = ?1")
    Department findUserDepartment(Integer departmentId);
}
