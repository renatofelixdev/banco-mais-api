package controllers;

import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

    public Result options(String path){
        return ok().withHeader("Access-Control-Allow-Origin","*")
                .withHeader("Access-Control-Allow-Headers",
                        "access-control-allow-origin, " +
                        "Origin,Accept, " +
                        "X-Requested-With, " +
                        "Content-Type, " +
                        "Access-Control-Request-Method, " +
                        "Access-Control-Request-Headers," +
                                "Authorization")
                .withHeader("Access-Control-Allow-Methods",
                        "GET, " +
                                "POST, " +
                                "PUT, " +
                                "DELETE, " +
                                "OPTIONS")
                .withHeader("Access-Control-Allow-Credentials","true");
    }

}