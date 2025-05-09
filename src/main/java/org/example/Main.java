package org.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataLoader dataLoader = new DataLoader();
        List<Order> orders = dataLoader.loadOrders(args[0]);
        List<PaymentMethod> paymentMethods = dataLoader.loadPaymentMethods(args[1]);
    }
}