package org.docssaverbot.docssaverbot.service;

import org.docssaverbot.docssaverbot.entity.User;
import org.docssaverbot.docssaverbot.repository.UserRepository;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;

@Service
@Component
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User createUser(Message message) {

        org.telegram.telegrambots.meta.api.objects.User user = message.getFrom();
        User currentUser = new User();

        currentUser.setChatId(user.getId());
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setUsername(user.getUserName());
        currentUser.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

        userRepository.save(currentUser);
        return currentUser;
    }

}
