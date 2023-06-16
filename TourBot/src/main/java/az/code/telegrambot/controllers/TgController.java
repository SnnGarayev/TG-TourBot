package az.code.telegrambot.controllers;

import az.code.telegrambot.controllers.redis.redis.RedisRepo;
import az.code.telegrambot.entity.*;
import az.code.telegrambot.entity.operatorSide.MainSession;
import az.code.telegrambot.entity.operatorSide.OperatorAnswer;
import az.code.telegrambot.repo.*;
import az.code.telegrambot.telegram.MyBot;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
@Setter
@Slf4j
public class TgController {
    @Autowired
    private final ApplicationContext ac;
    @Autowired
    private final RedisRepo redis;

    private QuestionRepoMain questionRepo;
    private LanguageRepoMain languageRepo;
    private SessionRepoMain sessionRepo;
    private UserRepoMain userRepo;
    private QuestionTypeRepoMain questionTypeRepo;
    private MainSessionRepo mainSessionRepo;
    private MyBot myBot;

    @Value("${questions_size}")
    private Integer queueSize;

    @PostConstruct
    private void init(){
        questionRepo = ac.getBean(QuestionRepoMain.class);
        languageRepo = ac.getBean(LanguageRepoMain.class);
        questionTypeRepo = ac.getBean(QuestionTypeRepoMain.class);
        userRepo = ac.getBean(UserRepoMain.class);
        sessionRepo=ac.getBean(SessionRepoMain.class);
        mainSessionRepo = ac.getBean(MainSessionRepo.class);

//        mainSessionRepo.blockMainSession(752688757l);

//        myBot = ac.getBean(MyBot.class);
//        getLanguages(1L, 0L, "Jamal");
//
//        System.out.println(getQuestion(1L, 0L, "English").getContext());
//        localizeQuestion("En", questionRepo.getQuestionById(0l));


    }



    // Startdan bura call gələcək, cachedə Chatİd li user olduqda onun queue-su davam edəcək, yoxdursa yenisi əlavə olunacaq
    public Question getQuestion(Long chatID, Long userID, String answer){

        RedisChat redisChat = redis.getRedis(chatID);

        Question q;

//        Long lastid = getLastId();

        if (redisChat == null){

            /**
             *  Yoxlamaq lazimdir, aktiv sessiya var ya yox
             *
             */

//            q = Question.builder().context("Aktiv sorğu yoxdur. Yeni sorğu yaratmaq üçün /start yazın")
//                    .questionType(questionTypeRepo.getQuestionTypes().get(1)).build();


            q = localizeQuestion(getLanguageSecure(redisChat), questionRepo.getQuestionById(11l)); // aktiv sorğu yoxdur

        } else {


            Long stage = redisChat.getStage();


            if (validateAnswer(stage, answer)){
                if (stage == -1){
                    Language l = languageRepo.getLanguageByButton(answer);
                    redisChat.setLanguage(l);
                    if (redisChat.getUser().getPhoneNumber() != null){
                        redisChat.setStage(0l);
                        stage = 0l;
                    }
                } else if (stage == 0) { // we are not doing anything if question is number

                    if (redisChat.getUser().getPhoneNumber() == null) {

//                        return returnMessage("Proqramın düzgün işləməsi üçün, nömrənizi paylaşmağınız mütləqdir");

                        return localizeQuestion(getLanguageSecure(redisChat), questionRepo.getQuestionById(12l)); // Proqramın düzgün işləməsi üçün, nömrənizi paylaşmağınız mütləqdir

                    }

//                } else if (stage > lastid){
                } else if (stage > queueSize){
//                    return  Question.builder().context("Sizin aktiv olan sorğunuz var. Sorğuya davam edin və ya /stop əmrini yazmaqla bu sorğunu dayandıra bilərsən")
//                            .questionType(questionTypeRepo.getQuestionTypes().get(1)).build();
                    return localizeQuestion(getLanguageSecure(redisChat), questionRepo.getQuestionById(13l)); // Sizin aktiv olan sorğunuz var. Sorğuya davam edin və ya /stop əmrini yazmaqla bu sorğunu dayandıra bilərsən

                }
                else {
                    String [] str = redisChat.getAnswerList();
                    str[stage.intValue()] = answer;
                    redisChat.setAnswerList(str);
                }
            } else {  // answer is not valid, we need to send this message

//                q = Question.builder().context("Cavab suala uyğun deyildir")
//                        .questionType(questionTypeRepo.getQuestionTypes().get(1)).build();

//                return q;

//                if (stage >lastid){
                if (stage >queueSize){
//                    return returnMessage("Sizin aktiv olan sorğunuz var. Sorğuya davam edin və ya /stop əmrini yazmaqla bu sorğunu dayandıra bilərsən");

                    return localizeQuestion(getLanguageSecure(redisChat), questionRepo.getQuestionById(13l)); // Sizin aktiv olan sorğunuz var. Sorğuya davam edin və ya /stop əmrini yazmaqla bu sorğunu dayandıra bilərsən

                } else {
//                    return returnMessage("Cavab suala uyğun deyildir");
                    return localizeQuestion(getLanguageSecure(redisChat), questionRepo.getQuestionById(14l)); // Cavab suala uyğun deyildir
                }

            }


            redisChat.setStage(stage +1); // sualı növbəti stage-a keçirir


            redis.saveRedisWithChatId(redisChat, chatID);

//        QuestionType qt = questionTypeRepo.getQuestionTypes().get(1);

//            if (redisChat.getStage()<=lastid){
            if (redisChat.getStage()<=queueSize){

//                Question qt = questionRepo.getQuestionById(redisChat.getStage());
                Question qt = questionRepo.getQuestionByQueue(redisChat.getStage().intValue());

                q = localizeQuestion(getLanguageSecure(redisChat), qt);

            } else {   // formanın doldurulması bitdikdən sonra aşağıdakı mesajı göstərmək lazımdır
//                QuestionType qt = questionTypeRepo.getQuestionTypes().get(1);
//                q = Question.builder().context("Əla, Sorğunu aldım. Ən qısa zamanda istəyinə uyğun təkliflər göndərəcəm")
//                        .questionType(qt).build();

                Question question = questionRepo.getQuestionById(15l);

                q = localizeQuestion(getLanguageSecure(redisChat), question); // Əla, Sorğunu aldım. Ən qısa zamanda istəyinə uyğun təkliflər göndərəcəm


                endSession(redisChat);
            }

        }

        return q;

    }




