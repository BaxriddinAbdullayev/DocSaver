package org.docssaverbot.docssaverbot.controller;

import org.docssaverbot.docssaverbot.dto.CodeMessage;
import org.docssaverbot.docssaverbot.entity.Folder;
import org.docssaverbot.docssaverbot.enums.CodeMessageType;
import org.docssaverbot.docssaverbot.enums.UserStatus;
import org.docssaverbot.docssaverbot.service.FolderService;
import org.docssaverbot.docssaverbot.service.StatusService;
import org.docssaverbot.docssaverbot.util.ButtonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import java.sql.Timestamp;
import java.util.List;


@Component
public class FolderController {

    @Autowired
    private FolderService folderService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private FileController fileController;

    public CodeMessage handle(Message message) {

        String text = message.getText();
        Long chatId = message.getChatId();

        CodeMessage codeMessage = new CodeMessage();
        codeMessage.setType(CodeMessageType.MESSAGE);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");

        if (text.equals("\uD83D\uDCC4 Create New Folder")) {
            sendMessage.setText("Enter a folder name:");
            this.statusService.changeLastSendMessage(sendMessage.getText(), chatId);
            this.statusService.changeUserStatus(UserStatus.NAMING_FOLDER.toString(), chatId);
            codeMessage.setSendMessage(sendMessage);
            sendMessage.setReplyMarkup(ButtonUtil.keyboard(ButtonUtil.rows(
                    ButtonUtil.row("Back", "⬅\uFE0F"))));
            return codeMessage;
        }


        switch (this.statusService.getStatusByUserId(chatId).getUserStatus()) {
            case "NAMING_FOLDER" -> {

                if (text.equals("⬅\uFE0F Back")) {
                    List<Folder> folderList = this.folderService.getByChatId(chatId);
                    if (folderList.isEmpty()) {
                        String textSendMessage = "Click on the button to create a folder: ";
                        ReplyKeyboardMarkup keyboard = ButtonUtil.keyboard(ButtonUtil.rows(
                                ButtonUtil.row("Create New Folder", "\uD83D\uDCC4")
                        ));
                        codeMessage = this.statusService.backOperation(textSendMessage,
                                keyboard, UserStatus.START, chatId);
                        return codeMessage;
                    } else {
                        String textSendMessage = "Your folders: ";
                        codeMessage = this.statusService.backOperation(textSendMessage,
                                this.folderService.showFolder(chatId),
                                UserStatus.FOLDER_NAMING_DONE, chatId);
                        return codeMessage;
                    }
                }

                if (this.folderService.isExistFolderName(text, chatId)) {
                    sendMessage.setText("❌File name exists.\n\nEnter a folder name:");
                    sendMessage.setReplyMarkup(ButtonUtil.keyboard(ButtonUtil.rows(
                            ButtonUtil.row("Back", "⬅\uFE0F"))));
                    codeMessage.setSendMessage(sendMessage);
                } else {
                    folderService.createFolderName(text, chatId);
                    sendMessage.setReplyMarkup(this.folderService.showFolder(chatId));
                    this.statusService.changeUserStatus(UserStatus.FOLDER_NAMING_DONE.toString(), chatId);
                    sendMessage.setText("Folder name saved!");
                    this.statusService.changeLastSendMessage(sendMessage.getText(), chatId);
                    codeMessage.setSendMessage(sendMessage);
                }
            }
            case "FOLDER_NAMING_DONE" -> {
                List<Folder> folderList = this.folderService.getByChatId(chatId);

                Folder currentFolder = null;
                for (Folder folder : folderList) {
                    if (folder.getFolderName().equals(text)) {
                        currentFolder = folder;
                        break;
                    }
                }

                if (currentFolder != null) {
                    sendMessage.setReplyMarkup(ButtonUtil.crudOperation());
                    sendMessage.setText("Choose one of the commands:");
                    this.statusService.changeLastSendMessage(sendMessage.getText(), chatId);
                    codeMessage.setSendMessage(sendMessage);
                    this.statusService.changeUserStatusAndFolderId(UserStatus.CHOOSING_CRUD_OPERATION.toString(),
                            currentFolder.getId(), chatId);
                } else {
                    codeMessage = this.statusService.wrongAction(chatId);
                }
            }
            case "CHOOSING_CRUD_OPERATION" -> {

                switch (text) {
                    case "➕ add file" -> {
                        sendMessage.setText("Send a file: ");
                        sendMessage.setReplyMarkup(ButtonUtil.keyboard(
                                ButtonUtil.rows(ButtonUtil.row("Back", "⬅\uFE0F"))));
                        this.statusService.changeLastSendMessage(sendMessage.getText(), chatId);
                        this.statusService.changeUserStatus(UserStatus.ADD_FILE.toString(), chatId);
                        codeMessage.setSendMessage(sendMessage);
                    }
                    case "\uD83D\uDCC4 view files" -> {
                        this.statusService.changeUserStatus(UserStatus.VIEW_FILES.toString(), chatId);
                        codeMessage = this.fileController.handle(message);
                    }
                    case "✏\uFE0F edit folder" -> {
                        Folder folder = this.folderService.getById(
                                this.statusService.getStatusByUserId(chatId).getFolderId()).get();
                        sendMessage.setText("Current folder name:   " + folder.getFolderName() +
                                "\n\nEnter new folder name: ");
                        sendMessage.setReplyMarkup(ButtonUtil.keyboard(ButtonUtil.rows(
                                ButtonUtil.row("Back", "⬅\uFE0F")
                        )));
                        this.statusService.changeLastSendMessage(sendMessage.getText(), chatId);
                        this.statusService.changeUserStatus(UserStatus.EDIT_FOLDER.toString(), chatId);
                        codeMessage.setSendMessage(sendMessage);
                    }
                    case "\uD83D\uDDD1 delete folder" -> {
                        Folder folder = this.folderService.getById(
                                this.statusService.getStatusByUserId(chatId).getFolderId()).get();
                        sendMessage.setParseMode("Markdown");
                        sendMessage.setText("*Attention*❗\uFE0F❗\uFE0F❗\uFE0F\n\n" +
                                "Deleting a folder also deletes its contents.\n\n" +
                                "Are you sure you want to delete the *" + folder.getFolderName() + "* folder?");

                        KeyboardRow row = ButtonUtil.row(ButtonUtil.button("Yes"));
                        row.add(ButtonUtil.button("No"));
                        sendMessage.setReplyMarkup(ButtonUtil.keyboard(ButtonUtil.rows(row)));

                        this.statusService.changeUserStatus(UserStatus.DELETE_FOLDER.toString(), chatId);
                        this.statusService.changeLastSendMessage(sendMessage.getText(), chatId);
                        codeMessage.setSendMessage(sendMessage);
                    }
                    case "⬅\uFE0F Back" -> {
                        String textSendMessage = "Your folders: ";
                        codeMessage = this.statusService.backOperation(textSendMessage,
                                this.folderService.showFolder(chatId),
                                UserStatus.FOLDER_NAMING_DONE, chatId);
                    }
                    default -> {
                        codeMessage = this.statusService.wrongAction(chatId);
                    }
                }


            }
            case "VIEW_FILES_OPERATION", "ADD_FILE" -> {
                if (text.equals("⬅\uFE0F Back")) {
                    String textSendMessage = "Choose one of the commands:";
                    codeMessage = this.statusService.backOperation(textSendMessage,
                            ButtonUtil.crudOperation(), UserStatus.CHOOSING_CRUD_OPERATION,
                            chatId);
                    return codeMessage;
                } else {
                    codeMessage = this.statusService.wrongAction(chatId);
                }
            }

            case "DELETE_FOLDER" -> {
                if (text.equals("Yes")) {
                    Folder folder = this.folderService.getById(
                            this.statusService.getStatusByUserId(chatId).getFolderId()).get();

                    sendMessage.setText("*" + folder.getFolderName() + "* deleted successfully!");
                    this.statusService.changeUserStatus(UserStatus.DELETE_FILES_WITH_FOLDER.toString(), chatId);
                    this.statusService.changeLastSendMessage("Your folders:", chatId);


                    folder.setIsActive(false);
                    this.folderService.save(folder);
                    this.fileController.handle(message);

                    this.statusService.changeUserStatus(UserStatus.FOLDER_NAMING_DONE.toString(), chatId);
                    sendMessage.setReplyMarkup(this.folderService.showFolder(chatId));

                    codeMessage.setSendMessage(sendMessage);
                } else if (text.equals("No")) {
                    String textSendMessage = "Choose one of the commands:";
                    codeMessage = this.statusService.backOperation(textSendMessage,
                            ButtonUtil.crudOperation(), UserStatus.CHOOSING_CRUD_OPERATION,
                            chatId);
                } else {
                    codeMessage = this.statusService.wrongAction(chatId);
                }
            }
            case "EDIT_FOLDER" -> {

                if (text.equals("⬅\uFE0F Back")) {
                    String textSendMessage = "Choose one of the commands:";
                    codeMessage = this.statusService.backOperation(textSendMessage,
                            ButtonUtil.crudOperation(), UserStatus.CHOOSING_CRUD_OPERATION,
                            chatId);
                    return codeMessage;
                }

                Folder folder = this.folderService.getById(
                        this.statusService.getStatusByUserId(chatId).getFolderId()).get();
                folder.setFolderName(text);
                folder.setChangedAt(new Timestamp(System.currentTimeMillis()));
                this.folderService.save(folder);
                sendMessage.setText("Folder renamed successfully!");
                sendMessage.setReplyMarkup(ButtonUtil.crudOperation());
                this.statusService.changeLastSendMessage(sendMessage.getText(), chatId);
                this.statusService.changeUserStatus(UserStatus.CHOOSING_CRUD_OPERATION.toString(), chatId);
                codeMessage.setSendMessage(sendMessage);
            }
            case "DELETE_FILE" -> {
                codeMessage = this.fileController.handle(message);
            }
            default -> {
                codeMessage = this.statusService.wrongAction(chatId);
            }

        }

        return codeMessage;
    }

}
