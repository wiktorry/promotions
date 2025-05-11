package org.example;

import javax.xml.transform.Result;
import java.util.*;
import java.util.stream.Collectors;

public class PromotionService {
    private List<Order> orders;
    private List<PaymentMethod> paymentMethods;
    private double pointsAmount = 0;

    public PromotionService(List<Order> orders, List<PaymentMethod> paymentMethods) {
        this.orders = orders;
        this.paymentMethods = paymentMethods;
        this.paymentMethods.sort(Comparator.comparing(PaymentMethod::getDiscount).reversed());
    }

    private List<Order> findOrdersWithDiscountUnder10() {
        List<Order> result = new ArrayList<>();
        for (Order order : orders) {
            if (order.getPromotions() != null) {
                List<String> promotions = order.getPromotions();
                List<PaymentMethod> payMethods = paymentMethods.stream()
                        .filter(payMethod -> promotions.contains(payMethod.getId())
                                && payMethod.getDiscount() >= 10)
                        .toList();
                if (payMethods.isEmpty()) {
                    result.add(order);
                }
            } else {
                result.add(order);
            }
        }
        result.sort(Comparator.comparing(Order::getValue).reversed());
        return result;
    }

    private double findBiggestProfit(Order order) {
        double biggestProfit = 0;
        for (String promotion : order.getPromotions()) {
            Optional<PaymentMethod> methodOptional = paymentMethods.stream()
                    .filter(payMethod -> payMethod.getId().equals(promotion))
                    .findFirst();
            if (methodOptional.isPresent()) {
                double profit = order.getValue() * methodOptional.get().getDiscount() / 100;
                if (profit > biggestProfit) {
                    biggestProfit = profit;
                }
            }
        }
        return biggestProfit;
    }

    private double getProfitFromPointsDiscount(List<Order> ordersToRemove, Map<String, Double> toPay) {
        List<Order> ordersToCheck = findOrdersWithDiscountUnder10();
        double totalProfit = 0;
        Optional<PaymentMethod> pointsMethodOptional = paymentMethods.stream()
                .filter(payMethod -> payMethod.getId().equals("PUNKTY"))
                .findFirst();
        if (pointsMethodOptional.isPresent()) {
            PaymentMethod pointsMethod = pointsMethodOptional.get();
            double points = pointsMethod.getLimit();
            for (Order order : ordersToCheck) {
                double biggestProfit = 0;
                if (order.getPromotions() != null) {
                    biggestProfit = findBiggestProfit(order);
                }
                double pointsCost = order.getValue() * 0.1;
                if (points >= pointsCost) {
                    points -= pointsCost;
                    pointsAmount = points;
                    toPay.merge("PUNKTY", pointsCost, Double::sum);
                    toPay.put("unknown_" + order.getId(), order.getValue() * 0.9);
                    totalProfit += order.getValue() * 0.1 - biggestProfit;
                    ordersToRemove.add(order);
                }
            }
        }
        makeMaximumUsageFromPoints(toPay);
        return totalProfit;
    }

    private void makeMaximumUsageFromPoints(Map<String, Double> toPay) {
        double usedPoints = 0;
        for (Map.Entry<String, Double> entry : toPay.entrySet()) {
            if (!entry.getKey().equals("PUNKTY")) {
                double amount = entry.getValue();
                if (pointsAmount <= amount) {
                    entry.setValue(amount - pointsAmount);
                    usedPoints += pointsAmount;
                    pointsAmount = 0;
                } else {
                    pointsAmount -= amount;
                    usedPoints += amount;
                    entry.setValue(0.0);
                }
            }
        }
        toPay.merge("PUNKTY", usedPoints, Double::sum);
    }

    private double getProfitFromPointsMaxOrdersDiscount() {
        double totalProfit = 0;
        Optional<PaymentMethod> pointsMethodOptional = paymentMethods.stream()
                .filter(payMethod -> payMethod.getId().equals("PUNKTY"))
                .findFirst();
        if (pointsMethodOptional.isPresent()) {
            PaymentMethod pointsMethod = pointsMethodOptional.get();
            List<Order> bestOrders = orders.stream()
                    .filter(order -> order.getValue() <= pointsMethod.getLimit())
                    .sorted(Comparator.comparing(Order::getValue))
                    .collect(Collectors.toList())
                    .reversed();
            double points = pointsMethod.getLimit();
            while (!bestOrders.isEmpty()) {
                Order bestOrder = bestOrders.removeFirst();
                if (bestOrder.getValue() <= points) {
                    double pointsCost = bestOrder.getValue() * (100 - pointsMethod.getDiscount()) / 100;
                    points -= pointsCost;
                    totalProfit += bestOrder.getValue() * pointsMethod.getDiscount() / 100;
                }
            }
        }
        return totalProfit;
    }

