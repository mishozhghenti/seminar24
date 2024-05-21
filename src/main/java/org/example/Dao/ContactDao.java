package org.example.Dao;

import org.example.Model.Contact;

public interface ContactDao {
    public Contact get(String id);
    public String create(Contact contact);
}
