package com.example.trituxuser;

import com.example.trituxuser.entity.Role;
import com.example.trituxuser.entity.User;
import com.example.trituxuser.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

@SpringBootApplication
public class TrituxUserApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrituxUserApplication.class, args);
	}
	@Bean
	PasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner run(UserService userService)
	{
		return args -> {
			userService.saveRole(new Role(0,"ROLE_USER"));
			userService.saveRole(new Role(0,"ROLE_ADMIN"));
			userService.saveRole(new Role(0,"ROLE_MANAGER"));

			userService.saveUser(new User(0,"Elaa","Elaa","Elaa123","Elaa123","password123","elaa.boulifi@esprit.tn",null,new ArrayList<>()));
			userService.saveUser(new User(0,"Hadhemi","Hadhemi","Hadhemi123","Hadhemi","password123","elaa1.boulifi@esprit.tn",null,new ArrayList<>()));
			userService.saveUser(new User(0,"Mohamed","Mohamed","Mohamed123","Mohamed123","password123","elaa2.boulifi@esprit.tn",null,new ArrayList<>()));

			userService.affecterRoleaUser("Elaa123","ROLE_USER");
			userService.affecterRoleaUser("Hadhemi","ROLE_ADMIN");
			userService.affecterRoleaUser("Mohamed123","ROLE_ADMIN");

		};
	}

}