    private PaymentMethod findBestPaymentMethod(double price) {
        return paymentMethods.stream()
                .filter(paymentMethod -> !paymentMethod.getId().equals("PUNKTY"))
                .filter(paymentMethod ->
                        paymentMethod.getLimit() >= price)
                .min(Comparator.comparing(PaymentMethod::getLimit))
                .orElseThrow(() -> new RuntimeException("Algorithm problem"));
    }

    private void processPoints(Map<String, Double> result) {
        Map<String, Double> toPay = new HashMap<>();
        List<Order> ordersToRemove = new ArrayList<>();
        //profit when we are looking for the biggest order value with PUNKTY discount
        double profitFromPointsMaxOrdersDiscount = getProfitFromPointsMaxOrdersDiscount();
        //profit when we are trying to get the best value from -10% points discount
        double profitFromPointsDiscount = getProfitFromPointsDiscount(ordersToRemove, toPay);
        if (profitFromPointsDiscount > profitFromPointsMaxOrdersDiscount) {
            orders.removeAll(ordersToRemove);
            result.merge("PUNKTY", toPay.get("PUNKTY"), Double::sum);
            Optional<PaymentMethod> pointsMethod = paymentMethods.stream()
                    .filter(method -> method.getId().equals("PUNKTY"))
                    .findFirst();
            pointsMethod.ifPresent(paymentMethod -> {
                double newLimit = paymentMethod.getLimit() - toPay.get("PUNKTY");
                paymentMethod.setLimit(newLimit);
                pointsAmount = newLimit;
            });
            toPay.remove("PUNKTY");
            for (Map.Entry<String, Double> entry : toPay.entrySet()) {
                double price = entry.getValue();
                PaymentMethod bestPaymentMethod = findBestPaymentMethod(price);
                bestPaymentMethod.setLimit(bestPaymentMethod.getLimit() - price);
                result.merge(bestPaymentMethod.getId(), price, Double::sum);
            }
        }
    }

    public Map<String, Double> process() {
        List<Order> bestOrders;
        double price;
        Map<String, Double> result = new HashMap<>();
        processPoints(result);
        for (PaymentMethod paymentMethod : paymentMethods) {
            bestOrders = orders.stream()
                    .filter(order -> order.getValue() <= paymentMethod.getLimit())
                    .filter(order -> paymentMethod.getId().equals("PUNKTY") ||
                            (order.getPromotions() != null &&
                                    order.getPromotions().contains(paymentMethod.getId())))
                    .sorted(Comparator.comparing(Order::getValue))
                    .collect(Collectors.toList())
                    .reversed();
            while (!bestOrders.isEmpty() && bestOrders.getFirst().getValue() <= paymentMethod.getLimit()) {
                Order bestOrder = bestOrders.removeFirst();
                price = bestOrder.getValue() - bestOrder.getValue() * ((double) paymentMethod.getDiscount() / 100);
                if (paymentMethod.getId().equals("PUNKTY")) {
                    pointsAmount = paymentMethod.getLimit() - price;
                }
                paymentMethod.setLimit(paymentMethod.getLimit() - price);
                orders.remove(bestOrder);
                result.merge(paymentMethod.getId(), price, Double::sum);
            }
        }
        if (!orders.isEmpty()) {
            return processRestOrders(result);
        }
        return result;
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
            bestPaymentMethod = findBestPaymentMethod(order.getValue());
            bestPaymentMethod.setLimit(bestPaymentMethod.getLimit() - order.getValue());
            result.merge(bestPaymentMethod.getId(), order.getValue(), Double::sum);
            ordersToDelete.add(order);
        }
        orders.removeAll(ordersToDelete);
        return result;
    }
}
