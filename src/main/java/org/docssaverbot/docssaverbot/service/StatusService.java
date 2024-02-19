package org.docssaverbot.docssaverbot.service;

import org.docssaverbot.docssaverbot.dto.CodeMessage;
import org.docssaverbot.docssaverbot.entity.Status;
import org.docssaverbot.docssaverbot.enums.CodeMessageType;
import org.docssaverbot.docssaverbot.enums.UserStatus;
import org.docssaverbot.docssaverbot.repository.StatusRepository;
import org.docssaverbot.docssaverbot.util.ButtonUtil;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.UUID;


@Service
@Component
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    public Status setFileId(Long chatId,UUID fileId){
        Status currentStatus = this.statusRepository.findByChatId(chatId);
        currentStatus.setFileId(fileId);
        this.statusRepository.save(currentStatus);
        return currentStatus;
    }

    public Status changeUserStatus(String userStatus, Long chatId) {
        Status currentStatus = this.statusRepository.findByChatId(chatId);

        if (currentStatus == null) {
            Status newStatus = new Status();
            newStatus.setUserStatus(userStatus);
            newStatus.setChatId(chatId);
            this.statusRepository.save(newStatus);
            return newStatus;
        }
        currentStatus.setUserStatus(userStatus);
        this.statusRepository.save(currentStatus);
        return currentStatus;
    }

    public Status changeViewOrDeleteStatus(String viewOrDeleteStatus, Long chatId) {
        Status currentStatus = this.statusRepository.findByChatId(chatId);
        currentStatus.setViewOrDelete(viewOrDeleteStatus);
        this.statusRepository.save(currentStatus);
        return currentStatus;
    }

    public CodeMessage wrongAction(Long chatId){
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");
        sendMessage.setText("❌❌❌ *Wrong action.*\n\n" + this.getLastMessage(chatId));

        CodeMessage codeMessage = new CodeMessage();
        codeMessage.setType(CodeMessageType.MESSAGE);
        codeMessage.setSendMessage(sendMessage);
        return codeMessage;
    }

    public Status changeLastSendMessage(String lastSendMessage, Long chatId) {
        Status currentStatus = this.statusRepository.findByChatId(chatId);

        if(currentStatus==null){
            Status newStatus = new Status();
            newStatus.setLastSendMessage(lastSendMessage);
            newStatus.setChatId(chatId);
            this.statusRepository.save(newStatus);
            return newStatus;
        }
        currentStatus.setLastSendMessage(lastSendMessage);
        this.statusRepository.save(currentStatus);
        return currentStatus;
    }

    public Status changeUserStatusAndFolderId(String userStatus, UUID folderId, Long chatId) {
        Status currentStatus = this.statusRepository.findByChatId(chatId);
        currentStatus.setUserStatus(userStatus);
        currentStatus.setFolderId(folderId);
        this.statusRepository.save(currentStatus);
        return currentStatus;
    }

    public String getLastMessage(Long chatId){
        return this.statusRepository.findByChatId(chatId).getLastSendMessage();
    }

    public Status getStatusByUserId(Long userId) {
        return this.statusRepository.findByChatId(userId);
    }

    public CodeMessage backOperation(String sendMessageText, ReplyKeyboardMarkup keyboardMarkup,
                                     UserStatus userStatus, Long chatId){
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sendMessageText);
        sendMessage.setReplyMarkup(keyboardMarkup);

        this.changeLastSendMessage(sendMessageText, chatId);
        this.changeUserStatus(userStatus.toString(), chatId);

        CodeMessage codeMessage=new CodeMessage();
        codeMessage.setType(CodeMessageType.MESSAGE);
        codeMessage.setSendMessage(sendMessage);
        return codeMessage;
    }

    public CodeMessage setFieldsForDelete(SendMessage sendMessage, CodeMessage codeMessage,String extension,
                                 String fileId,CodeMessageType codeMessageType){
        KeyboardRow row = ButtonUtil.row(ButtonUtil.button("Yes"));
        row.add(ButtonUtil.button("No"));
        sendMessage.setReplyMarkup(ButtonUtil.keyboard(ButtonUtil.rows(row)));
        sendMessage.setText("❗\uFE0F *Are you sure you want to delete this file?*");
        this.changeLastSendMessage(sendMessage.getText(), codeMessage.getChatId());

        codeMessage.setSendMessage(sendMessage);
        codeMessage.setType(codeMessageType);

        switch (extension){
            case "PHOTO" -> {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setPhoto(new InputFile().setMedia(fileId));
                sendPhoto.setChatId(codeMessage.getChatId());
                codeMessage.setSendPhoto(sendPhoto);
            }
            case "DOCUMENT" -> {
                SendDocument sendDocument = new SendDocument();
                sendDocument.setDocument(new InputFile().setMedia(fileId));
                sendDocument.setChatId(codeMessage.getChatId());
                codeMessage.setSendDocument(sendDocument);
            }
            case "AUDIO" -> {
                SendAudio sendAudio = new SendAudio();
                sendAudio.setAudio(new InputFile().setMedia(fileId));
                sendAudio.setChatId(codeMessage.getChatId());
                codeMessage.setSendAudio(sendAudio);
            }
            case "VIDEO" -> {
                SendVideo sendVideo = new SendVideo();
                sendVideo.setVideo(new InputFile().setMedia(fileId));
                sendVideo.setChatId(codeMessage.getChatId());
                codeMessage.setSendVideo(sendVideo);
            }
            case "VOICE" -> {
                SendVoice sendVoice = new SendVoice();
                    sendVoice.setVoice(new InputFile().setMedia(fileId));
                    sendVoice.setChatId(codeMessage.getChatId());
                    codeMessage.setSendVoice(sendVoice);
            }
        }

        return codeMessage;
    }

}
