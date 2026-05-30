package scooter;

import client.OrderClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Order;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;

@RunWith(Parameterized.class)
public class CreateOrderParamTest {
    private OrderClient orderClient;
    private int trackId;
    private final List<String> colors;

    public CreateOrderParamTest(List<String> colors) {
        this.colors = colors;
    }

    @Parameterized.Parameters(name = "Цвета заказа: {0}")
    public static Object[][] getColors() {
        return new Object[][] {
                { Arrays.asList("BLACK") },
                { Arrays.asList("GREY") },
                { Arrays.asList("BLACK", "GREY") },
                { Collections.emptyList() }
        };
    }

    @Before
    public void setUp() {
        orderClient = new OrderClient();
    }

    @After
    public void tearDown() {
        if (trackId != 0) {
            orderClient.cancel(trackId);
        }
    }

    @Test
    @DisplayName("Параметризованное создание заказа с разными цветами")
    public void orderCreationTest() {
        // Передаем "4" вместо текстового названия станции метро
        Order order = new Order("Ivan", "Ivanov", "Red Square 1", "4", "+79998887766", 3, "2026-07-07", "Fast delivery", colors);
        Response response = orderClient.create(order);

        response.then().statusCode(201).body("track", notNullValue());
        trackId = response.then().extract().path("track");
    }
}