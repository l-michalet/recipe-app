package micha.udemy.recipeapp.service;

import micha.udemy.recipeapp.command.RecipeCommand;
import micha.udemy.recipeapp.converter.RecipeCommandToRecipe;
import micha.udemy.recipeapp.converter.RecipeToRecipeCommand;
import micha.udemy.recipeapp.model.Recipe;
import micha.udemy.recipeapp.repository.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@ExtendWith(MockitoExtension.class)
class RecipeServiceImplTest {

    @Mock
    RecipeRepository recipeRepository;

    @Mock
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Mock
    RecipeCommandToRecipe recipeCommandToRecipe;

    @InjectMocks
    RecipeServiceImpl recipeService;

    @Test
    public void getRecipeById()  {
        Recipe recipe = Recipe.builder().id(1L).build();
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        Recipe recipeReturned = recipeService.getRecipeById(1L);

        assertEquals(recipe, recipeReturned);
        verify(recipeRepository, never()).findAll();
    }

    @Test
    public void getRecipeById_notFound()  {
        when(recipeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> recipeService.getRecipeById(1L));;
    }

    @Test
    void getRecipes() {
        when(recipeRepository.findAll()).thenReturn(Set.of(new Recipe()));

        Set<Recipe> recipes = recipeService.getRecipes();

        assertEquals(recipes.size(), 1);
        verify(recipeRepository, times(1)).findAll();
    }

    @Test
    public void getRecipeCommandByIdTest() {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId(1L);

        when(recipeToRecipeCommand.convert(recipe)).thenReturn(recipeCommand);

        RecipeCommand commandById = recipeService.getRecipeCommandById(1L);

        assertNotNull("Null recipe returned", commandById);
        verify(recipeRepository, times(1)).findById(anyLong());
        verify(recipeRepository, never()).findAll();
    }
}