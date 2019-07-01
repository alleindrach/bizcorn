/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * @program: bizcorn
 * @description:礼物包装
 * @author: Alleindrach@gmail.com
 * @create: 2019-06-28 11:26
 **/
@Document(collection="PackageBox")
public class PackageBox implements Serializable {
    @Id
    @Getter
    @Setter
    private String id;
    @Setter
    @Getter
    private String img;
    @Setter
    @Getter
    private String snd;
    @Setter
    @Getter
    private String order;
    @Setter
    @Getter
    private String cat;
    @Getter
    @Setter
    protected List<String> tags;

    @Setter
    @Getter
    private Boolean deleted=false;

}