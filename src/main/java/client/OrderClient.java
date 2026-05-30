package client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.Order;

import static io.restassured.RestAssured.given;

public class OrderClient {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private static final String ORDERS_PATH = "/api/v1/orders";
    private static final String CANCEL_PATH = "/api/v1/orders/cancel";

    protected RequestSpecification getSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }

    @Step("Создать заказ")
    public Response create(Order order) {
        return given().spec(getSpec()).body(order).when().post(ORDERS_PATH);
    }

    @Step("Получить список заказов")
    public Response getOrderList() {
        return given().spec(getSpec()).when().get(ORDERS_PATH);
    }

    @Step("Отменить заказ")
    public Response cancel(int trackId) {
        return given().spec(getSpec()).queryParam("track", trackId).when().put(CANCEL_PATH);
    }
}