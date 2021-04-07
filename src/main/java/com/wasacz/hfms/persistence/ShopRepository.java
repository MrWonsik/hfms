package com.wasacz.hfms.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {


    Optional<List<Shop>> findAllByUserAndIsDeletedFalse(User user);

    Optional<Shop> findByIdAndUserAndIsDeletedFalse(Long id, User user);
}
