package guru.springframework.repositories.reactive;

import guru.springframework.domain.UnitOfMeasure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureReactiveRepositoryTest {


    @Autowired
    UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;
    UnitOfMeasure unitOfMeasure = new UnitOfMeasure();

    @Before
    public void setUp() throws Exception {
        unitOfMeasureReactiveRepository.deleteAll().block();
    }

    @Test
    public void save(){
        unitOfMeasure.setDescription("spoon");
        unitOfMeasureReactiveRepository.save(unitOfMeasure).block();
        assertEquals(unitOfMeasureReactiveRepository.count().block(),Long.valueOf(1L));
    }

    @Test
    public void findById(){
        save();
        assertEquals(unitOfMeasure.getId(),unitOfMeasureReactiveRepository.findById(unitOfMeasure.getId()).block().getId());
    }
}