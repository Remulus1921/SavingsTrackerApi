package com.example.savingstrackerapi.assetType;

import com.example.savingstrackerapi.asset.Asset;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "assets_type")
@Getter
@Setter
public class AssetType {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private String name;

  @OneToMany(mappedBy = "assetType", cascade = CascadeType.ALL)
  List<Asset> assetList;

  public AssetType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "AssetType{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", assetList=" + assetList +
            '}';
  }
}
