package br.com.lucasrznd.contractmanagementapi.entities;

import br.com.lucasrznd.contractmanagementapi.entities.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;

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
    private String advertisingOrder;
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

    @Column(columnDefinition = "TEXT")
    private String pdfPath;

    @Column(columnDefinition = "TEXT")
    private String token;

}
