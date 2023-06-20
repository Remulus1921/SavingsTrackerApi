package com.example.savingstrackerapi.saving;

import com.example.savingstrackerapi.asset.Asset;
import com.example.savingstrackerapi.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "savings")
@Getter
@Setter
public class Saving {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private Double amount;

  @ManyToOne
  private User user;
  @ManyToOne
  private Asset asset;

//  @Override
//  public String toString() {
//    return "Saving{" +
//            "id=" + id +
//            ", amount=" + amount +
//            ", user=" + user +
//            ", asset=" + asset +
//            '}';
//  }
}
