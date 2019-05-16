package allein.bizcorn.model.util;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class HttpClientCallResult implements Serializable {
    @Getter
    @Setter
    private String retString;
    @Getter
    @Setter
    private int httpStatus;
}
