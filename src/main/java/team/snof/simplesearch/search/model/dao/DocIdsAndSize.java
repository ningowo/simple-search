package team.snof.simplesearch.search.model.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocIdsAndSize {

    List<String> sortedDocIds;

    Integer total;
}
