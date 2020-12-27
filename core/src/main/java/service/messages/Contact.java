package service.messages;

public class Contact {
    long contactId;

    public boolean isContacted() {
        return contacted;
    }

    public void setContacted(boolean contacted) {
        this.contacted = contacted;
    }

    boolean contacted;

    public long getContactId() {
        return contactId;
    }

    public void setContactId(long contactId) {
        this.contactId = contactId;
    }


}
