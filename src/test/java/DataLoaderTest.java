import com.fasterxml.jackson.core.type.TypeReference;
import org.example.DataLoader;
import org.example.Order;
import org.example.PaymentMethod;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DataLoaderTest {
    @Test
    public void shouldLoadOrders() {
        DataLoader loader = new DataLoader();
        List<Order> orders = loader.loadJson("src/main/resources/orders.json",
                new TypeReference<List<Order>>() {
                });
        Assert.assertEquals(4, orders.size());
    }

    @Test
    public void shouldLoadPaymentMethods() {
        DataLoader loader = new DataLoader();
        List<PaymentMethod> paymentMethods = loader.loadJson("src/main/resources/paymentmethods.json",
                new TypeReference<List<PaymentMethod>>() {
                });
        Assert.assertEquals(3, paymentMethods.size());
    }

    @Test
    public void shouldReturnNullWithWrongPath() {
        DataLoader loader = new DataLoader();
        List<PaymentMethod> paymentMethods = loader.loadJson("src/main/resources/file.json",
                new TypeReference<List<PaymentMethod>>() {
                });
        Assert.assertNull(paymentMethods);
    }
}
