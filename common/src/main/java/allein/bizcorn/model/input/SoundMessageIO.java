/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.input;

import java.io.Serializable;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-05-07 17:48
 **/
public class SoundMessageIO implements Serializable{
    private String id;
    private Integer channel;
    private String snd;

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public String getSnd() {
        return snd;
    }

    public void setSnd(String snd) {
        this.snd = snd;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
