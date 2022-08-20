package com.example.trituxuser.mail;

import javax.mail.MessagingException;

public interface EmailSender {
    void send(String to,String link) throws MessagingException;
}
