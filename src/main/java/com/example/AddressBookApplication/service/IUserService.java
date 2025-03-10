package com.example.AddressBookApplication.service;

import com.example.AddressBookApplication.dto.UserDTO;

public interface IUserService {
    public String registerUser(UserDTO userdto);
    public String authenticateUser(String email, String password);
}