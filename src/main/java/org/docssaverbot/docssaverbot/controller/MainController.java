package org.docssaverbot.docssaverbot.controller;

import org.docssaverbot.docssaverbot.config.BotConfig;
import org.docssaverbot.docssaverbot.dto.CodeMessage;
import org.docssaverbot.docssaverbot.entity.File;
import org.docssaverbot.docssaverbot.enums.CodeMessageType;
import org.docssaverbot.docssaverbot.enums.UserStatus;
import org.docssaverbot.docssaverbot.service.StatusService;
import org.docssaverbot.docssaverbot.util.ButtonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.ArrayList;
import java.util.List;


@Component
public class MainController extends TelegramLongPollingBot {

    @Autowired
    private GeneralController generalController;

    @Autowired
    private FolderController folderController;

    @Autowired
    private FileController fileController;

    @Autowired
    private StatusService statusService;

    final BotConfig config;

    public MainController(BotConfig config) {
        this.config = config;

        List<BotCommand> commands = new ArrayList<>();
        commands.add(new BotCommand("/start", "Begin"));

        try {
            execute(new SetMyCommands(commands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {

        Message message = update.getMessage();

        if (update.hasCallbackQuery()) {
            this.viewFiles(this.fileController.handle(update));
        } else if (message.hasText()) {
            String text = message.getText();

            if (text.equals("/start") || text.equals("/help")) {
                this.sendMsg(this.generalController.handle(message));
            } else {
                CodeMessage codeMessage = this.folderController.handle(message);
                if (codeMessage.getType().equals(CodeMessageType.MAIN_CONTROLLER)) {
                    this.viewFiles(codeMessage);
                } else {
                    this.sendMsg(codeMessage);
                }
            }
        } else {
            this.sendMsg(this.fileController.handle(message));
        }

    }

    public void viewFiles(CodeMessage codeMessage) {

        SendMessage sendMessage = codeMessage.getSendMessage();
        Long chatId = codeMessage.getChatId();

        String viewOrDelete = this.statusService.getStatusByUserId(chatId).getViewOrDelete();

        if (viewOrDelete.equals(UserStatus.DELETE_FILE_FOR_WARNING.toString())) {

            File file = codeMessage.getFile();

            sendMessage.setText(".                                      " +
                    "                                                   " +
                    "                                                   " +
                    "                             .");
            codeMessage.setSendMessage(sendMessage);
            codeMessage.setType(CodeMessageType.MESSAGE);
            this.sendMsg(codeMessage);

            switch (file.getExtension()) {
                case "PHOTO" -> {
                    this.statusService.setFieldsForDelete(sendMessage,codeMessage,file.getExtension(),
                            file.getFileId(),CodeMessageType.HAVE_PHOTO_MESSAGE);
                    this.sendMsg(codeMessage);
                }
                case "DOCUMENT" -> {

                    this.statusService.setFieldsForDelete(sendMessage,codeMessage,file.getExtension(),
                            file.getFileId(),CodeMessageType.HAVE_DOCUMENT_MESSAGE);
                    this.sendMsg(codeMessage);
                }
                case "AUDIO" -> {
                    this.statusService.setFieldsForDelete(sendMessage,codeMessage,file.getExtension(),
                            file.getFileId(),CodeMessageType.HAVE_AUDIO_MESSAGE);
                    this.sendMsg(codeMessage);
                }
                case "VIDEO" -> {
                    this.statusService.setFieldsForDelete(sendMessage,codeMessage,file.getExtension(),
                            file.getFileId(),CodeMessageType.HAVE_VIDEO_MESSAGE);
                    this.sendMsg(codeMessage);
                }
                case "VOICE" -> {
                    this.statusService.setFieldsForDelete(sendMessage,codeMessage,file.getExtension(),
                            file.getFileId(),CodeMessageType.HAVE_VOICE_MESSAGE);
                    this.sendMsg(codeMessage);
                }
            }
        }else if(viewOrDelete.equals(UserStatus.VIEW_FILES.toString())){

            int count = 0;
            for (File currentFile : codeMessage.getFileList()) {
                switch (currentFile.getExtension()) {
                    case "PHOTO" -> {

                        if (count == 0) {
                            sendMessage.setReplyMarkup(ButtonUtil.keyboard(ButtonUtil.rows(
                                    ButtonUtil.row("Back", "⬅\uFE0F"))));
                            codeMessage.setSendMessage(sendMessage);
                            codeMessage.setType(CodeMessageType.HAVE_MESSAGE_PHOTO);
                            count++;
                        } else {
                            codeMessage.setType(CodeMessageType.PHOTO);
                        }

                        SendPhoto sendPhoto = new SendPhoto();
                        sendPhoto.setPhoto(new InputFile().setMedia(currentFile.getFileId()));
                        sendPhoto.setChatId(chatId);
                        codeMessage.setSendPhoto(sendPhoto);
                        this.sendMsg(codeMessage);
                    }
                    case "DOCUMENT" -> {

                        if (count == 0) {
                            sendMessage.setReplyMarkup(ButtonUtil.keyboard(ButtonUtil.rows(
                                    ButtonUtil.row("Back", "⬅\uFE0F"))));
                            codeMessage.setSendMessage(sendMessage);
                            codeMessage.setType(CodeMessageType.HAVE_MESSAGE_DOCUMENT);
                            count++;
                        } else {
                            codeMessage.setType(CodeMessageType.DOCUMENT);
                        }

                        SendDocument sendDocument = new SendDocument();
                        sendDocument.setDocument(new InputFile().setMedia(currentFile.getFileId()));
                        sendDocument.setChatId(chatId);
                        codeMessage.setSendDocument(sendDocument);
                        this.sendMsg(codeMessage);
                    }
                    case "AUDIO" -> {
                        if (count == 0) {
                            sendMessage.setReplyMarkup(ButtonUtil.keyboard(ButtonUtil.rows(
                                    ButtonUtil.row("Back", "⬅\uFE0F"))));
                            codeMessage.setSendMessage(sendMessage);
                            codeMessage.setType(CodeMessageType.HAVE_MESSAGE_AUDIO);
                            count++;
                        } else {
                            codeMessage.setType(CodeMessageType.AUDIO);
                        }

                        SendAudio sendAudio = new SendAudio();
                        sendAudio.setAudio(new InputFile().setMedia(currentFile.getFileId()));
                        sendAudio.setChatId(chatId);
                        codeMessage.setSendAudio(sendAudio);
                        this.sendMsg(codeMessage);
                    }
                    case "VIDEO" -> {
                        if (count == 0) {
                            sendMessage.setReplyMarkup(ButtonUtil.keyboard(ButtonUtil.rows(
                                    ButtonUtil.row("Back", "⬅\uFE0F"))));
                            codeMessage.setSendMessage(sendMessage);
                            codeMessage.setType(CodeMessageType.HAVE_MESSAGE_VIDEO);
                            count++;
                        } else {
                            codeMessage.setType(CodeMessageType.VIDEO);
                        }

                        SendVideo sendVideo = new SendVideo();
                        sendVideo.setVideo(new InputFile().setMedia(currentFile.getFileId()));
                        sendVideo.setChatId(chatId);
                        codeMessage.setSendVideo(sendVideo);
                        this.sendMsg(codeMessage);
                    }
                    case "VOICE" -> {
                        if (count == 0) {
                            sendMessage.setReplyMarkup(ButtonUtil.keyboard(ButtonUtil.rows(
                                    ButtonUtil.row("Back", "⬅\uFE0F"))));
                            codeMessage.setSendMessage(sendMessage);
                            codeMessage.setType(CodeMessageType.HAVE_MESSAGE_VOICE);
                            count++;
                        } else {
                            codeMessage.setType(CodeMessageType.VOICE);
                        }

                        SendVoice sendVoice = new SendVoice();
                        sendVoice.setVoice(new InputFile().setMedia(currentFile.getFileId()));
                        sendVoice.setChatId(chatId);
                        codeMessage.setSendVoice(sendVoice);
                        this.sendMsg(codeMessage);
                    }
                }

                sendMessage.setText("...................................................................");
                sendMessage.setReplyMarkup(ButtonUtil.keyboardInline(ButtonUtil.collection(ButtonUtil.rowInline(
                        ButtonUtil.buttonInline(
                                "Delete File", currentFile.getId().toString(), "❌"
                        )))));
                codeMessage.setSendMessage(sendMessage);
                codeMessage.setType(CodeMessageType.MESSAGE);
                this.statusService.changeLastSendMessage("Follow the steps required:", chatId);
                this.sendMsg(codeMessage);
            }
        }

    }

    public void sendMsg(CodeMessage codeMessage) {
        try {
            switch (codeMessage.getType()) {
                case MESSAGE -> execute(codeMessage.getSendMessage());
                case PHOTO -> execute(codeMessage.getSendPhoto());
                case DOCUMENT -> execute(codeMessage.getSendDocument());
                case AUDIO -> execute(codeMessage.getSendAudio());
                case VOICE -> execute(codeMessage.getSendVoice());
                case VIDEO -> execute(codeMessage.getSendVideo());

                /**
                 * for View File
                 */

                case HAVE_MESSAGE_PHOTO -> {
                    execute(codeMessage.getSendMessage());
                    execute(codeMessage.getSendPhoto());
                }
                case HAVE_MESSAGE_DOCUMENT -> {
                    execute(codeMessage.getSendMessage());
                    execute(codeMessage.getSendDocument());
                }
                case HAVE_MESSAGE_AUDIO -> {
                    execute(codeMessage.getSendMessage());
                    execute(codeMessage.getSendAudio());
                }
                case HAVE_MESSAGE_VOICE -> {
                    execute(codeMessage.getSendMessage());
                    execute(codeMessage.getSendVoice());
                }
                case HAVE_MESSAGE_VIDEO -> {
                    execute(codeMessage.getSendMessage());
                    execute(codeMessage.getSendVideo());
                }

                /**
                 * for Delete File
                 */

                case HAVE_PHOTO_MESSAGE -> {
                    execute(codeMessage.getSendPhoto());
                    execute(codeMessage.getSendMessage());
                }
                case HAVE_DOCUMENT_MESSAGE -> {
                    execute(codeMessage.getSendDocument());
                    execute(codeMessage.getSendMessage());
                }
                case HAVE_AUDIO_MESSAGE -> {
                    execute(codeMessage.getSendAudio());
                    execute(codeMessage.getSendMessage());
                }
                case HAVE_VOICE_MESSAGE -> {
                    execute(codeMessage.getSendVoice());
                    execute(codeMessage.getSendMessage());
                }
                case HAVE_VIDEO_MESSAGE -> {
                    execute(codeMessage.getSendVideo());
                    execute(codeMessage.getSendMessage());
                }
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String getBotUsername() {
        return config.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }
}
