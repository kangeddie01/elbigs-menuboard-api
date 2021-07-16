package com.elbigs.jpaRepository;

import com.elbigs.entity.UserRolesEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRolesRepo extends CrudRepository<UserRolesEntity, Long> {
    List<UserRolesEntity> findByLoginId(String loginId);
}
