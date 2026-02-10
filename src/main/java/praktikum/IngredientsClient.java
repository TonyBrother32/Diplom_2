package praktikum;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class IngredientsClient extends RestAssuredClient {
    public final String PATH = "/ingredients";

    @Step("Получение данных об ингредиентах")
    public Response getIngredients() {
        return given()
                .spec(requestSpecification())
                .when()
                .get(PATH);
    }
}
