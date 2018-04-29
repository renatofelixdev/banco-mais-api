package validators;

import com.fasterxml.jackson.databind.JsonNode;
import enums.NotificationStatus;
import models.Notification;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class ApiValidator {
    protected Notification notification = new Notification();
    protected Map<String, String> validators = new LinkedHashMap<String, String>();
    public String ENTITY = "";


    public Notification hasErrors(JsonNode json) {
        notification.setStatus(NotificationStatus.SUCCESS);
        validators.clear();
        validators(json);
        if(notification.getStatus() == NotificationStatus.ERROR)
            notification.setMessage("Há erros no formulário, verifique os dados e tente novamente!");
        return notification;
    }

    protected void validators(JsonNode json){
        nameHasError(json);
    }

    protected void nameHasError(JsonNode json){
        this.requiredField(json, "name");
    }

    protected void requiredField(JsonNode json, String obj){
        if(json.get(obj) == null || json.get(obj).asText().isEmpty()){
            validators.put(obj, "Digite o nome do (a) "+ ENTITY);
            notification.setStatus(NotificationStatus.ERROR);
            notification.setValidators(validators);
        }
    }

    protected void validInt(JsonNode json, String obj, String entity){
        if(json.get(obj) != null && !json.get(obj).asText().isEmpty()){
            try{
                Integer i = Integer.parseInt(json.get(obj).asText());
            }catch (Exception e){
                validators.put(obj, "Selecione um(a) "+entity+"!");
                notification.setStatus(NotificationStatus.ERROR);
                notification.setValidators(validators);
            }

        }
    }
}
