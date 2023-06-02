package com.example.savingstrackerapi.saving;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SavingRepository extends JpaRepository<Saving, UUID> {

}
