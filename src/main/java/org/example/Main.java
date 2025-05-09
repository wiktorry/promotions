package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataLoader dataLoader = new DataLoader();
        List<Order> orders = dataLoader.loadOrders(args[0]);
        List<PaymentMethod> paymentMethods = dataLoader.loadPaymentMethods(args[1]);
        PromotionService promotionService = new PromotionService(orders, paymentMethods);
        promotionService.process().forEach((a, b) -> System.out.println(a + " " + b));
    }
}