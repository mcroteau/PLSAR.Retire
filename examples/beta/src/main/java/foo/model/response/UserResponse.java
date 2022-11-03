package foo.model.response;

import foo.model.User;

public class UserResponse {
    String status;
    User user;

    public UserResponse(String status, User user){
        this.status = status;
        this.user = user;
    }
}
