/*
 * Copyright (c) 2019.
 * Alleindrach@gmail.com
 */

package allein.bizcorn.model.security;

public enum Authority {
    ROLE_ADMIN("admin"),ROLE_USER("user"),ROLE_DBA("dba"),ROLE_DBO("dbo");
    private String role;
    private Authority(String role){
        this.role=role;
    }
    public String getValue(){
        return this.role;
    }
    public void setValue(String role){
        this.role=role;
    }
}
