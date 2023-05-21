package com.example.savingstrackerapi.repository;

import com.example.savingstrackerapi.model.Saving;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SavingRepository extends JpaRepository<Saving, UUID> {
}
