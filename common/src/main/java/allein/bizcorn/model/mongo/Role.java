package allein.bizcorn.model.mongo;

public enum Role {
    ADULT(0),KID(1);
    private int role;
    private Role(int role){
        this.role=role;
    }
    public int getValue(){
        return this.role;
    }
    public void setValue(int val){
        this.role=val;
    }
}
