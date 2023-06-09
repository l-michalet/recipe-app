package micha.udemy.recipeapp.repository;

import micha.udemy.recipeapp.model.UnitOfMeasure;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DataJpaTest
class UnitOfMeasureRepositoryIntegrationTest {

    UnitOfMeasureRepository unitOfMeasureRepository;

    // The context is loaded and the uoms are built and saved from the data.sql file

    @Test
    public void findByDescription_teaspoon() {
        UnitOfMeasure uom = unitOfMeasureRepository.findByDescription("Teaspoon").orElseThrow();
        assertEquals("Teaspoon", uom.getDescription());
    }

    @Test
    public void findByDescription_cup() {
        UnitOfMeasure uom = unitOfMeasureRepository.findByDescription("Cup").orElseThrow();
        assertEquals("Cup", uom.getDescription());
    }
}