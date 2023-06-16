package az.code.telegrambot.entity;

import lombok.Data;

@Data
public class OperatorOfferDTO {

    Long offerId;

    Long chatId;

    String offer;
}
