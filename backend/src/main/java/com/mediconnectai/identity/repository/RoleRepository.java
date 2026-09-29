package com.mediconnectai.identity.repository;

import com.mediconnectai.identity.entity.Role;
import java.util.List;
import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByName(String name);
    List<Role> findAll();
}
