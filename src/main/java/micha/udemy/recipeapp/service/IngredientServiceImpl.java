package micha.udemy.recipeapp.service;

import lombok.extern.slf4j.Slf4j;
import micha.udemy.recipeapp.command.IngredientCommand;
import micha.udemy.recipeapp.converter.IngredientCommandToIngredient;
import micha.udemy.recipeapp.converter.IngredientToIngredientCommand;
import micha.udemy.recipeapp.exception.NotFoundException;
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
                .orElseThrow(() -> new NotFoundException("Recipe not found id=" + recipeId));

        return recipe.getIngredients().stream()
                .filter(ingredient -> ingredient.getId().equals(ingredientId))
                .map(ingredientToIngredientCommand::convert)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Ingredient not found id=" + ingredientId));
    }

    @Override
    @Transactional
    public IngredientCommand saveIngredientCommand(IngredientCommand command) {
        Recipe recipe = recipeRepository.findById(command.getRecipeId())
                .orElseThrow(() -> new NotFoundException("Recipe not found id=" + command.getRecipeId()));

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
                    .orElseThrow(() -> new NotFoundException("Unit of measure not found id=" + command.getUom().getId()));
            ingredientFound.setUom(uom);
        } else {
            Ingredient ingredient = ingredientCommandToIngredient.convert(command);
            if (ingredient != null) {
                ingredient.setRecipe(recipe);
                recipe.addIngredient(ingredient);
            }
        }

        Recipe savedRecipe = recipeRepository.save(recipe);

        Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                .filter(recipeIngredients -> recipeIngredients.getId().equals(command.getId()))
                .findFirst();

        //check by description
        if (savedIngredientOptional.isEmpty()) {
            savedIngredientOptional = savedRecipe.getIngredients().stream()
                    .filter(recipeIngredients -> recipeIngredients.getDescription().equals(command.getDescription()))
                    .filter(recipeIngredients -> recipeIngredients.getAmount().equals(command.getAmount()))
                    .filter(recipeIngredients -> recipeIngredients.getUom().getId().equals(command.getUom().getId()))
                    .findFirst();
        }
        return ingredientToIngredientCommand.convert(savedIngredientOptional
                .orElseThrow(() -> new NotFoundException("Recipe ingredient not found id=" + command.getId())));
    }

    @Override
    public void deleteIngredientById(Long recipeId, Long ingredientId) {
        Recipe recipe = recipeRepository.findById(recipeId)
           .orElseThrow(() -> new NotFoundException("Recipe not found id=" + recipeId));

        Optional<Ingredient> ingredientOptional = recipe
            .getIngredients()
            .stream()
            .filter(ingredient -> ingredient.getId().equals(ingredientId))
            .findFirst();
        if (ingredientOptional.isPresent()) {
            Ingredient ingredientToDelete = ingredientOptional.get();
            ingredientToDelete.setRecipe(null);
            recipe.getIngredients().remove(ingredientOptional.get());
            recipeRepository.save(recipe);
        }
    }
}
