package com.mashupstack.recipe_sharing.controller;


import com.mashupstack.recipe_sharing.response.LoginResponse;
import com.mashupstack.recipe_sharing.dto.LoginDTO;
import com.mashupstack.recipe_sharing.dto.UserDTO;
import com.mashupstack.recipe_sharing.models.User;
import com.mashupstack.recipe_sharing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    @PostMapping("/register")
    public User registerUser(@RequestBody UserDTO userDTO) {

        User user = new User(userDTO.getFullname(), userDTO.getUsername() , passwordEncoder.encode(userDTO.getPassword()));

        User user1 =  userRepository.save(user);
        //return ResponseEntity.ok(user1).getBody();

        return user1;
    }

    @GetMapping("/list")
    public List<User> allUsers(){
        return userRepository.findAll();
    }

    /*@GetMapping("/login")
    public String login() {
        return "login";
    }*/
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO){
        String msg = " ";
        LoginResponse loginResponse;

        User user = userRepository.findByUsername(loginDTO.getUsername());

        System.out.println(loginDTO.getUsername());

        if (user != null){
            String userPassword = loginDTO.getPassword();
            String encodedPassword = user.getPassword();

            boolean isCorrectPassword =  passwordEncoder.matches(userPassword, encodedPassword);

            if(isCorrectPassword){
                Optional<User> loginUser = userRepository.findOneByUsernameAndPassword(loginDTO.getUsername(), encodedPassword);

                if (loginUser.isPresent()){
                     loginResponse =  new LoginResponse(true, "Login Success");
                } else {
                     loginResponse =    new LoginResponse(false, "Login Failed");
                }
            } else {
                 loginResponse =    new LoginResponse(false, "Incorrect Password");
            }
        } else {
             loginResponse =    new LoginResponse(false, "User does not Exist");
        }

        return ResponseEntity.ok(loginResponse);

    }

    @PostMapping("/update")
    @CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
    public String updateProduct(/*@PathVariable Long id,*/ @RequestBody UserDTO userDTO) {
        //Optional<User> optionalUserDetails = userRepository.findById(id);
        Optional<User> optionalUserDetails = Optional.ofNullable(userRepository.findByUsername(userDTO.getUsername()));
        if(optionalUserDetails.isPresent()){
            User userDetails = optionalUserDetails.get();
            userDetails.setFullname(userDTO.getFullname());
            userDetails.setUsername(userDTO.getUsername());
            userDetails.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userRepository.save(userDetails);
        }

        return "redirect:/all";
    }

    @GetMapping("account/{id}")
    public User loadUser(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username);
    }

}
