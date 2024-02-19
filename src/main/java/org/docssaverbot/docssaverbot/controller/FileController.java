package org.docssaverbot.docssaverbot.controller;

import org.docssaverbot.docssaverbot.dto.CodeMessage;
import org.docssaverbot.docssaverbot.entity.File;
import org.docssaverbot.docssaverbot.entity.Status;
import org.docssaverbot.docssaverbot.enums.CodeMessageType;
import org.docssaverbot.docssaverbot.enums.FileExtension;
import org.docssaverbot.docssaverbot.enums.UserStatus;
import org.docssaverbot.docssaverbot.service.FileService;
import org.docssaverbot.docssaverbot.service.StatusService;
import org.docssaverbot.docssaverbot.util.ButtonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;

import java.util.List;
import java.util.UUID;

@Component
public class    FileController {

    @Autowired
    private StatusService statusService;

    @Autowired
    private FileService fileService;

    public CodeMessage handle(Message message) {

        Long chatId = message.getChatId();


        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");

        CodeMessage codeMessage = new CodeMessage();
        codeMessage.setType(CodeMessageType.MESSAGE);
        Status statusByUserId = this.statusService.getStatusByUserId(chatId);

        switch (statusByUserId.getUserStatus()) {
            case "ADD_FILE" -> {

                if (message.hasPhoto()) {
                    List<PhotoSize> photo = message.getPhoto();

                    int count = 0;
                    for (PhotoSize photoSize : photo) {
                        if (count == photo.size() - 1) {
                            codeMessage = this.fileService.addFile(message,
                                    FileExtension.PHOTO.toString(), photoSize.getFileId());
                        }
                        count++;
                    }
                } else if (message.hasVideo()) {
                    Video video = message.getVideo();
                    codeMessage = this.fileService.addFile(message,
                            FileExtension.VIDEO.toString(), video.getFileId());
                } else if (message.hasDocument()) {
                    Document document = message.getDocument();
                    codeMessage = this.fileService.addFile(message,
                            FileExtension.DOCUMENT.toString(), document.getFileId());
                } else if (message.hasAudio()) {
                    Audio audio = message.getAudio();
                    codeMessage = this.fileService.addFile(message,
                            FileExtension.AUDIO.toString(), audio.getFileId());
                } else if (message.hasVoice()) {
                    Voice voice = message.getVoice();
                    codeMessage = this.fileService.addFile(message,
                            FileExtension.VOICE.toString(), voice.getFileId());
                } else {
                    sendMessage.setText("❗\uFE0F*Sorry*, this bot is only designed to save files in formats such as" +
                            " *image*, *video*, *document*, *audio* and *voice*.\n\n" +
                            "If you need information on how the bot works, go to */help*");
                    sendMessage.setReplyMarkup(ButtonUtil.crudOperation());
                    codeMessage.setSendMessage(sendMessage);
                    codeMessage.setType(CodeMessageType.MESSAGE);
                    this.statusService.changeLastSendMessage("Choose one of the commands:",chatId);
                    this.statusService.changeUserStatus(UserStatus.CHOOSING_CRUD_OPERATION.toString(),chatId);
                }
            }
            case "VIEW_FILES" -> {
                List<File> fileList = this.fileService.getAllByFolderId(statusByUserId.getFolderId());

                if (fileList.isEmpty()) {
                    sendMessage.setText("❗\uFE0F No file added yet");
                    sendMessage.setReplyMarkup(ButtonUtil.crudOperation());
                    this.statusService.changeUserStatus(UserStatus.CHOOSING_CRUD_OPERATION.toString(), chatId);
                    this.statusService.changeLastSendMessage("Choose one of the commands:", chatId);
                    codeMessage.setSendMessage(sendMessage);
                    return codeMessage;
                }

                sendMessage.setText("Your files: ");
                codeMessage = this.fileService.setCodeMessageField(UserStatus.VIEW_FILES,
                        UserStatus.VIEW_FILES_OPERATION, CodeMessageType.MAIN_CONTROLLER,
                        chatId, sendMessage, fileList, null);
            }
            case "DELETE_FILE" -> {

                if (message.hasText() && message.getText().equals("Yes")) {
                    File file = this.fileService.getFileById(
                            this.statusService.getStatusByUserId(chatId).getFileId()).get();
                    file.setIsActive(false);
                    this.fileService.save(file);

                    sendMessage.setText("*File deleted successfully!*");
                    this.statusService.changeUserStatus(UserStatus.CHOOSING_CRUD_OPERATION.toString(), chatId);
                    this.statusService.changeLastSendMessage("Choose one of the commands:", chatId);

                    sendMessage.setReplyMarkup(ButtonUtil.crudOperation());
                    codeMessage.setSendMessage(sendMessage);
                } else if (message.hasText() && message.getText().equals("No")) {
                    String textSendMessage = "Choose one of the commands:";
                    codeMessage = this.statusService.backOperation(textSendMessage,
                            ButtonUtil.crudOperation(), UserStatus.CHOOSING_CRUD_OPERATION,
                            chatId);
                    codeMessage.setType(CodeMessageType.MESSAGE);
                } else {
                    codeMessage = this.statusService.wrongAction(chatId);
                }
            }
            case "DELETE_FILES_WITH_FOLDER" -> {
                List<File> files = this.fileService.getAllByFolderId(
                        statusByUserId.getFolderId());

                for (File file : files) {
                    file.setIsActive(false);
                    this.fileService.save(file);
                }
            }
            default -> {
                codeMessage = this.statusService.wrongAction(chatId);
            }
        }
        return codeMessage;
    }

    public CodeMessage handle(Update update) {

        CallbackQuery callbackQuery = update.getCallbackQuery();
        String data = callbackQuery.getData();
        Long chatId = callbackQuery.getMessage().getChatId();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");

        CodeMessage codeMessage = new CodeMessage();
        Status statusByUserId = this.statusService.getStatusByUserId(chatId);

        switch (statusByUserId.getUserStatus()) {
            case "VIEW_FILES_OPERATION" -> {
                File currentFile = this.fileService.getFileById(UUID.fromString(data)).get();
                this.statusService.setFileId(chatId, UUID.fromString(data));

                codeMessage = this.fileService.setCodeMessageField(UserStatus.DELETE_FILE_FOR_WARNING,
                        UserStatus.DELETE_FILE, CodeMessageType.MAIN_CONTROLLER, chatId,
                        sendMessage, null, currentFile);

            }
        }

        return codeMessage;
    }
}