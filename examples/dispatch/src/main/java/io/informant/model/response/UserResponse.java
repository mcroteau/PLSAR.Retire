package io.informant.model.response;

import io.informant.model.User;

public class UserResponse {
    String status;
    User user;

    public UserResponse(String status, User user){
        this.status = status;
        this.user = user;
    }
}
