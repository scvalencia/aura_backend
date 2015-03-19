package controllers;

import actions.CorsComposition;
import play.*;
import play.data.Form.*;
import play.mvc.*;

import views.html.*;

@CorsComposition.Cors
public class Application extends Controller {

    public static Result index() {
        return ok(index.render("Your new application is ready."));
    }

    public static Result token(String path) {
        return ok("");
    }
}


