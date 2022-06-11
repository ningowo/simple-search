package team.snof.simplesearch.search.model.dao.index;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Index implements Serializable {

    // 分词
    public String indexKey;

    public List<DocInfo> docInfoList;

}
