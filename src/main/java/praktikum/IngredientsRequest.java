package praktikum;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class IngredientsRequest {

    private List<String> ingredients;
}