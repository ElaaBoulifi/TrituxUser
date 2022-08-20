package com.example.trituxuser.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.trituxuser.entity.RegistrationForm;
import com.example.trituxuser.entity.Role;
import com.example.trituxuser.entity.User;
import com.example.trituxuser.mail.EmailSender;
import com.example.trituxuser.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final EmailSender emailSender;
    private final UserService userService;

    @GetMapping("/getusers")
    public List<User> getUsers(){
        return userService.getUsers();
    }
    @PostMapping("/saveuser")
    public void saveuser(@RequestBody User u){
        userService.saveUser(u);
    }
    @PostMapping("/saverole")
    public void saverole(@RequestBody Role r){
        userService.saveRole(r);
    }
    @PostMapping("/affecterroletouser")
    public void affecterroletouser(@RequestBody RoleToUser roleToUser)
    {
        userService.affecterRoleaUser(roleToUser.getUsername(),roleToUser.getRolename());
    }
    @DeleteMapping("/deleteuser/{id}")
    public void deleteuser(@PathVariable int id)
    {
        userService.deleteUser(id);
    }
    @GetMapping("/refresh_token")
    public void refreshtoken(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        String authorizationHeader=request.getHeader(AUTHORIZATION);
        if(authorizationHeader!= null && authorizationHeader.startsWith("Bearer "))
        {
            try{
                String token =authorizationHeader.substring("Bearer ".length());
                Algorithm algorithm = Algorithm.HMAC256("secretsecret");
                JWTVerifier verfier= JWT.require(algorithm).build();
                DecodedJWT decodedJWT=verfier.verify(token);
                String username=decodedJWT.getSubject();
                User user=userService.getUser(username);
                String access_token = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis()+150*60*1000))
                        .withClaim("roles",user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .withIssuer("Tritux")
                        .sign(algorithm);
                Map<String,String> tokens=new HashMap<>();
                tokens.put("access_token",access_token);
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),tokens);


            }
            catch(Exception exception){
                response.setHeader("error",exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String,String> error=new HashMap<>();
                error.put("error", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(),error);

            }

        }
    }
    @PostMapping("/forgot_password/{username}")
    public void forgotPassword(@PathVariable String username){
        User user=userService.getUser(username);
        String token= RandomString.make(30);
        userService.updateResetPasswordToken(token,user.getEmail());
        String resetPasswordLink="http://localhost:8081/user/reset_password?token="+token;
        try {
            emailSender.send(user.getEmail(),resetPasswordLink);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

    }
    @PutMapping("/reset_password")
    public String resetPassword(@RequestParam String token,@RequestParam String newPassword){
        return userService.updatePassword(token,newPassword);
    }
    @PostMapping("/signup")
    public void signup(@RequestBody RegistrationForm registrationForm){
        userService.signup(registrationForm);
    }
}
@Getter
@Setter
class RoleToUser{
    private String username;
    private String rolename;
}
