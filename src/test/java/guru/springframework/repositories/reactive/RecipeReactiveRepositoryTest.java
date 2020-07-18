package guru.springframework.repositories.reactive;

import guru.springframework.domain.Category;
import guru.springframework.domain.Recipe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@DataMongoTest
public class RecipeReactiveRepositoryTest {


    @Autowired
    RecipeReactiveRepository recipeReactiveRepository;
    Recipe recipe = new Recipe();

    @Before
    public void setUp() throws Exception {
        recipeReactiveRepository.deleteAll().block();
    }

    @Test
    public void findById() {
        save();
        assertEquals(recipeReactiveRepository.findById(recipe.getId()).block().getId(),recipe.getId());
    }

    @Test
    public void save(){
        recipe.setDescription("tea");
        recipeReactiveRepository.save(recipe).block();
        assertEquals(recipeReactiveRepository.count().block(),Long.valueOf(1L));
    }
}