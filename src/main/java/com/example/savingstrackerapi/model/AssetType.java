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
@Table(name = "assets_type")
public class AssetType {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private String name;

  @OneToMany(targetEntity = Asset.class, cascade = CascadeType.ALL)
  @JoinColumn(name = "type", referencedColumnName = "id")
  List<Asset> assetList;

  @Override
  public String toString() {
    return "AssetType{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", assetList=" + assetList +
            '}';
  }
}
