package controllers;

import actions.CorsComposition;
import com.fasterxml.jackson.databind.JsonNode;
import models.Doctor;
import play.*;
import play.data.Form;
import play.data.Form.*;
import play.mvc.*;

import views.html.index;
import views.html.login;
import views.html.test;

@CorsComposition.Cors
public class Aura extends Controller {


    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public static Result token(String path) {
        return ok("");
    }

    public static Result login() {
        return ok(login.render(""));
    }

    public static Result test(String session) {
        return ok(
                test.render(session));
    }

}


