package scooter;

import client.OrderClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.notNullValue;

public class GetOrderListTest {
    private OrderClient orderClient;

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @Test
    @DisplayName("Получение списка заказов")
    public void getOrderListReturnsOrders() {
        Response response = orderClient.getOrderList();

        // Проверяем, что вернулся статус 200 и в теле есть массив orders
        response.then()
                .statusCode(200)
                .body("orders", notNullValue());
    }
}