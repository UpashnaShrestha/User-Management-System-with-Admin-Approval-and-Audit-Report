package com.project.UserManagementSystem;

import com.project.UserManagementSystem.entity.User;
import com.project.UserManagementSystem.enums.UserStatus;
import com.project.UserManagementSystem.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;

@SpringBootApplication
public class UserManagementSystemApplication {


	public static void main(String[] args) {
		SpringApplication.run(UserManagementSystemApplication.class, args);
	}

}
