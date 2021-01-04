package service.messages;

import java.util.ArrayList;

public class ContactList {

    private ArrayList<Contact> contacts;
    public ContactList() {
        this.contacts = new ArrayList<>();
    }

    public ContactList(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public void addContact(Contact contact) {
        this.contacts.add(contact);
    }

    public int size() {
        return contacts.size();
    }
}
