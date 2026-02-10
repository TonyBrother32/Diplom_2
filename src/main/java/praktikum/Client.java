package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class Client extends RestAssuredClient {
    private final String REGISTER_PATH = "/auth/register";
    private final String LOGIN_PATH = "/auth/login";
    private final String AUTH_CLIENT = "/auth/user";

    @Step("Создание пользователя")
    public ValidatableResponse createClient(ClientGenerator client) {
        return given()
                .spec(requestSpecification())
                .body(client)
                .when()
                .post(REGISTER_PATH)
                .then().log().ifError();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse loginClient(ClientGenerator client) {
        return given()
                .spec(requestSpecification())
                .body(client)
                .when()
                .post(LOGIN_PATH)
                .then().log().ifError();
    }

    @Step("Обновление пользователя без авторизации")
    public ValidatableResponse updateClientWithoutAuthorization(ClientGenerator client) {
        return given()
                .spec(requestSpecification())
                .body(client)
                .when()
                .patch(AUTH_CLIENT)
                .then().log().ifError();
    }

    @Step("Обновление пользователя с авторизацией")
    public ValidatableResponse updateClientWithAuthorization(ClientGenerator client, String token) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", token)
                .body(client)
                .when()
                .patch(AUTH_CLIENT)
                .then().log().ifError();
    }

    @Step("Удаление пользователя")
    public ValidatableResponse deleteClient(String token) {
        return given()
                .spec(requestSpecification())
                .header("Authorization", token)
                .when()
                .delete(AUTH_CLIENT)
                .then().log().ifError();
    }
}
