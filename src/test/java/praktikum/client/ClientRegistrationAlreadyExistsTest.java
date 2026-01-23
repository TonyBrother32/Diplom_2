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

public class ClientRegistrationAlreadyExistsTest {

    private Client client;
    private ClientGenerator clientGenerator;
    private String secondUserToken;
    private String firstUserToken;

    @BeforeEach
    public void setUp() {
        client = new Client();
        clientGenerator = ClientGenerator.getRandom();

        ValidatableResponse responseUser = client.createClient(clientGenerator);
        secondUserToken = responseUser.extract().path("accessToken");

        responseUser.assertThat()
                .statusCode(SC_OK);
    }

    @AfterEach
    public void clear() {
        if (firstUserToken != null) {
            System.out.println("Удаляем пользователя: " + firstUserToken);
            client.deleteClient(firstUserToken)
                    .assertThat()
                    .statusCode(SC_ACCEPTED)
                    .and()
                    .body("success", is(true))
                    .and()
                    .body("message", is("User successfully removed"));
        }

        if (secondUserToken != null) {
            System.out.println("Удаляем пользователя: " + secondUserToken);
            client.deleteClient(secondUserToken)
                    .assertThat()
                    .statusCode(SC_ACCEPTED).and()
                    .body("success", is(true))
                    .and()
                    .body("message", is("User successfully removed"));
        }
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    public void createClientAlreadyExists() {
        ValidatableResponse responseUser = client.createClient(clientGenerator);
        int statusCode = responseUser.extract().statusCode();

        if (statusCode == SC_OK) {
            firstUserToken = responseUser.extract().path("accessToken");
        }

        responseUser.assertThat()
                .statusCode(SC_FORBIDDEN)
                .and()
                .body("message", equalTo("User already exists"))
                .extract()
                .path("success", String.valueOf(is(false)));
    }
}
