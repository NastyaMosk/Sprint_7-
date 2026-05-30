package scooter;

import client.CourierClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Courier;
import model.CourierCredentials;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginCourierTest {
    private CourierClient courierClient;
    private int courierId;
    private String dynamicLogin;
    private final String password = "password_auth";

    @Before
    public void setUp() {
        courierClient = new CourierClient();
        // Генерируем уникальный логин для изоляции каждого запуска
        dynamicLogin = "auth_scooter_" + System.currentTimeMillis();

        Courier courier = new Courier(dynamicLogin, password, "AuthTester");
        courierClient.create(courier);
    }

    @After
    public void tearDown() {
        if (courierId == 0) {
            Response loginResponse = courierClient.login(new CourierCredentials(dynamicLogin, password));
            if (loginResponse.getStatusCode() == 200) {
                courierId = loginResponse.then().extract().path("id");
            }
        }
        if (courierId != 0) {
            courierClient.delete(courierId);
        }
    }

    @Test
    @DisplayName("Успешная авторизация курьера")
    public void courierCanLoginWithValidCredentials() {
        CourierCredentials credentials = new CourierCredentials(dynamicLogin, password);
        Response response = courierClient.login(credentials);

        response.then().statusCode(200).body("id", notNullValue());
        courierId = response.then().extract().path("id");
    }

    @Test
    @DisplayName("Ошибка авторизации при неверном логине")
    public void loginFailsWithWrongLogin() {
        CourierCredentials credentials = new CourierCredentials(dynamicLogin + "_wrong", password);
        Response response = courierClient.login(credentials);

        response.then().statusCode(404).body("message", equalTo("Учетная запись не найдена"));
    }

    @Test
    @DisplayName("Ошибка авторизации без логина")
    public void loginFailsWithoutLogin() {
        CourierCredentials credentials = new CourierCredentials("", password);
        Response response = courierClient.login(credentials);

        response.then().statusCode(400).body("message", equalTo("Недостаточно данных для входа"));
    }

    @Test
    @DisplayName("Ошибка авторизации без пароля")
    public void loginFailsWithoutPassword() {
        CourierCredentials credentials = new CourierCredentials(dynamicLogin, "");
        Response response = courierClient.login(credentials);

        response.then().statusCode(400).body("message", equalTo("Недостаточно данных для входа"));
    }
}