package micha.udemy.recipeapp.service;
import micha.udemy.recipeapp.command.RecipeCommand;
import micha.udemy.recipeapp.model.Recipe;

import java.util.Set;

public interface RecipeService {

    Recipe getRecipeById(Long id);

    Set<Recipe> getRecipes();

    RecipeCommand getRecipeCommandById(Long id);

    RecipeCommand saveRecipeCommand(RecipeCommand command);
}
