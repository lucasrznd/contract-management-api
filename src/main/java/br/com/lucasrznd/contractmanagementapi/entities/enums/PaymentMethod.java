package br.com.lucasrznd.contractmanagementapi.entities.enums;

import lombok.Getter;

@Getter
public enum PaymentMethod {

    BOLETO("BOLETO"),
    PIX("PIX");

    private String description;

    PaymentMethod(String description) {
        this.description = description;
    }

}
