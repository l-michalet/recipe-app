package converters;

import micha.udemy.recipeapp.command.CategoryCommand;
import micha.udemy.recipeapp.converter.CategoryToCategoryCommand;
import micha.udemy.recipeapp.model.Category;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CategoryToCategoryCommandTest {

    public static final Long ID_VALUE = 1L;
    public static final String DESCRIPTION = "description";

    @InjectMocks
    CategoryToCategoryCommand converter;

    @Test
    public void testNullObject() {
        assertNull(converter.convert(null));
    }

    @Test
    public void testEmptyObject() {
        assertNotNull(converter.convert(new Category()));
    }

    @Test
    public void convert() {
        //given
        Category category = new Category();
        category.setId(ID_VALUE);
        category.setDescription(DESCRIPTION);

        //when
        CategoryCommand categoryCommand = converter.convert(category);

        //then
        assertEquals(ID_VALUE, categoryCommand.getId());
        assertEquals(DESCRIPTION, categoryCommand.getDescription());
    }

}
