package validators;

import com.fasterxml.jackson.databind.JsonNode;
import dao.UserClientDAO;
import enums.NotificationStatus;
import models.UserClient;
import util.CPF;
import util.NameEntity;

import javax.inject.Inject;


public class UserClientValidator extends ApiValidator{


    public UserClientValidator(){
        super.ENTITY = NameEntity.USER_CLIENT;
    }

    @Override
    protected void validators(JsonNode json) {
        super.requiredField(json, "name");
        super.requiredField(json, "cpf");
        super.requiredField(json, "address");
        validCpf(json, "cpf");
    }

    private void validCpf(JsonNode json, String cpf) {
        if(json.get(cpf) != null && !json.get(cpf).asText().isEmpty() && !CPF.isCPF(json.get(cpf).asText())){
            validators.put("cpf", "CPF inválido!");
            notification.setStatus(NotificationStatus.ERROR);
            notification.setValidators(validators);
        }
    }

}
