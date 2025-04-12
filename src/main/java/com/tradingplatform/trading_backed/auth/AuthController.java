package com.tradingplatform.trading_backed.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        try{
            userService.register(request);
            return "User registered successfully!";
        }
        catch(RuntimeException e){    
            return e.getMessage();
        }
        
    }

    @GetMapping("/getAllUser")
    public void getAllUser(){
        try{
            userService.getAllUser();
        }
        catch(RuntimeException e){
            System.out.println(e.getMessage());
        }
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/deleteUser/{userName}")
    public void removeUser(@PathVariable("userName") String userName) {
        userService.removeUser(userName);
    }
}

