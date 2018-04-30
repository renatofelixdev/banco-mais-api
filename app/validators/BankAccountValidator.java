package validators;

import com.fasterxml.jackson.databind.JsonNode;
import enums.BankAccountType;
import enums.NotificationStatus;
import util.NameEntity;

public class BankAccountValidator extends  ApiValidator{
    public BankAccountValidator(){
        super.ENTITY = NameEntity.BANK_ACCOUNT;
    }

    @Override
    protected void validators(JsonNode json) {
        super.requiredField(json, "number");
        super.requiredField(json, "balance");
        super.validDecimal(json, "balance");
        super.requiredField(json, "userClient");
        super.validInt(json, "userClient", NameEntity.USER_CLIENT);
        super.requiredField(json, "bankAccountType");
        validAccountType(json, "bankAccountType");
        super.requiredField(json, "bankAgency");
        super.validInt(json, "bankAgency", NameEntity.BANK_AGENCY);

    }

    private void validAccountType(JsonNode json, String obj) {
        if(json.get(obj) != null && !json.get(obj).asText().isEmpty()){
            if(BankAccountType.parse(obj) == null){
                validators.put(obj, "Tipo de conta inválido");
                notification.setStatus(NotificationStatus.ERROR);
                notification.setValidators(validators);
            }

        }
    }

}
