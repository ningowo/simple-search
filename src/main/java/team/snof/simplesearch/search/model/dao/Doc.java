package team.snof.simplesearch.search.model.dao;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Doc implements Serializable {

    @Id
    public String id;

    public String url;

    public String caption;

}
