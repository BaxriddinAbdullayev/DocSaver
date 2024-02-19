package org.docssaverbot.docssaverbot.controller;

import org.docssaverbot.docssaverbot.dto.CodeMessage;
import org.docssaverbot.docssaverbot.entity.User;
import org.docssaverbot.docssaverbot.enums.CodeMessageType;
import org.docssaverbot.docssaverbot.enums.UserStatus;
import org.docssaverbot.docssaverbot.repository.UserRepository;
import org.docssaverbot.docssaverbot.service.StatusService;
import org.docssaverbot.docssaverbot.service.UserService;
import org.docssaverbot.docssaverbot.util.ButtonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

@Component
public class GeneralController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StatusService statusService;

    public CodeMessage handle(Message message) {

        Long chatId = message.getChatId();
        String text = message.getText();

        CodeMessage codeMessage = new CodeMessage();
        codeMessage.setType(CodeMessageType.MESSAGE);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setParseMode("Markdown");

        if (text.equals("/start")) {

            User currentUser = userRepository.findByChatId(chatId);

            if (currentUser == null) {
                userService.createUser(message);
                this.statusService.changeUserStatus(UserStatus.START.toString(), chatId);
            } else {
                return this.statusService.wrongAction(chatId);
            }

            sendMessage.setText("Click on the button to create a folder: ");
            this.statusService.changeLastSendMessage(sendMessage.getText(), chatId);
            ReplyKeyboardMarkup replyKeyboardMarkup = ButtonUtil.keyboard(ButtonUtil.rows(
                    ButtonUtil.row("Create New Folder", "\uD83D\uDCC4")
            ));
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
            codeMessage.setSendMessage(sendMessage);
        } else if (text.equals("/help") && this.statusService.getStatusByUserId(chatId).getUserStatus().equals(
                UserStatus.CHOOSING_CRUD_OPERATION.toString())) {
            sendMessage.setText("What can this bot do?\n" +
                    "\n" +
                    "\uD83D\uDC4B Hello! I'm DocsSaverBot for saving documents.\n" +
                    "\n" +
                    "I can help you save files in the following formats:\n" +
                    "\n" +
                    "\uD83D\uDCC4 document\n" +
                    "\uD83C\uDFA5 video\n" +
                    "\uD83D\uDCF7 picture\n" +
                    "\uD83C\uDFB5 audio\n" +
                    "\uD83C\uDFA4 voice\n" +
                    "\n" +
                    "Sincerely, DocsSaverBot \uD83E\uDD16");
            sendMessage.setChatId(chatId);
            sendMessage.setParseMode("Markdown");
            sendMessage.setReplyMarkup(ButtonUtil.crudOperation());
            codeMessage.setSendMessage(sendMessage);
            this.statusService.changeLastSendMessage("Choose one of the commands:",chatId);
        }

        return codeMessage;
    }


}
