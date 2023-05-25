package com.example.savingstrackerapi.saving;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavingRepository extends JpaRepository<Saving, UUID> {

  @Query("SELECT s FROM User u JOIN u.savingList s WHERE u.id = ?1")
  List<Saving> findUserSavings(UUID userId);
}