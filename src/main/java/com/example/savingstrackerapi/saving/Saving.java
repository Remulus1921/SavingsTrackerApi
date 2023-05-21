package com.example.savingstrackerapi.saving;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "savings")
public class Saving {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private Double amount;

  @Override
  public String toString() {
    return "Saving{" +
            "id=" + id +
            ", amount=" + amount +
            '}';
  }
}
