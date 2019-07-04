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
import java.util.Date;

/**
 * @program: bizcorn
 * @description:
 * @author: Alleindrach@gmail.com
 * @create: 2019-07-04 09:24
 **/
@Document(collection="Complaint")
public class Complaint implements Serializable {
    @Id
    @Getter
    @Setter
    private String id;

    @Getter
    @Setter
    protected String content;
    @Getter
    @Setter
    protected Date createDate;
    @Getter
    @Setter
    protected String complaintorID;

    @Getter
    @Setter
    protected String auditorID;

    @Getter
    @Setter
    protected Date auditDate;

    @Getter
    @Setter
    private String auditComment;

    @Getter
    @Setter
    private Boolean approved=false;
}
