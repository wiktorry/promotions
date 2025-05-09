package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DataLoader {
    private final ObjectMapper mapper = new ObjectMapper();

    public List<Order> loadOrders(String path) {
        List<Order> orders = null;
        try {
            File file = new File(path);
            orders = mapper.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            System.out.println("Error while reading orders file" + e.getMessage());
        }
        return orders;
    }

    public List<PaymentMethod> loadPaymentMethods(String path) {
        List<PaymentMethod> paymentMethods = null;
        try {
            File file = new File(path);
            paymentMethods = mapper.readValue(file, new TypeReference<>() {
            });
        } catch (IOException e) {
            System.out.println("Error while reading paymentMethods file" + e.getMessage());
        }
        return paymentMethods;
    }
}
