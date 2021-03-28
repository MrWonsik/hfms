package com.wasacz.hfms.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ShopRepository extends JpaRepository<Shop, Long> {


    Optional<Shop> findByShopNameAndUser(String name, User user);

    Optional<List<Shop>> findAllByUser(User user);

    Optional<Shop> findByIdAndUser(Long id, User user);
}
