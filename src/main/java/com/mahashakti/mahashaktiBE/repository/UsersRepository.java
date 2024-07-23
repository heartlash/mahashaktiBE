package com.mahashakti.mahashaktiBE.repository;

import com.mahashakti.mahashaktiBE.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {
}
