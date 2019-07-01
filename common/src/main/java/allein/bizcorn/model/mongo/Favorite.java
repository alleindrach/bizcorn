/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-06-28 12:00
 **/
@Document(collection="Favorite")
public class Favorite implements Serializable{
    @Getter
    @Setter
    private Date favoriteDate;//喜欢的时间
    @Getter
    @Setter
    private String favoritor;//喜欢的人id
    @Getter
    @Setter
    private String favoritee;//喜欢的礼物id
}