package org.example;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Please provide two paths");
            return;
        }
        DataLoader dataLoader = new DataLoader();
        List<Order> orders = dataLoader.loadJson(args[0],
                new TypeReference<List<Order>>() {
                });
        List<PaymentMethod> paymentMethods = dataLoader.loadJson(args[1],
                new TypeReference<List<PaymentMethod>>() {
                });
        if (orders == null || paymentMethods == null) {
            return;
        }
        PromotionService promotionService = new PromotionService(orders, paymentMethods);
        promotionService.process().forEach((paymentId, totalSpend) ->
                System.out.println(paymentId + " " + totalSpend));
    }
}