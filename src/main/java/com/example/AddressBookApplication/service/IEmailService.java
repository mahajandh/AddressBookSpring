package com.example.AddressBookApplication.service;

public interface IEmailService {
    public void sendEmail(String toEmail, String subject, String body);
}