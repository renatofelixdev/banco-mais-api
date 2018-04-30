package validators;

import com.fasterxml.jackson.databind.JsonNode;
import enums.NotificationStatus;
import models.Notification;
import util.NameEntity;

public class BankingOperationValidator extends ApiValidator{

    public BankingOperationValidator(){
        super.ENTITY = NameEntity.BANK_OPERATION;
    }

    public Notification bankStatement(JsonNode json) {
        notification.setStatus(NotificationStatus.SUCCESS);
        validators.clear();
        super.requiredField(json, "startDate");
        super.requiredField(json, "endDate");
        super.requiredField(json, "bank");
        super.requiredField(json, "agency");
        super.requiredField(json, "account");

        super.validDate(json, "startDate");
        super.validDate(json, "endDate");

        if(notification.getStatus() == NotificationStatus.ERROR)
            notification.setMessage("Há erros no formulário, verifique os dados e tente novamente!");
        return notification;
    }

    public Notification deposit(JsonNode json) {
        notification.setStatus(NotificationStatus.SUCCESS);
        validators.clear();
        super.requiredField(json, "value");
        super.requiredField(json, "bank");
        super.requiredField(json, "agency");
        super.requiredField(json, "account");

        super.validDecimal(json, "value");

        if(notification.getStatus() == NotificationStatus.ERROR)
            notification.setMessage("Há erros no formulário, verifique os dados e tente novamente!");
        return notification;
    }
}
