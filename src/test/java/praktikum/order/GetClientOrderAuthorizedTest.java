package praktikum.order;

import org.junit.jupiter.api.DisplayName;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import praktikum.Client;
import praktikum.IngredientsClient;
import praktikum.OrderClient;
import praktikum.Ingredients;
import praktikum.IngredientsRequest;
import praktikum.IngredientsResponse;
import praktikum.ClientGenerator;

import java.util.ArrayList;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.is;

public class GetClientOrderAuthorizedTest {

    private OrderClient orderClient;
    private Client client;
    private IngredientsClient ingredientsClient;
    private ClientGenerator clientGenerator;
    private String token;
    private IngredientsRequest ingredientsRequest;

    @BeforeEach
    public void setUp() {
        orderClient = new OrderClient();
        client = new Client();
        ingredientsClient = new IngredientsClient();
        clientGenerator = ClientGenerator.getRandom();

        // Создание пользователя
        ValidatableResponse responseUser = client.createClient(clientGenerator);
        token = responseUser.extract().path("accessToken");

        responseUser.assertThat()
                .statusCode(SC_OK)
                .extract()
                .path("accessToken");

        // Получение списка ингредиентов
        Response ingredients = ingredientsClient.getIngredients();
        ingredients.then().assertThat().statusCode(SC_OK);

        Ingredients[] getIngredients = ingredients.body().as(IngredientsResponse.class).getData();

        // Проверка наличия минимум 2 ингредиентов
        if (getIngredients.length < 2) {
            throw new RuntimeException(
                    "Для теста требуется минимум 2 ингредиента, доступно: " + getIngredients.length);
        }

        ArrayList<String> ingredientsList = new ArrayList<>();
        ingredientsList.add(getIngredients[0].get_id());
        ingredientsList.add(getIngredients[1].get_id());

        ingredientsRequest = new IngredientsRequest(ingredientsList);

        // Создание заказа
        ValidatableResponse createOrderResponse = orderClient.createOrderAuthorized(ingredientsRequest, token);
        createOrderResponse.assertThat()
                .statusCode(SC_OK)
                .body("success", is(true));
    }

    @AfterEach
    public void clear() {
        if (token != null) {
            ValidatableResponse deleteResponse = client.deleteClient(token);
            deleteResponse.assertThat()
                    .statusCode(SC_ACCEPTED)
                    .body("success", is(true))
                    .body("message", is("User successfully removed"));
        }
    }

    @Test
    @DisplayName("Получить список заказов для авторизованного пользователя")
    public void getUserOrderWithAuthorization() {
        orderClient.getOrderAuthorized(token).assertThat()
                .statusCode(SC_OK)
                .body("success", is(true));
    }
}
