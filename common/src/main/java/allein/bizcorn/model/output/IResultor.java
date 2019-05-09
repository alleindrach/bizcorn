/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.output;

import allein.bizcorn.common.exception.ErrorCodeException;
import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public interface IResultor extends  Serializable {
   public JSONObject toResultJson();
}
