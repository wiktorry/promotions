package org.example;

import java.util.*;

public class PromotionService {
    private List<Order> orders;
    private List<PaymentMethod> paymentMethods;
    private double pointsAmount = 0;

    public PromotionService(List<Order> orders, List<PaymentMethod> paymentMethods) {
        this.orders = orders;
        this.paymentMethods = paymentMethods;
        this.paymentMethods.sort(Comparator.comparing(PaymentMethod::getDiscount).reversed());
    }

    public Map<String, Double> process() {
        List<Order> bestOrders;
        double price;
        Map<String, Double> result = new HashMap<>();
        for (PaymentMethod paymentMethod : paymentMethods) {
            bestOrders = orders.stream()
                    .filter(order -> order.getValue() <= paymentMethod.getLimit())
                    .sorted(Comparator.comparing(Order::getValue))
                    .toList().reversed();
            Order bestOrder = bestOrders.getFirst();
            while (bestOrder.getValue() <= paymentMethod.getLimit()) {
                price = bestOrder.getValue() - bestOrder.getValue() * ((double) paymentMethod.getDiscount() / 100);
                if (paymentMethod.getId().equals("PUNKTY")) {
                    pointsAmount = paymentMethod.getLimit() - price;
                }
                paymentMethod.setLimit(paymentMethod.getLimit() - price);
                orders.remove(bestOrder);
                result.merge(paymentMethod.getId(), price, Double::sum);
            }
        }
        return processRestOrders(result);
    }

    private Map<String, Double> processRestOrders(Map<String, Double> result) {
        orders.sort(Comparator.comparing(Order::getValue).reversed());
        double pointsCost;
        Order cheapestOrder = orders.getLast();
        PaymentMethod bestPaymentMethod;
        double promotion = 0;
        List<Order> ordersToDelete = new ArrayList<>();
        for (Order order : orders) {
            if (order.getValue() * 0.1 <= pointsAmount) {
                pointsCost = order.getValue() * 0.1;
                promotion = pointsCost;
                pointsAmount = pointsAmount - pointsCost;
                if (pointsAmount < cheapestOrder.getValue() * 0.1 || cheapestOrder == order) {
                    order.setValue(order.getValue() - (pointsAmount + pointsCost + promotion));
                    result.merge("PUNKTY", pointsAmount + pointsCost, Double::sum);
                } else {
                    order.setValue(order.getValue() - pointsCost - promotion);
                    result.merge("PUNKTY", pointsCost, Double::sum);
                }
            }
            bestPaymentMethod = paymentMethods.stream()
                    .filter(paymentMethod -> !paymentMethod.getId().equals("PUNKTY"))
                    .filter(paymentMethod ->
                            paymentMethod.getLimit() >= order.getValue())
                    .min(Comparator.comparing(PaymentMethod::getLimit))
                    .orElseThrow(() -> new RuntimeException("Algorithm problem"));
            bestPaymentMethod.setLimit(bestPaymentMethod.getLimit() - order.getValue());
            result.merge(bestPaymentMethod.getId(), order.getValue(), Double::sum);
            ordersToDelete.add(order);
        }
        orders.removeAll(ordersToDelete);
        return result;
    }
}
