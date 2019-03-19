package allein.model.user;

public class Result<T> {
    int state =0;
    T data;
    String message=null;

    public Result(int state, String message, T data)
    {
        this.state=state;
        this.message=message;
        this.data=data;

    }
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
