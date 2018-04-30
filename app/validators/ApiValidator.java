package validators;

import com.fasterxml.jackson.databind.JsonNode;
import enums.NotificationStatus;
import models.Notification;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    protected void validDate(JsonNode json, String obj){
        if(json.get(obj) != null && !json.get(obj).asText().isEmpty()) {
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            df.setLenient(false);
            try {
                Date dt = df.parse(json.get(obj).asText());
            } catch (ParseException e) {
                validators.put(obj, "Data inválida!");
                notification.setStatus(NotificationStatus.ERROR);
                notification.setValidators(validators);
            }
        }
    }

    protected void validDecimal(JsonNode json, String obj){
        if(json.get(obj) != null && !json.get(obj).asText().isEmpty()) {
            try {
                BigDecimal decimal = BigDecimal.valueOf(Double.valueOf(json.get(obj).asText()));
            } catch (Exception e) {
                validators.put(obj, "Valor inválido!");
                notification.setStatus(NotificationStatus.ERROR);
                notification.setValidators(validators);
            }
        }
    }

    protected void validBoolean(JsonNode json, String obj){
        if(json.get(obj) != null && !json.get(obj).asText().isEmpty()) {
            try {
                Boolean aBoolean = Boolean.valueOf(json.get(obj).asText());
            } catch (Exception e) {
                validators.put(obj, "Falta esta informação!");
                notification.setStatus(NotificationStatus.ERROR);
                notification.setValidators(validators);
            }
        }
    }
}
