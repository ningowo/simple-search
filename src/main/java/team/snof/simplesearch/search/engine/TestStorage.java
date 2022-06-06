package team.snof.simplesearch.search.engine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestStorage {

    @Autowired
    TestComp testComp;

    public int test(){
        System.out.println("teststorage==============");

        testComp.test();

        System.out.println("end teststorage==============");
        return 1;
    }
}
