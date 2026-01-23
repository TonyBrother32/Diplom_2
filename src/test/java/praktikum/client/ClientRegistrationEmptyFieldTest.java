package praktikum.client;

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


public class ClientRegistrationEmptyFieldTest {

    private Client client;
    private ClientGenerator clientGenerator;
    private String token;

    @BeforeEach
    public void setUp() {
        client = new Client();
    }

    @AfterEach
    public void clear() {
        if (token != null) {
            System.out.println("Удаляем пользователя: " + token);
            client.deleteClient(token)
                    .assertThat()
                    .statusCode(SC_ACCEPTED)
                    .log().all()
                    .and()
                    .body("success", is(true))
                    .log().all()
                    .and()
                    .body("message", is("User successfully removed"));
        }
    }

    @Test
    @DisplayName("Создание пользователя без email")
    public void createUserEmptyEmail() {
        clientGenerator = new ClientGenerator("", "password", "newClient123");

        ValidatableResponse responseUser = client.createClient(clientGenerator);
        token = responseUser.extract().path("accessToken");

        responseUser.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без password")
    public void createUserEmptyPassword() {
        clientGenerator = new ClientGenerator("Client123@gmail.com", "", "newClient123");

        ValidatableResponse responseUser = client.createClient(clientGenerator);
        token = responseUser.extract().path("accessToken");

        responseUser.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    public void createUserEmptyName() {
        clientGenerator = new ClientGenerator("Client123@gmail.com", "password", "");

        ValidatableResponse responseUser = client.createClient(clientGenerator);
        token = responseUser.extract().path("accessToken");

        responseUser.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("Email, password and name are required fields"));
    }
}