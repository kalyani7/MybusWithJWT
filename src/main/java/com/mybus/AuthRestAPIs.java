package com.mybus;

import com.mybus.configuration.security.jwt.JwtProvider;
import com.mybus.dao.UserDAO;
import com.mybus.message.request.LoginForm;
import com.mybus.message.request.SignUpForm;
import com.mybus.message.response.JwtResponse;
import com.mybus.model.User;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthRestAPIs {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDAO userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    JwtProvider jwtProvider;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginForm loginRequest) {

        System.out.println("userName......"+loginRequest.getUserName()+".........."+loginRequest.getPassword());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUserName(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt));

    }


    @PostMapping("/signup")
    public ResponseEntity<JSONObject> registerUser(@Valid @RequestBody SignUpForm signUpRequest,
                                                   final HttpServletRequest request ) {
        JSONObject response = new JSONObject();
        // Creating user's account
        User user = new User();
        user.setUserName(signUpRequest.getUserName());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user = userRepository.save(user);
        response.put("message", "User registered");
        return ResponseEntity.ok().body(response);
    }




}
