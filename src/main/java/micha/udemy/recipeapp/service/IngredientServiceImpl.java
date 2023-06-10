package micha.udemy.recipeapp.service;

import lombok.extern.slf4j.Slf4j;
import micha.udemy.recipeapp.command.IngredientCommand;
import micha.udemy.recipeapp.converter.IngredientToIngredientCommand;
import micha.udemy.recipeapp.model.Recipe;
import micha.udemy.recipeapp.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final RecipeRepository recipeRepository;

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand, RecipeRepository recipeRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public IngredientCommand findByRecipeIdAndIngredientId(Long recipeId, Long ingredientId) {
        Recipe recipe = recipeRepository.findById(recipeId)
            .orElseThrow(() -> new RuntimeException("Recipe not found"));

        return recipe.getIngredients().stream()
            .filter(ingredient -> ingredient.getId().equals(ingredientId))
            .map(ingredientToIngredientCommand::convert)
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Ingredient not found " + ingredientId));
    }
}
