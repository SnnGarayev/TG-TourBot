package az.code.telegrambot.telegram;

import az.code.telegrambot.controllers.TgController;
import az.code.telegrambot.entity.Question;
import az.code.telegrambot.entity.operatorSide.OperatorAnswer;
import az.code.telegrambot.repo.MainSessionRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyBot extends TelegramLongPollingBot {

    @Autowired
    private TgController tgController;

    @Autowired
    private Environment env;


    // for test
    @Autowired
    private MainSessionRepo msr;


    @Override
    public String getBotUsername() {
        return env.getProperty("bot.username");
//        return "leluuubot";
    }

    @Override
    public String getBotToken() {
//        return "6253072903:AAF_VGGZc1hGh3lUBlbmQrKbN4KCopk9IVs";
        return env.getProperty("bot.token");
    }

    @PostConstruct
    private void init(){

        tgController.setMyBot(this);

//        //test
//        OperatorAnswer oa = msr.getOperatorAnswer(2l);
//        tgController.operatorOffer(752688757l, oa);
    }


    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            if (update.getMessage().getContact()!=null){

                tgController.setNumber(update.getMessage().getChatId(),update.getMessage().getFrom().getId(),update.getMessage().getContact().getPhoneNumber());
                update.getMessage().setText(update.getMessage().getContact().getPhoneNumber());}

            handleMessage(update.getMessage());
        } else if (update.hasCallbackQuery()){ // this is callback


            CallbackQuery callbackQuery = update.getCallbackQuery();

            tgController.receiveCallback(callbackQuery);

            try {

                execute(new DeleteMessage(callbackQuery.getMessage().getChatId().toString()
                        , callbackQuery.getMessage().getMessageId()));

            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }


        }
    }

    public void handleMessage(Message message) {

        if(message.getText()!=null){
        switch (message.getText()) {
            case "/start":
                System.out.println("start etdik");
                Question q = tgController.getLanguages(message.getChatId(),
                        message.getFrom().getId(),
                        message.getFrom().getUserName());
                SendMessage m = new SendMessage();
                m.setChatId(message.getChatId());
                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
                keyboardMarkup.setOneTimeKeyboard(true);
                keyboardMarkup.setSelective(true);
                keyboardMarkup.setResizeKeyboard(true);
                keyboardMarkup.setOneTimeKeyboard(true);
                List<KeyboardRow> keyboardList = new ArrayList<>();
                KeyboardRow row = new KeyboardRow();
                if (q.getAnswers() != null) {  // Əgər sualda cavab gəlmirsə, error atmasın və sadəcə mesajı göstərsin
                    q.getAnswers().forEach(elem -> {
                        KeyboardButton button = new KeyboardButton();
                        button.setText(elem.getContext());
                        row.add(button);
                    });
                }

                keyboardList.add(row);
                keyboardMarkup.setKeyboard(keyboardList);
                m.setReplyMarkup(keyboardMarkup);

                m.setText(q.getContext());

                try {
                    execute(m);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }

                break;
            case "/stop":
                Question qt1 = tgController.stop(message.getChatId());
                SendMessage sms1 = new SendMessage();
                sms1.setChatId(message.getChatId());
                sms1.setText(qt1.getContext());
                try {
                    execute(sms1);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                Question qu = tgController.getQuestion(message.getChatId(), message.getFrom().getId(), message.getText());
                String test = qu.getQuestionType().getTypeName();
                //Gelen cavabi save edirik :

                if (test.equals("text") || test.equals("date") || test.equals("notification")) {
                    SendMessage sms = new SendMessage();
                    sms.setChatId(message.getChatId());
                    sms.setText(qu.getContext());

                    if (!test.equals("notification")) { // if its notification we won't remove buttons

                        ReplyKeyboardRemove r = new ReplyKeyboardRemove(true);

                        sms.setReplyMarkup(r);
                    }

                    try {
                        execute(sms);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                } else if (test.equals("button")) {
                    SendMessage msg = new SendMessage();
                    msg.setChatId(message.getChatId());
                    ReplyKeyboardMarkup keyboardMarkup1 = new ReplyKeyboardMarkup();
                    keyboardMarkup1.setOneTimeKeyboard(true);
                    keyboardMarkup1.setSelective(true);
                    keyboardMarkup1.setResizeKeyboard(true);
                    keyboardMarkup1.setOneTimeKeyboard(true);
                    List<KeyboardRow> keyboardList1 = new ArrayList<>();
                    KeyboardRow row1 = new KeyboardRow();
                    KeyboardRow row2 = new KeyboardRow();
                    if (qu.getAnswers() != null) {
                        qu.getAnswers().forEach(elem -> {
                            KeyboardButton button = new KeyboardButton();
                            button.setText(elem.getContext());
                            if( row1.size() >1) {
                                row2.add(button);
                            }
                            else {
                                row1.add(button);
                            }
                        });
                    }
                    if (row1 != null) {
                        keyboardList1.add(row1);
                        keyboardList1.add(row2);
                        keyboardMarkup1.setKeyboard(keyboardList1);
                        msg.setReplyMarkup(keyboardMarkup1);
                        msg.setText(qu.getContext());
                        try {
                            execute(msg);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else if (test.equals("getNumber")) {

                    SendMessage ms = new SendMessage();
                    ms.setChatId(message.getChatId());
                    ms.setText(qu.getContext());
                    ReplyKeyboardMarkup replyKeyboardMarkup2 = new ReplyKeyboardMarkup();
                    replyKeyboardMarkup2.setOneTimeKeyboard(true);
                    ms.setReplyMarkup(replyKeyboardMarkup2);
                    replyKeyboardMarkup2.setSelective(true);
                    replyKeyboardMarkup2.setResizeKeyboard(true);
                    replyKeyboardMarkup2.setOneTimeKeyboard(true);
                    List<KeyboardRow> keyboard2 = new ArrayList<>();
                    KeyboardRow keyboardFirstRow2 = new KeyboardRow();
                    KeyboardButton keyboardButton2 = new KeyboardButton();
                    keyboardButton2.setText(qu.getAnswers().get(0).getContext());
                    keyboardButton2.setRequestContact(true);
                    keyboardFirstRow2.add(keyboardButton2);
                    keyboard2.add(keyboardFirstRow2);
                    replyKeyboardMarkup2.setKeyboard(keyboard2);
                    ms.setReplyMarkup(replyKeyboardMarkup2);
                    try {
                        execute(ms);
                    } catch (TelegramApiException e) {
                        throw new RuntimeException(e);
                    }
                }
        }
        }

//        if (message.hasText())
//        {
////            if (message.getText().equals("/start")) {
////                System.out.println("start etdik");
////                Question q = tgController.getLanguages(message.getChatId(), message.getFrom().getId() ,message.getFrom().getUserName());
////                SendMessage m = new SendMessage();
////                m.setChatId(message.getChatId());
////                ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
////                keyboardMarkup.setSelective(true);
////                keyboardMarkup.setResizeKeyboard(true);
////                keyboardMarkup.setOneTimeKeyboard(true);
////                List<KeyboardRow> keyboardList = new ArrayList<>();
////                KeyboardRow row = new KeyboardRow();
////
////                if (q.getAnswers() != null){  // Əgər sualda cavab gəlmirsə, error atmasın və sadəcə mesajı göstərsin
////                q.getAnswers().stream().forEach(elem -> {
////                    KeyboardButton button1 = new KeyboardButton();
////                    button1.setText(elem.getContext());
////                    row.add(button1);
////                });}
////
////
////                keyboardList.add(row);
////                keyboardMarkup.setKeyboard(keyboardList);
////                m.setReplyMarkup(keyboardMarkup);
////                m.setText(q.getContext());
////                try {
////                    execute(m);
////                } catch (TelegramApiException e) {
////                    throw new RuntimeException(e);
////                }
////
//            } else if (message.getText().equals("English")) {
//                SendMessage m = new SendMessage();
//                m.setChatId(message.getChatId());
//                m.setText("you choosed language. Now share your phone number please))");
//                ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
//                m.setReplyMarkup(replyKeyboardMarkup);
//                replyKeyboardMarkup.setSelective(true);
//                replyKeyboardMarkup.setResizeKeyboard(true);
//                replyKeyboardMarkup.setOneTimeKeyboard(true);
//                List<KeyboardRow> keyboard = new ArrayList<>();
//                KeyboardRow keyboardFirstRow = new KeyboardRow();
//                KeyboardButton keyboardButton = new KeyboardButton();
//                keyboardButton.setText("Share your number please and get more infomartion about tour");
//                keyboardButton.setRequestContact(true);
//                keyboardFirstRow.add(keyboardButton);
//                keyboard.add(keyboardFirstRow);
//                replyKeyboardMarkup.setKeyboard(keyboard);
//                m.setReplyMarkup(replyKeyboardMarkup);
//                try {
//                    execute(m);
//                } catch (TelegramApiException e) {
//                    throw new RuntimeException(e);
//                }
//
//            }
        }

        public void sendMessage(Long chatId, Question question, boolean removeOldMessage){
            SendMessage msg = new SendMessage();
            msg.setChatId(chatId);

            ReplyKeyboardMarkup keyboardMarkup1 = new ReplyKeyboardMarkup();
            keyboardMarkup1.setOneTimeKeyboard(true);
            keyboardMarkup1.setSelective(true);
            keyboardMarkup1.setResizeKeyboard(true);
            keyboardMarkup1.setOneTimeKeyboard(true);
            List<KeyboardRow> keyboardList1 = new ArrayList<>();
            KeyboardRow row1 = new KeyboardRow();
            KeyboardRow row2 = new KeyboardRow();



            if (question.getAnswers() != null) {
                question.getAnswers().forEach(elem -> {
                    KeyboardButton button = new KeyboardButton();
                    button.setText(elem.getContext());
                    if( row1.size() >1) {
                        row2.add(button);
                    }
                    else {
                        row1.add(button);
                    }
                });
            }

            if (row1 != null) {
                keyboardList1.add(row1);
                keyboardList1.add(row2);
                keyboardMarkup1.setKeyboard(keyboardList1);
                msg.setReplyMarkup(keyboardMarkup1);
            }

            if (removeOldMessage){
                ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
                msg.setReplyMarkup(remove);
            }

            msg.setText(question.getContext());


            try {
                execute(msg);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }


    public void sendMessageInlineKeyboard(Long chatId, Question question, int type, String value){

        /**
         * Divided with |
         * Types: 1- for offer response

         */

        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);

        InlineKeyboardMarkup keyboardMarkup1 = new InlineKeyboardMarkup();


        msg.setText(question.getContext());

        List<InlineKeyboardButton> buttonList = new ArrayList<>();

        List<List<InlineKeyboardButton>> buttonListMain = new ArrayList<>();

        if (question.getAnswers() != null) {
            question.getAnswers().forEach(elem -> {

                InlineKeyboardButton button = new InlineKeyboardButton();

                button.setCallbackData(type + "|" + value + "|" + elem.getContext());

                button.setText(elem.getContext());


                if( buttonList.size() >1) {
                 buttonListMain.add(buttonList);
                 buttonList.clear();
                 buttonList.add(button);
                }else {
                    buttonList.add(button);
                }
            });
            buttonListMain.add(buttonList);

        }


        if (buttonListMain.size() > 0) {

            keyboardMarkup1.setKeyboard(buttonListMain);

            msg.setReplyMarkup(keyboardMarkup1);
        }


//            ReplyKeyboardRemove remove = new ReplyKeyboardRemove(true);
//            msg.setReplyMarkup(remove);


        msg.setText(question.getContext());


        try {
            execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }
    }



