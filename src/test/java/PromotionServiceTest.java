import org.example.Order;
import org.example.PaymentMethod;
import org.example.PromotionService;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PromotionServiceTest {
    @Test
    public void shouldReturnOptimalResultForExampleData() {
        List<Order> orders = new ArrayList<>(List.of(
                new Order("ORDER1", 100, new ArrayList<>(List.of("mZysk"))),
                new Order("ORDER2", 200, new ArrayList<>(List.of("BosBankrut"))),
                new Order("ORDER3", 150, new ArrayList<>(List.of("mZysk", "BosBankrut"))),
                new Order("ORDER4", 50, null)
        ));
        List<PaymentMethod> paymentMethods = new ArrayList<>(List.of(
                new PaymentMethod("PUNKTY", 15, 100),
                new PaymentMethod("mZysk", 10, 180),
                new PaymentMethod("BosBankrut", 5, 200)
        ));
        PromotionService promotionService = new PromotionService(orders, paymentMethods);
        Map<String, Double> result = promotionService.process();
        Assert.assertEquals(new HashMap<>(Map.of(
                "mZysk", 165.0,
                "BosBankrut", 190.0,
                "PUNKTY", 100.0
        )), result);
    }

    @Test
    public void shouldReturnOptimalResult() {
        //Situation when is more optimal to have 2x -10% than one "PUNKTY" promotion
        List<Order> orders = new ArrayList<>(List.of(
                new Order("ORDER1", 50, null),
                new Order("ORDER2", 100, null)
        ));
        List<PaymentMethod> paymentMethods = new ArrayList<>(List.of(
                new PaymentMethod("PUNKTY", 15, 50),
                new PaymentMethod("BosBankrut", 5, 200)
        ));
        PromotionService promotionService = new PromotionService(orders, paymentMethods);
        Map<String, Double> result = promotionService.process();
        Assert.assertEquals(new HashMap<>(Map.of(
                "BosBankrut", 100.0,
                "PUNKTY", 50.0
        )), result);
    }

    @Test
    public void shouldReturnEmptyResultForEmptyData() {
        List<Order> orders = new ArrayList<>();
        List<PaymentMethod> paymentMethods = new ArrayList<>();
        PromotionService promotionService = new PromotionService(orders, paymentMethods);
        Map<String, Double> result = promotionService.process();
        Assert.assertEquals(new HashMap<>(), result);
    }
    
}
