package org.example;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethod {
    private String id;
    private int discount;
    private double limit;
}
