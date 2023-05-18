package com.example.savingstrackerapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assets")
public class Asset {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private String name;

  @OneToMany(targetEntity = Saving.class, cascade = CascadeType.ALL)
  @JoinColumn(name = "asset", referencedColumnName = "id")
  List<Saving> savingList;
}
