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
import praktikum.IngredientsResponse;
import praktikum.IngredientsRequest;
import praktikum.ClientGenerator;

import java.util.ArrayList;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class CreateOrderAuthorizedTest {

    private OrderClient orderClient;
    private Client client;
    private IngredientsClient ingredientsClient;
    private ClientGenerator clientGenerator;
    private IngredientsRequest ingredientsRequest;
    private String token;
    private Response ingredients;

    @BeforeEach
    public void setUp() {
        orderClient = new OrderClient();
        client = new Client();
        ingredientsClient = new IngredientsClient();

        clientGenerator = ClientGenerator.getRandom();
        ValidatableResponse responseUser = client.createClient(clientGenerator);
        token = responseUser.extract().path("accessToken");

        responseUser.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", is(true));
    }

    @AfterEach
    public void clear() {
        if (token != null) {
            System.out.println("Удаляем пользователя: " + token);
            client.deleteClient(token)
                    .assertThat()
                    .statusCode(SC_ACCEPTED)
                    .and()
                    .body("success", is(true))
                    .and()
                    .body("message", is("User successfully removed"));
        }
    }

    @Test
    @DisplayName("Отправить запрос на создание заказа для авторизованного пользователя")
    public void createOrderWithAuthorization() {
        ingredients = ingredientsClient.getIngredients();
        ingredients.then().assertThat().statusCode(SC_OK);
        Ingredients[] getIngredients = ingredients.body().as(IngredientsResponse.class).getData();
        ArrayList<String> ingredientsList = new ArrayList<>();
        ingredientsList.add(getIngredients[0].get_id());
        ingredientsList.add(getIngredients[1].get_id());
        ingredientsList.add(getIngredients[2].get_id());
        ingredientsRequest = new IngredientsRequest(ingredientsList);
        orderClient.createOrderAuthorized(ingredientsRequest, token)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", is(true))
                .and()
                .body("order", is(notNullValue()));
    }

    @Test
    @DisplayName("Запрос на создание заказа для авторизованного пользователя, но с невалидным хешем ингредиента")
    public void createOrderWithInvalidIngredientHash() {
        ArrayList<String> ingredients = new ArrayList<>();
        ingredients.add("0123457");
        ingredientsRequest = new IngredientsRequest(ingredients);
        orderClient.createOrderAuthorized(ingredientsRequest, token)
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Запрос на создание заказа с пустым списком ингредиентов")
    public void createEmptyOrder() {
        ingredientsRequest = new IngredientsRequest(new ArrayList<>());
        orderClient.createOrderAuthorized(ingredientsRequest, token)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("success", is(false))
                .and()
                .body("message", is("Ingredient ids must be provided"));
    }
}
