package praktikum.client;

import org.junit.jupiter.api.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import praktikum.Client;
import praktikum.ClientGenerator;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.is;

public class ClientRegistrationTest {

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
                    .and()
                    .body("success", is(true))
                    .and()
                    .body("message", is("User successfully removed"));
        }
    }

    @Test
    @DisplayName("Создание пользователя")
    public void registrationUser() {
        clientGenerator = ClientGenerator.getRandom();

        ValidatableResponse responseUser = client.createClient(clientGenerator);
        token = responseUser.extract().path("accessToken");

        responseUser.assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", is(true));
    }
}
