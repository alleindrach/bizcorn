package allein.bizcorn.model.mongo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class Authority implements Serializable {

    private static final long serialVersionUID = 1L;
    @Getter
    @Setter
    private String authority;


}
