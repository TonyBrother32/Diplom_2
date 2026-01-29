package praktikum.order;

import org.junit.jupiter.api.DisplayName;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import praktikum.IngredientsClient;
import praktikum.OrderClient;
import praktikum.Ingredients;
import praktikum.IngredientsRequest;
import praktikum.IngredientsResponse;
import java.util.ArrayList;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.is;

public class CreateOrderNotAuthorizedTest {

    private OrderClient orderClient;
    private IngredientsClient ingredientsClient;
    private IngredientsRequest ingredientsRequest;
    private Response ingredients;

    @BeforeEach
    public void setUp() {
        orderClient = new OrderClient();
        ingredientsClient = new IngredientsClient();

        ingredients = ingredientsClient.getIngredients();
        ingredients.then().assertThat().statusCode(SC_OK);

        Ingredients[] getIngredients = ingredients.body().as(IngredientsResponse.class).getData();

        // Проверка наличия достаточного количества ингредиентов
        if (getIngredients.length < 2) {
            throw new RuntimeException(
                    "Для теста требуется минимум 2 ингредиента, доступно: " + getIngredients.length
            );
        }

        ArrayList<String> ingredientsList = new ArrayList<>();
        ingredientsList.add(getIngredients[0].get_id());
        ingredientsList.add(getIngredients[1].get_id());

        ingredientsRequest = new IngredientsRequest(ingredientsList);
    }

    @Test
    @DisplayName("Запрос на создание заказа без авторизации")
    public void createOrderWithIngredientWithoutAuthorization() {
        orderClient.createOrderUnauthorized(ingredientsRequest)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", is(true));
    }
}
