package micha.udemy.recipeapp.service;

import micha.udemy.recipeapp.command.IngredientCommand;
import micha.udemy.recipeapp.converter.IngredientToIngredientCommand;
import micha.udemy.recipeapp.converter.UnitOfMeasureToUnitOfMeasureCommand;
import micha.udemy.recipeapp.model.Ingredient;
import micha.udemy.recipeapp.model.Recipe;
import micha.udemy.recipeapp.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceImplTest {

    @Mock
    RecipeRepository recipeRepository;

    IngredientToIngredientCommand ingredientToIngredientCommand;

    @InjectMocks
    IngredientService ingredientService;

    @BeforeEach
    public void setUp() {
        ingredientToIngredientCommand = new IngredientToIngredientCommand(new UnitOfMeasureToUnitOfMeasureCommand());
    }


    @Test
    public void getByRecipeIdAndIngredientId() {
        Set<Ingredient> ingredients = Set.of(
            Ingredient.builder().id(1L).build(),
            Ingredient.builder().id(2L).build(),
            Ingredient.builder().id(3L).build()
        );
        Recipe recipe = Recipe.builder()
            .id(1L)
            .ingredients(ingredients)
            .build();
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));

        IngredientCommand ingredientCommand = ingredientService.findByRecipeIdAndIngredientId(1L, 3L);

        assertEquals(3L, ingredientCommand.getId());
        assertEquals(1L, ingredientCommand.getRecipeId());
        verify(recipeRepository, times(1)).findById(anyLong());
    }

}
