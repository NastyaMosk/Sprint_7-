package scooter;

import client.CourierClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Courier;
import model.CourierCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {
    private CourierClient courierClient;
    private int courierId;
    private String dynamicLogin;

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        // Генерируем уникальный логин для каждого запуска теста
        dynamicLogin = "scooter_hero_" + System.currentTimeMillis();
    }

    @After
    public void tearDown() {
        if (courierId != 0) {
            courierClient.delete(courierId);
        }
    }

    @Test
    @DisplayName("Успешное создание курьера со всеми полями")
    @Description("Проверка создания курьера, статус-кода 201 и ответа ok: true")
    public void courierCanBeCreatedWithAllFields() {
        Courier courier = new Courier(dynamicLogin, "password123", "Dmitry");
        Response response = courierClient.create(courier);

        response.then().statusCode(201).body("ok", equalTo(true));

        Response loginResponse = courierClient.login(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        if (loginResponse.getStatusCode() == 200) {
            courierId = loginResponse.then().extract().path("id");
        }
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void cannotCreateTwoIdenticalCouriers() {
        Courier courier = new Courier(dynamicLogin, "password123", "Dmitry");
        // Создаем первого курьера
        courierClient.create(courier);

        // Логинимся, чтобы получить ID для удаления в tearDown
        Response loginResponse = courierClient.login(new CourierCredentials(courier.getLogin(), courier.getPassword()));
        if (loginResponse.getStatusCode() == 200) {
            courierId = loginResponse.then().extract().path("id");
        }

        // Пытаемся создать точно такого же второго курьера
        Response response = courierClient.create(courier);
        response.then().statusCode(409).body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Нельзя создать курьера без логина")
    public void cannotCreateCourierWithoutLogin() {
        // Передаем пустую строку вместо null для гарантированной отправки поля в JSON
        Courier courier = new Courier("", "password123", "Dmitry");
        Response response = courierClient.create(courier);

        response.then().statusCode(400).body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Нельзя создать курьера без пароля")
    public void cannotCreateCourierWithoutPassword() {
        // Передаем пустую строку вместо null, чтобы избежать зависания ручки на стенде
        Courier courier = new Courier(dynamicLogin, "", "Dmitry");
        Response response = courierClient.create(courier);

        response.then().statusCode(400).body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }
}