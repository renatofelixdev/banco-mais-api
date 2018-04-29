package api;

import models.UserMaster;
import play.mvc.Controller;
import play.mvc.Result;
import util.Utils;

import javax.inject.Inject;

public class UserMasterApiController extends Controller {

    @Inject
    private Utils utils;

    public Result create(String login, String password){
        if(login != null && !login.isEmpty() && password != null && !password.isEmpty()){
            UserMaster userMaster = new UserMaster();
            userMaster.setLogin(login);
            userMaster.setPassword(utils.safePassword(password));
            userMaster.save();
            return ok("Cadastrado com sucesso!");
        }
        return badRequest("Erro ao tentar cadastrar!");
    }
}
