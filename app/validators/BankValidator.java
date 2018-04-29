package validators;

import com.fasterxml.jackson.databind.JsonNode;
import dao.ApiDAO;
import util.NameEntity;

public class BankValidator extends ApiValidator {
    public BankValidator(){
        super.ENTITY = NameEntity.BANK;
    }

    @Override
    protected void validators(JsonNode json) {
        super.validators(json);
        super.requiredField(json, "code");
    }
}
