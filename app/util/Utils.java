package util;

import akka.util.Crypt;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import enums.NotificationStatus;
import models.Notification;
import play.data.DynamicForm;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Singleton;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public Utils(){}

    public Notification notificationError(){
        Notification notification = new Notification();
        notification.setStatus(NotificationStatus.WARNING);
        notification.setMessage("Ops! Erro no servidor. Tente novamente!");
        return notification;
    }

    public String createToken() {
        return UUID.randomUUID().toString();
    }

    public String safePassword(String password) {
        if(password == null || password.isEmpty()) return "";
        return Crypt.sha1(password);
    }

    public String getValueFromJson(JsonNode json, String key){
        try {
            if (json != null && json.get(key) != null)
                return json.get(key).asText();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public Notification notification(NotificationStatus status, String str) {
        Notification notification = new Notification();
        notification.setStatus(status);
        notification.setMessage(str);
        return notification;
    }

    public String removerAcentos(String str) {
        return Normalizer.normalize(str, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
    }

    public String treatStringUrl(String url){
        return removerAcentos(url.toLowerCase()
                .replace("-", "")
                .replace(" ", "-")
                .replace("--", "-")
                .replace("&", "e")
                .replace("$", "s")
                .replace("@", "a")
                .replace("#", "")
                .replace("'", "")
                .replace(".", ""));
    }

    public JsonNode parser(DynamicForm dynamicForm, Http.Request request){
        JsonNode json = JsonNodeFactory.instance.objectNode();

        try {
            Set<String> keys = request.body().asMultipartFormData().asFormUrlEncoded().keySet();

            for (String key : keys) {
                ((ObjectNode) json).put(key, dynamicForm.field(key).value());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return json;
    }

    public Double getValueDouble(String str){
        try{
            return Double.valueOf(str);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Integer getValueInt(String str){
        try{
            return Integer.valueOf(str);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public Result valid(Object object, String entity){
        if(object == null){
            return badRequest(Json.toJson(notification(NotificationStatus.WARNING,
                    entity + " não encontrado (a)!")));
        }
        return null;
    }

    public Object mapGetValue(Map<String, Object> map, String key){
        if(map.containsKey(key))
            return map.get(key);
        return null;
    }

    public Result ok(JsonNode jsonNode) {
        return play.mvc.Results.ok(jsonNode).withHeader(HeaderKey.ACCESS_CONTROL_ALLOW_ORIGIN, HeaderKey.ORIGINS);
    }

    public Result badRequest(JsonNode jsonNode) {
        return play.mvc.Results.badRequest(jsonNode).withHeader(HeaderKey.ACCESS_CONTROL_ALLOW_ORIGIN, HeaderKey.ORIGINS);
    }

    public boolean isCep(String cep) {
        return cep.matches("\\d{5}-\\d{3}");
    }

    public boolean isTelephone(String numeroTelefone) {
        return numeroTelefone.matches("\\(\\d{2}\\) \\d{5}-\\d{4}") || numeroTelefone.matches("\\(\\d{2}\\) \\d{4}-\\d{4}");
    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public static boolean isEmail(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }

    public void putValue(JsonNode json, String key, String value){
        ((ObjectNode) json).put(key, value);
    }

    public Date getDateFrom(String date){
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        df.setLenient(false);
        try {
            return df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
