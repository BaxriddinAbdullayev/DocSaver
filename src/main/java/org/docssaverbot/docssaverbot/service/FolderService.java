package org.docssaverbot.docssaverbot.service;

import org.docssaverbot.docssaverbot.dto.CodeMessage;
import org.docssaverbot.docssaverbot.entity.Folder;
import org.docssaverbot.docssaverbot.enums.CodeMessageType;
import org.docssaverbot.docssaverbot.enums.UserStatus;
import org.docssaverbot.docssaverbot.repository.FolderRepository;
import org.docssaverbot.docssaverbot.util.ButtonUtil;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Component
public class FolderService {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private StatusService statusService;

    public Folder createFolderName(String text, Long chatId) {

        Folder folder = new Folder();
        folder.setFolderName(text);
        folder.setChatId(chatId);
        folder.setIsActive(true);
        folder.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

        folderRepository.save(folder);
        return folder;
    }

    public boolean isExistFolderName(String folderName, Long chatId) {
        List<Folder> folderList = this.getByChatId(chatId);

        if (folderList.isEmpty()) {
            return false;
        }

        for (Folder folder : folderList) {
            if (folder.getFolderName().equals(folderName)) {
                return true;
            }
        }
        return false;
    }

    public ReplyKeyboardMarkup showFolder(Long chatId) {

        List<Folder> folderNamesByUserId = this.getByChatId(chatId);
        List<KeyboardRow> rows = new ArrayList<>();
        for (Folder folder : folderNamesByUserId) {
            rows.add(ButtonUtil.row(folder.getFolderName()));
        }

        rows.add(ButtonUtil.row("Create New Folder", "\uD83D\uDCC4"));
        ReplyKeyboardMarkup keyboard = ButtonUtil.keyboard(rows);

        return keyboard;
    }

    public List<Folder> getByChatId(Long chatId) {
        return this.folderRepository.findActiveFoldersByChatId(chatId);
    }

    public Optional<Folder> getById(UUID id) {
        return this.folderRepository.findById(id);
    }

    public Folder save(Folder folder) {
        return this.folderRepository.save(folder);
    }

    public CodeMessage backOperation(String sendMessageText,ReplyKeyboardMarkup keyboardMarkup,
                                     UserStatus userStatus,Long chatId){
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(sendMessageText);
        sendMessage.setReplyMarkup(keyboardMarkup);
        this.statusService.changeLastSendMessage(sendMessage.getText(), chatId);
        this.statusService.changeUserStatus(userStatus.toString(), chatId);
        CodeMessage codeMessage=new CodeMessage();
        codeMessage.setType(CodeMessageType.MESSAGE);
        codeMessage.setSendMessage(sendMessage);
        return codeMessage;
    }
}
