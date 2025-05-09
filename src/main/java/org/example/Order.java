package org.example;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    private String id;
    private double value;
    List<String> promotions;
}
