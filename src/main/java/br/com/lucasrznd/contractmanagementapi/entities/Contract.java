package br.com.lucasrznd.contractmanagementapi.entities;

import br.com.lucasrznd.contractmanagementapi.entities.enums.PaymentMethod;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.With;

import java.time.LocalDate;

@Getter
@Setter
@With
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_contract")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ClientCompany clientCompany;

    @ManyToOne
    private Seller seller;
    private Integer advertisingOrder;
    private Double spotDuration;
    private int quantitySpotDay;
    private Double testimonialDuration;
    private int testimonialQuantity;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double monthlyPrice;
    private int flashQuantity;
    private String newspaperParticipation;
    private PaymentMethod paymentMethod;
    private Integer paymentDueDay;
    private String observation;

}
