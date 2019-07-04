/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.mongo;
//审核状态，NONE=私有的，不公开的，不许审核，PENDING 等待审核后发布，APPROVED 已过审，发布中，REJECTED 已决绝，WITHDRAWED 已撤销发布

public enum AuditStatus {
   NONE,PENDING,APPROVED,REJECTED,WITHDRAWED,COMPLAINT
}
