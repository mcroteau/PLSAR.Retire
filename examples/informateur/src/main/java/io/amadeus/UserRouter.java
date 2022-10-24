package io.amadeus;

import com.google.gson.Gson;
import io.amadeus.model.User;
import io.amadeus.model.response.UserResponse;
import io.amadeus.repo.UserRepo;
import io.kakai.annotate.*;
import io.kakai.annotate.http.Get;

@Router
public class UserRouter {

    Gson gson = new Gson();

    @Bind
    UserRepo userRepo;

    @Json
    @Get("/code/{id}")
    public String getCode(@Variable String id){
        User user = userRepo.getUserCode(id);
        if(user == null){
            return gson.toJson(new UserResponse("terrible", null));
        }
        return gson.toJson(new UserResponse("ok", user));
    }

    @Json
    @Get("/users/{id}")
    public String getUser(@Variable Long id){
        User user = userRepo.get(id);
        if(user == null){
            return gson.toJson(new UserResponse("sucks", null));
        }
        return gson.toJson(new UserResponse("ok", user));
    }

}
