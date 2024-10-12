package br.com.lucasrznd.contractmanagementapi.entities;

import jakarta.persistence.*;
import lombok.*;

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

    private String registrationNumber;

    private String cpf;
    private String stateRegistration;
    private String phoneNumber;
    private String email;
    private String streetName;
    private String avenueName;
    private Integer number;
    private String city;
    private String state;
    private String zipCode;

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetName).append(", ").append(avenueName).append(", ");
        sb.append(number).append(", ").append(city);

        return sb.toString().toUpperCase();
    }

    public String getFullAddressWithArea() {
        StringBuilder sb = new StringBuilder();
        sb.append(streetName).append(", ").append(avenueName).append(", ");
        sb.append(number).append(", ").append(city).append(", ");
        sb.append(state).append(", ").append(zipCode);

        return sb.toString().toUpperCase();
    }

}
