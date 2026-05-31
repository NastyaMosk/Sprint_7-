package client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.Courier;
import model.CourierCredentials;

import static io.restassured.RestAssured.given;

public class CourierClient {
    private static final String BASE_URL = "https://qa-scooter.praktikum-services.ru";
    private static final String CREATE_PATH = "/api/v1/courier";
    private static final String LOGIN_PATH = "/api/v1/courier/login";
    private static final String DELETE_PATH = "/api/v1/courier/";

    protected RequestSpecification getSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URL)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }

    @Step("Создать курьера")
    public Response create(Courier courier) {
        return given().spec(getSpec()).body(courier).when().post(CREATE_PATH);
    }

    @Step("Авторизовать курьера")
    public Response login(CourierCredentials credentials) {
        return given().spec(getSpec()).body(credentials).when().post(LOGIN_PATH);
    }

    @Step("Удалить курьера")
    public Response delete(int courierId) {
        return given().spec(getSpec()).when().delete(DELETE_PATH + courierId);
    }
}