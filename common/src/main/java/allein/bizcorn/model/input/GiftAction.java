/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.input;

import allein.bizcorn.model.mongo.GiftActionType;
import lombok.Getter;
import lombok.Setter;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-06-28 17:04
 **/
public class GiftAction {
    @Getter
    @Setter
    private GiftActionType move;
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private String payload;
}
