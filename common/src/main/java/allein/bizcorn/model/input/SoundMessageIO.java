/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.input;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-05-07 17:48
 **/
public class SoundMessageIO implements Serializable{
    @Getter @Setter
    private String id;
    @Getter @Setter
    private Integer channel;
    @Getter @Setter
    private String snd;
    @Getter @Setter
    private Boolean sync=true;

    @Getter @Setter
    private Boolean echo=false;
}
