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
    private Integer advertisingOrder;
    private Double spotDuration;
    private int quantitySpotDay;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double monthlyPrice;
    private int flashQuantity;
    private String newspaperParticipation;
    private PaymentMethod paymentMethod;
    private LocalDate paymentDueDay;
    private String observation;

}
