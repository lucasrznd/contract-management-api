package br.com.lucasrznd.contractmanagementapi.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@With
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_seller")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String phoneNumber;

    /**
     * Object storage key of the seller image (or a legacy external URL).
     */
    @Column(columnDefinition = "TEXT")
    private String imageKey;

}