    // bütün suallar cavablandıqda endSession metodu çağırılacaq
    public void endSession(RedisChat redisChat){

        System.out.println(redisChat.getUser().getUserName() + " compleated form");
        String[] answers=redisChat.getAnswerList();
        List<SessionReply > sessionReplies = new ArrayList<>();
        List<Question> questions=questionRepo.getQuestions();



        User u = userRepo.getUserByIdAndChatId(redisChat.getUser().getId(),
                Long.valueOf(redisChat.getUser().getChatId()));

//
//        MainSession mainSession = MainSession.builder()
//                .date(LocalDate.now()).stage(0).build();

        Session session=Session.builder().user(u).userReplies(sessionReplies).build();




        for (int i=2;i<answers.length;i++){
            String s=answers[i];
            SessionReply sessionReply=SessionReply.builder().
                    answer(s).question(questions.get(i-1)).sessionId(session).build();

            sessionReplies.add(sessionReply);
        }

        session.setUserReplies(sessionReplies);

        MainSession mainSession = MainSession.builder().session(session)
                .date(LocalDate.now()).stage(0).build();


        mainSessionRepo.saveMainSession(mainSession);
    }



//    // bunu cache da saxlamaq lazimdır, cunki her /start olduqda DB dan eyni melumatlari alir
//    @Cacheable("languages")
    public Question getLanguages(Long chatId, Long userId, String username){

        Question question;

        User user = getUser(chatId, userId, username); // We will create or get user that already exist

        RedisChat redisChat = getRedischat(chatId);

//        Long stageOut;

//        if (user.getPhoneNumber() == null){
//            stageOut = 0l;
//        } else {
//            stageOut = 1l;
//        }

        Boolean checkStatus = false;

        List<MainSession> msList = mainSessionRepo.getMainSessionsOfUser(chatId);

        if (msList.size()>0){
            checkStatus = true;
        }

        if (redisChat == null || checkStatus){ // Then redischat exists, we need to show /stop comand

//            question = Question.builder().context("Sizin aktiv olan sorğunuz var. Sorğuya davam edin və ya /stop" +
//                            " əmrini yazmaqla bu sorğunu dayandıra bilərsən")
//                    .questionType(questionTypeRepo.getQuestionTypes().get(1)).build();

            question = localizeQuestion(getLanguageSecure(redisChat), questionRepo.getQuestionById(13l)); // Sizin aktiv olan sorğunuz var. Sorğuya davam edin və ya /stop əmrini yazmaqla bu sorğunu dayandıra bilərsən

        } else {

            redisChat.setUser(user);

//            redisChat.setStage(stageOut);
            redisChat.setStage(-1l);

            redis.saveRedisWithChatId(redisChat, chatId);


            List<Language> languages = languageRepo.getLanguages();

            List<QuestionsAnswer> questionsAnswers = new ArrayList<>();

            languages.forEach(a -> {
                questionsAnswers.add(QuestionsAnswer.builder()
                        .locale(null)

                        .context(a.getButtonText()).build());
            });

            QuestionType questionType = questionTypeRepo.getQuestionTypeById(0l);

            question = Question.builder()
                    .id(9999l)
                    .questionType(questionType)
                    .context("Xoş gördük. Hansı dildə danışmaq sənə rahat olar?")
                    .answers(questionsAnswers)
                    .build();

        }

        return question;
    }



