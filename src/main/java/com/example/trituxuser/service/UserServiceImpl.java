package com.example.trituxuser.service;

import com.example.trituxuser.entity.RegistrationForm;
import com.example.trituxuser.entity.Role;
import com.example.trituxuser.entity.User;
import com.example.trituxuser.repository.RoleRepo;
import com.example.trituxuser.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
@Service
@RequiredArgsConstructor

public class UserServiceImpl implements UserService , UserDetailsService{
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepo roleRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user=userRepo.findByUsername(username);
        if(user==null){
            System.out.println("User not found");
        }
        else{
            System.out.println("User found in db");
        }
        Collection<SimpleGrantedAuthority> authorities=new ArrayList<>();
        /*
        for(Role r:user.getRoles())
        {
            authorities.add(new SimpleGrantedAuthority(r.getName()));
        }*/
        user.getRoles().forEach(r->{
            authorities.add(new SimpleGrantedAuthority(r.getName()));
        });
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),authorities);
    }
    @Override
    public void saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepo.save(user);
    }

    @Override
    public void saveRole(Role role) {
        roleRepo.save(role);
    }

    @Override
    public void affecterRoleaUser(String username, String rolename) {
        Collection<Role> roles=new ArrayList<>();
        User u=userRepo.findByUsername(username);
        Role r=roleRepo.findByName(rolename);
        if(u.getRoles()==null)
        {
            
            roles.add(r);
            u.setRoles(roles);


        }
        else
        {
            u.getRoles().add(r);

        }
        userRepo.save(u);

    }

    @Override
    public User getUser(String username) {
        return userRepo.findByUsername(username);
    }

    @Override
    public List<User> getUsers() {
        return userRepo.findAll();
    }

    @Override
    public void deleteUser(int id) {
        User u =userRepo.findById(id).orElse(null);
        userRepo.delete(u);
        //userRepo.deleteById(id);
    }

    @Override
    public void updateResetPasswordToken(String token, String email) {
        User user=userRepo.findByEmail(email);
        if(user!=null)
        {
            user.setResetPasswordToken(token);
            userRepo.save(user);
        }
        else{
            System.out.println("user not found");
        }
    }

    @Override
    public User getUserByResetPasswordToken(String token) {
        return userRepo.findByResetPasswordToken(token);
    }

    @Override
    public String updatePassword(String token, String newPassword) {
        User user=userRepo.findByResetPasswordToken(token);
        BCryptPasswordEncoder passwordEncoder=new BCryptPasswordEncoder();
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        userRepo.save(user);
        return "Your password successfully updated";
    }

    @Override
    public void signup(RegistrationForm registrationForm) {
        Collection<Role> roles=new ArrayList<>();
        Role r=roleRepo.findByName("ROLE_USER");
        User u =new User();
        u.setEmail(registrationForm.getEmail());
        u.setPassword(registrationForm.getPassword());
        u.setFirstname(registrationForm.getFirstname());
        u.setLastname(registrationForm.getLastname());
        u.setUsername(registrationForm.getUsername());
        u.setAddresse(registrationForm.getAddresse());
        u.setResetPasswordToken(null);
        roles.add(r);
        u.setRoles(roles);
        u.setPassword(passwordEncoder.encode(u.getPassword()));
        userRepo.save(u);
    }


}
