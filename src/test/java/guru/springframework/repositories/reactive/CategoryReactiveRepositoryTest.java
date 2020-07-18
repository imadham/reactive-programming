package guru.springframework.repositories.reactive;

import guru.springframework.domain.Category;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class CategoryReactiveRepositoryTest {

    @Autowired
    CategoryReactiveRepository categoryReactiveRepository;
    Category category = new Category();
    @Before
    public void setUp(){

        categoryReactiveRepository.deleteAll().block();

    }

    @Test
    public void findByDescription() {
        save();
        assertEquals(categoryReactiveRepository.findByDescription("tea").block().getDescription(),category.getDescription());
    }

    @Test
    public void save(){
        category.setDescription("tea");
        categoryReactiveRepository.save(category).block();
        assertEquals(categoryReactiveRepository.count().block(),Long.valueOf(1L));
    }
}