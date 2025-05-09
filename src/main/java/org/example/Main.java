package org.example;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataLoader dataLoader = new DataLoader();
        List<Order> orders = dataLoader.loadJson(args[0],
                new TypeReference<List<Order>>() {
                });
        List<PaymentMethod> paymentMethods = dataLoader.loadJson(args[1],
                new TypeReference<List<PaymentMethod>>() {
                });
        PromotionService promotionService = new PromotionService(orders, paymentMethods);
        promotionService.process().forEach((a, b) -> System.out.println(a + " " + b));
    }
}