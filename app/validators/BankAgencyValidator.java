package validators;

import com.fasterxml.jackson.databind.JsonNode;
import util.NameEntity;

public class BankAgencyValidator extends ApiValidator {
    public BankAgencyValidator(){
        super.ENTITY = NameEntity.BANK_AGENCY;
    }

    @Override
    protected void validators(JsonNode json) {
        super.validators(json);
        super.requiredField(json, "code");
    }
}
