package com.LDLS.Auth.services;

import com.LDLS.Auth.dtos.ReqRes;
import com.LDLS.Auth.models.Role;
import com.LDLS.Auth.models.Users;
import com.LDLS.Auth.repositories.UserRepository;
import com.LDLS.Auth.utils.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JWTUtils jwtUtils;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    public ReqRes signUp(ReqRes registrationRequest){
        ReqRes resp = new ReqRes();
        try {
            Users users = new Users();
            users.setFirstName(registrationRequest.getFirstName());
            users.setLastName(registrationRequest.getLastName());
            users.setPhoneNumber(registrationRequest.getPhoneNumber());
            users.setEmail(registrationRequest.getEmail());
            users.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            users.setRole(Role.valueOf(registrationRequest.getRole())); // Assuming Role is an enum
            Users savedUser = userRepository.save(users);
            if (savedUser.getId() > 0) {
                resp.setUsers(savedUser);
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }
        } catch (Exception e){
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes signIn(ReqRes signinRequest){
        ReqRes response = new ReqRes();

        try {
            // Authenticate the user
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(), signinRequest.getPassword()));

            // Find the user by email
            Users user = userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(() -> new Exception("User not found"));

            // Generate JWT token
            String jwt = jwtUtils.generateToken(user);

            // Generate refresh token
            String refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            // Set the response
            response.setStatusCode(200);
            response.setJwt(jwt);
            response.setType("Bearer");
            response.setBearer("Bearer " + jwt);
            response.setRole(user.getRole().toString());
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Signed In");

            // Include header information
            Map<String, String> header = new HashMap<>();
            header.put("Content-Type", "application/json");
            header.put("Authorization", "Bearer " + jwt);
            response.setHeader(header);
        } catch (Exception e){
            response.setStatusCode(500);
            response.setError(e.getMessage());
        }
        return response;
    }


    public ReqRes refreshToken(ReqRes refreshTokenReqiest){
        ReqRes response = new ReqRes();
        String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getJwt()); // Corrected to use getJwt
        Users users = userRepository.findByEmail(ourEmail).orElseThrow();
        if (jwtUtils.isTokenValid(refreshTokenReqiest.getJwt(), users)) { // Corrected to use getJwt
            var jwt = jwtUtils.generateToken(users);
            response.setStatusCode(200);
            response.setJwt(jwt); // Corrected to use setJwt
            response.setType("Bearer"); // Set the token type
            response.setBearer("Bearer " + jwt); // Set the bearer token
            response.setRole(users.getRole().toString()); // Set the user role
            response.setExpirationTime("24Hr");
            response.setMessage("Successfully Refreshed Token");
        }
        response.setStatusCode(500);
        return response;
    }
}