    /**
     * Sualı telegramda göstərilən formaya gətirir
     */
    @Cacheable("questions")
    public Question localizeQuestion(String locale, Question question){

        Question questionOut = question;

        log.info(question.getContext());

        questionOut.setContext(
                question.getTranslations()
                        .getLocaleTranslations()
                        .stream().filter(a-> {
                            if (locale != null){ return a.getLanguage()
                                .getLanguage().equalsIgnoreCase(locale);}
                            else return a.getLanguage()
                                    .getLanguage().equalsIgnoreCase("Az");
                        })
                        .collect(Collectors.toList()).get(0)
                        .getTranslate()
        );

        questionOut.getAnswers().forEach(
                a->{
                    a.setContext(
                            a.getLocale()
                                    .getLocaleTranslations().stream().filter(b-> {
                                                if (locale != null){ return b.getLanguage()
                                                        .getLanguage().equalsIgnoreCase(locale);}
                                                else return b.getLanguage()
                                                        .getLanguage().equalsIgnoreCase("Az");
                                                }
                                    )
                                    .collect(Collectors.toList()).get(0).getTranslate());
                }
        );

        return questionOut;

    }

    private User getUser(Long chatID, Long userID, String username){
        User user = userRepo.getUserByIdAndChatId(userID, chatID);

        if (user == null){
            user = User.builder()
                    .telegramUserId(userID.toString())
                    .chatId(chatID.toString())
                    .userName(username)
                    .build();
        }

        userRepo.saveUser(user);

        return user;
    }

    private RedisChat getRedischat(Long chatId){
        RedisChat redisChat = redis.getRedis(chatId);

        if (redisChat == null){
            redisChat = RedisChat.builder().id(chatId).stage(-1l)
                    .answerList(new String[queueSize+1]).build();
            redis.saveRedisWithChatId(redisChat, chatId);
        } else {
            return null;}

        return redisChat;
    }

    @Cacheable("lastid")
    public Long getLastId(){return questionRepo.getLastId();}



    public Question stop(Long chatId){

        RedisChat redisChat = redis.getRedis(chatId);

        redis.deleteRedis(chatId);

        mainSessionRepo.blockMainSession(chatId);

//        return Question.builder().context("Sorğunuz dayandırılmışdır. Yeni sorğu başlamaq üçün /start yazın")
//                .questionType(questionTypeRepo.getQuestionTypes().get(1)).build();


        return localizeQuestion(getLanguageSecure(redisChat), questionRepo.getQuestionById(17l)); // Sorğunuz dayandırılmışdır. Yeni sorğu başlamaq üçün /start yazın



        // also we need to remove it from active session list if form finished

    }


    private boolean validateAnswer(Long stage, String answer){

        /**
         stage -1 dirse, hal hazirda dil gelmisdir,
         butun languagelerde Language.buttonText lere baxmaq lazimdir

         Question.questionType.typeName de sual tipleri yazilmisdir, onlar "button", "text" ve "date" olur
         button olduqda stage ile suali goturub, QuestionAnswer.locale.localeTranslations listinden .translate den
         o sozleri validate etmelisen

         text olduqda validate etmirik

         date olduqda gün.Ay.İl formasında qəbul edirik
         */

        Long lastid = getLastId();

        if (stage == -1){
            List<Language> lList = languageRepo.getLanguages();
            for (Language language: lList){
                if (language.getButtonText().equals(answer))
                    return true;
            }
            return false;

        }
        else if (stage>0 && stage <= lastid)
        {
            Question question = questionRepo.getQuestionById(stage);
            System.out.println("Question stage: " + stage);
            if (question.getQuestionType().getTypeName().equals("button")){

                for (QuestionsAnswer questionsAnswer:question.getAnswers()){
                    for (LocaleTranslations localeTranslations:questionsAnswer.getLocale().getLocaleTranslations()){
                        if(localeTranslations.getTranslate().equals(answer)){
                            return true;
                        }
                    }
                }
                return false;
            } else if (question.getQuestionType().getTypeName().equals("date")) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                format.setLenient(false);
                try {
                    format.parse(answer);
                } catch (ParseException e) {
                    return false;
                }
                return true;
            }

        } else if (stage > lastid){
            return false;
        }

