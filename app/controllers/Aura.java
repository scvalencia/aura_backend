package controllers;

import actions.CorsComposition;
import actions.HttpsController;
import play.mvc.*;

import views.html.*;

@CorsComposition.Cors
public class Aura extends Controller {


    public static Result index() {
        java.util.Set<String> tokenP = session().keySet();

        if(tokenP.isEmpty()) {
            return ok(login.render(""));
        }
        else {
            java.util.List<String> tokenId=new java.util.ArrayList<String>(tokenP);
            String id = tokenId.get(0);
            String token = session().get(id);
            return ok(search.render(id+"--TOKEN--"+token));
        }

    }

    public static Result unauthorizedAccess() {
        return ok();
    }

    public static Result token(String path) {
        return ok("");
    }

    public static Result login() {
        return ok(login.render(""));
    }

    public static Result info() {
        java.util.Set<String> tokenP = session().keySet();

        if(tokenP.isEmpty()) {
            return ok(login.render(""));
        }
        else {
            java.util.List<String> tokenId=new java.util.ArrayList<String>(tokenP);
            String id = tokenId.get(0);
            String token = session().get(id);
            return ok(info.render(id+"--TOKEN--"+token));
        }
    }

}