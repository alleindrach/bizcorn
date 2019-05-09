/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-05-09 09:54
 **/
@Component
public class ServiceConfigProp {
    @Setter
    @Getter
    @Value("${bizcorn.bind.timeout}")
    public Long bindTokenTimout;

}
