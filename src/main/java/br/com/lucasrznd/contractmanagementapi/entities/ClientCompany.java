package br.com.lucasrznd.contractmanagementapi.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

@Getter
@Setter
@With
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_company")
public class ClientCompany {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String businessName;

    @Column(unique = true)
    private String tradeName;

    @Column(unique = true)
    private String registrationNumber;

    @Column(unique = true)
    private String stateRegistration;
    private String phoneNumber;
    private String email;
    private String streetName;
    private String avenueName;
    private Integer number;
    private String city;
    private String state;
    private String zipCode;

}
