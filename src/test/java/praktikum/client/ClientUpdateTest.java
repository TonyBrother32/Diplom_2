package praktikum.client;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import praktikum.Client;
import praktikum.ClientGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ClientUpdateTest {

    private Client client;
    private ClientGenerator clientGenerator;
    private String token;
    private Faker faker;

    @BeforeEach
    public void setUp() {
        client = new Client();
        clientGenerator = ClientGenerator.getRandom();
        faker = new Faker();

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
    @DisplayName("Обновление имени пользователя")
    public void updateNameClient() {
        String name = faker.name().name();
        clientGenerator.setName(name);

        ValidatableResponse response = client.updateClientWithAuthorization(clientGenerator, token);

        response.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("user.name", equalTo(name));
    }

    @Test
    @DisplayName("Обновление email пользователя")
    public void updateEmailClient() {
        String email = faker.internet().emailAddress();
        clientGenerator.setEmail(email);

        ValidatableResponse response = client.updateClientWithAuthorization(clientGenerator, token);

        response.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("user.email", equalTo(email));
    }

    @Test
    @DisplayName("Обновление данных пользователя без авторизации")
    public void updateUserWithoutAuthorization() {
        ValidatableResponse response = client.updateClientWithoutAuthorization(clientGenerator);

        response.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("You should be authorised"));
    }
}

