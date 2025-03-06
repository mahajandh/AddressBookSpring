package com.example.AddressBookApplication.service;

import com.example.AddressBookApplication.dto.ContactDTO;
import com.example.AddressBookApplication.Model.Contact;

import java.util.List;

public interface IContactService {
    List<ContactDTO> getAllContacts();
    ContactDTO getContactById(Long id);
    ContactDTO createContact(ContactDTO contactDTO);
    ContactDTO updateContact(Long id, ContactDTO contactDTO);
    void deleteContact(Long id);
}