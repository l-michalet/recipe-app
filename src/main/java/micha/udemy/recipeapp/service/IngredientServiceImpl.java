package micha.udemy.recipeapp.service;

import lombok.extern.slf4j.Slf4j;
import micha.udemy.recipeapp.command.IngredientCommand;
import micha.udemy.recipeapp.converter.IngredientCommandToIngredient;
import micha.udemy.recipeapp.converter.IngredientToIngredientCommand;
import micha.udemy.recipeapp.model.Ingredient;
import micha.udemy.recipeapp.model.Recipe;
import micha.udemy.recipeapp.model.UnitOfMeasure;
import micha.udemy.recipeapp.repository.RecipeRepository;
import micha.udemy.recipeapp.repository.UnitOfMeasureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final RecipeRepository recipeRepository;
    private final UnitOfMeasureRepository unitOfMeasureRepository;

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand, IngredientCommandToIngredient ingredientCommandToIngredient, RecipeRepository recipeRepository, UnitOfMeasureRepository unitOfMeasureRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.recipeRepository = recipeRepository;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
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

    @Override
    @Transactional
    public IngredientCommand saveIngredientCommand(IngredientCommand command) {
        Recipe recipe = recipeRepository.findById(command.getRecipeId())
           .orElseThrow(() -> new RuntimeException("Recipe not found for id: " + command.getRecipeId()));

        Optional<Ingredient> ingredientOptional = recipe
            .getIngredients()
            .stream()
            .filter(ingredient -> ingredient.getId().equals(command.getId()))
            .findFirst();
        if (ingredientOptional.isPresent()) {
            Ingredient ingredientFound = ingredientOptional.get();
            ingredientFound.setDescription(command.getDescription());
            ingredientFound.setAmount(command.getAmount());
            UnitOfMeasure uom = unitOfMeasureRepository.findById(command.getUom().getId())
               .orElseThrow(() -> new RuntimeException("UOM NOT FOUND"));
            ingredientFound.setUom(uom);
        } else {
            recipe.addIngredient(ingredientCommandToIngredient.convert(command));
        }

        Recipe savedRecipe = recipeRepository.save(recipe);

        return ingredientToIngredientCommand.convert(savedRecipe.getIngredients().stream()
                .filter(recipeIngredients -> recipeIngredients.getId().equals(command.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Recipe ingredient not found " + command.getId())));
    }
}
