package team.snof.simplesearch.search.storage;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.snof.simplesearch.search.model.dao.index.Index;
import team.snof.simplesearch.search.model.dao.index.IndexPartial;
import team.snof.simplesearch.search.model.dao.index.TempData;

import java.util.ArrayList;

@SpringBootTest
@Slf4j
public class IndexPartialTest {
    @Autowired
    IndexPartialStorage indexPartialStorage;

    @Test
    public void saveTest() {
        ArrayList<TempData> tempData = new ArrayList<>();
        tempData.add(new TempData(1L, 1));
        tempData.add(new TempData(2L, 2));
        tempData.add(new TempData(3L, 3));
        IndexPartial indexPartial = new IndexPartial("test1", tempData);
        indexPartialStorage.saveIndexPartial(indexPartial);
    }
}
