package io.amadeus.model.response;

import io.amadeus.model.User;

public class UserResponse {
    String status;
    User user;

    public UserResponse(String status, User user){
        this.status = status;
        this.user = user;
    }
}
