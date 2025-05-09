package org.example;

import java.util.*;

public class PromotionService {
    private List<Order> orders;
    private List<PaymentMethod> paymentMethods;
    private Map<String, Set<String>> paymentMethodsToOrders;

    PromotionService(List<Order> orders, List<PaymentMethod> paymentMethods) {
        this.orders = orders;
        this.paymentMethods = paymentMethods;
        this.paymentMethods.sort(Comparator.comparing(PaymentMethod::getDiscount));
    }

    public Map<String, Double> process() throws Exception {
        Order bestOrder;
        Map<String, Double> result = new HashMap<>();
        for (PaymentMethod paymentMethod : paymentMethods) {
            bestOrder = orders.stream()
                    .filter(order -> order.getValue() <= paymentMethod.getLimit())
                    .max(Comparator.comparing(Order::getValue))
                    .orElseThrow(() -> new Exception("Problem with algorithm"));
            paymentMethod.setLimit(paymentMethod.getDiscount() - bestOrder.getValue());
            orders.remove(bestOrder);
            result.put(paymentMethod.getId(), bestOrder.getValue());
        }
        return result;
    }
}
