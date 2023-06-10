package micha.udemy.recipeapp.service;

import micha.udemy.recipeapp.command.IngredientCommand;

public interface IngredientService {

    IngredientCommand findByRecipeIdAndIngredientId(Long recipeId, Long ingredientId);

}
