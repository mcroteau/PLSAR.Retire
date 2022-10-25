package io.informant;

import com.google.gson.Gson;
import io.informant.model.User;
import io.informant.model.response.UserResponse;
import io.informant.repo.UserRepo;
import io.kakai.annotate.*;
import io.kakai.annotate.http.Get;

@Router
public class UserController {

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
