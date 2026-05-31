package ntu.phamgiakhiem.PhoneStore.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import ntu.phamgiakhiem.PhoneStore.models.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
