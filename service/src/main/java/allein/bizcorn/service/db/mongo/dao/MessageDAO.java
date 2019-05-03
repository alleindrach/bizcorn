/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.service.db.mongo.dao;

import allein.bizcorn.model.mongo.Message;

import java.util.List;


public interface MessageDAO extends  BaseDAO<Message> {
/*
@Description:读取未读的消息
@Param: page:页号，从0开始，limit：每页记录数
@Return:
@Author:Alleindrach@gmail.com
@Date:2019/5/2
@Time:9:03 AM
*/
    List<Message> selectUncopied(String uid,int page, int limit);

    long getUncopiedCount(String uid);

}
