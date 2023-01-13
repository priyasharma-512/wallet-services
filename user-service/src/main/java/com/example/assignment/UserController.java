package com.example.assignment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/add_user")
    public ResponseEntity<String> addUser(@RequestBody UserRequest userRequest)
    {
        userService.addUser(userRequest);
        return new ResponseEntity<>("User added Successfully", HttpStatus.CREATED);
    }


    @GetMapping("/get_user")
    public User getUserByUserName(@RequestParam("userName") String userName) throws Exception
    {
        return userService.getUserByUserName(userName);
    }
}
