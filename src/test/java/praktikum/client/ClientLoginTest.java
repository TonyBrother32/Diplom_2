package praktikum.client;

import org.junit.jupiter.api.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import praktikum.Client;
import praktikum.ClientGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class ClientLoginTest {

    private Client client;
    private ClientGenerator clientGenerator;
    private String token;

    @BeforeEach
    public void setUp() {
        client = new Client();
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
    @DisplayName("Вход зарегистрированного пользователя")
    public void loginUser() {
        ValidatableResponse response = client.loginClient(clientGenerator);

        response.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", is(true));
    }

    @Test
    @DisplayName("Вход с невалидным паролем")
    public void loginUserPasswordInvalid() {
        clientGenerator.setPassword("invalidpassword123");
        ValidatableResponse response = client.loginClient(clientGenerator);

        response.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .body("success", is(false));
    }

    @Test
    @DisplayName("Вход с невалидным Email")
    public void loginUserEmailInvalid() {
        clientGenerator.setEmail("invalid@gmail.ru");
        ValidatableResponse response = client.loginClient(clientGenerator);

        response.assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", equalTo("email or password are incorrect"))
                .and()
                .body("success", is(false));
    }
}
