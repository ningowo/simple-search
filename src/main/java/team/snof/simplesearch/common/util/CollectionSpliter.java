package team.snof.simplesearch.common.util;

import java.util.ArrayList;
import java.util.List;

public class CollectionSpliter<T> {

    public List<List<T>> splitList(List<T> list, int groupSize) {
        int length = list.size();
        int groupNum = length % groupSize == 0 ? length / groupSize : length / groupSize + 1;

        List<List<T>> newList = new ArrayList<>(groupNum);
        for (int i = 0; i < groupNum; i++) {
            // start和end左开右闭
            int start = i * groupSize;
            int end = Math.min((i + 1) * groupSize, length);
            newList.add(list.subList(start, end));
        }
        return newList;
    }
}