        return true;


    }

    public void setNumber(Long chatId, Long userId, String number){

        RedisChat r = redis.getRedis(chatId);

        r.getUser().setPhoneNumber(number);

        redis.updateRedis(r);

        userRepo.addNumberToUser(userId, chatId, number);


    }

    public void operatorOffer(Long chatId, OperatorAnswer answer){ // operatordan teklif geldikde cagirilir

        RedisChat redisChat = redis.getRedis(chatId);

//        List<QuestionsAnswer> answerList = new ArrayList<>();
//
//        answerList.add(QuestionsAnswer.builder().context("Qəbul etmirəm").build());
//        answerList.add(QuestionsAnswer.builder().context("Maraqlandırır").build());

//        Question q = Question.builder().context("Sizin sorğunuza uyğun təklif verilmişdir- \n" + answer.getAnswer()).answers(answerList).build();

        Question q = localizeQuestion(getLanguageSecure(redisChat), questionRepo.getQuestionById(16l)); // Sizin sorğunuza uyğun təklif verilmişdir-

        q.setContext(q.getContext() + " " + answer.getAnswer());

        myBot.sendMessageInlineKeyboard(chatId, q,1,  answer.getId().toString());
    }

    public void answerOffer(Long chatId, Long offerId, String answer){ // user teklife cavab verdikde cagirilir

        OperatorAnswer offer = mainSessionRepo.getOperatorAnswer(offerId);

        if (answer.equalsIgnoreCase("Maraqlandırır") || answer.equalsIgnoreCase("Принять") || answer.equalsIgnoreCase("accept")){
            offer.setStatus(1);
        } else {
            offer.setStatus(-1);
        }

        mainSessionRepo.saveOperatorAnswer(offer);

//        Question q = Question.builder().context("Cavabınız qeydə alındı").build();
//        myBot.sendMessage(chatId, q, true);

    }


    private Question returnMessage(String msg){
        return Question.builder().context(msg).questionType(questionTypeRepo.getQuestionTypeById(4l)).build();
    }

    public void receiveCallback(CallbackQuery query){
        String[] params = query.getData().split("[|]");

//        switch (params[0]){
//            case "1": // case 1 is answer to offer
//
//
//                answerOffer(query.getMessage().getChatId(), Long.parseLong(params[1]), params[2]);
//                break;
//        }

        // if we add other options, we will use method above

        answerOffer(query.getMessage().getChatId(), Long.parseLong(params[1]), params[2]);

    }

//    @RabbitListener(queues = "offer-queue")//bu queue dan qulaq as
//    public void handleMessage(OperatorAnswer answer){
//        log.info("************Message received..************");
//        log.info("*****************"+answer.toString());
//    }


    @RabbitListener(queues = "offer-queue")
    public void onMessage(String answer) throws JsonProcessingException {

//        System.out.println(answer); // use it to clear que -_-
        log.warn("**************"+answer);
        ObjectMapper objectMapper = new ObjectMapper();
//        OperatorAnswer operatorAnswer = objectMapper.readValue(answer, OperatorAnswer.class);
        OperatorOfferDTO offerDTO = objectMapper.readValue(answer, OperatorOfferDTO.class);

        if (offerDTO.getOffer() != null && offerDTO.getOfferId() != null && offerDTO.getChatId() != null) {

            OperatorAnswer operatorAnswer = OperatorAnswer.builder()
                    .answer(offerDTO.getOffer()).id(offerDTO.getOfferId())
                    .build();
            log.warn("************" + operatorAnswer.toString());
            operatorOffer(offerDTO.getChatId(), operatorAnswer);
        }
        else {
            System.out.println("Request error; Error: " + answer);

        }
    }

    private String getLanguageSecure(RedisChat redisChat){
        String out;

        if (redisChat != null && redisChat.getLanguage() != null){
            out = redisChat.getLanguage().getLanguage();
        }
        else out = "Az";

        return out;
    }



//    @PostConstruct
//    private void test(){
////        List<SessionReply> replies = new ArrayList<>();
////
////        replies.add(SessionReply.builder().answer("Salam").build());
////        replies.add(SessionReply.builder().answer("test").build());
////
////        MainSession ms = MainSession.builder().session(
////                Session.builder().userReplies(replies).build()
////        ).build();
////
//        mainSessionRepo = ac.getBean(MainSessionRepo.class);
//        userRepo = ac.getBean(UserRepoMain.class);
//        questionRepo = ac.getBean(QuestionRepoMain.class);
//        questionTypeRepo = ac.getBean(QuestionTypeRepoMain.class);
//        sessionRepo = ac.getBean(SessionRepoMain.class);
//
////        mainSessionRepo.saveMainSession(ms);
//
//        User u = userRepo.getUserByIdAndChatId(2l,
//                752688757l);
//
//
//        String[] answers = {null, null, "salam","yeni cavab", "bir cavab daha"};
//
//        endSession(RedisChat.builder()
//                .answerList(answers)
//                .user(u)
//                .build());
//    }


}
