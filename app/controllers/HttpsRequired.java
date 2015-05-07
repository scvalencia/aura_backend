package controllers;

import play.Play;
import play.mvc.Controller;

/**
 * Created by scvalencia on 3/29/15.
 */

import play.Play;
import play.libs.F;
import play.mvc.*;

public class HttpsRequired extends Action<Controller> {

    // heroku header
    private static final String SSL_HEADER = "X-Forwarded-Proto";

    @Override
    public F.Promise<Result> call(Http.Context ctx) throws Throwable {

        System.out.println("PROD: " + Play.isProd());
        System.out.println("IS REQUEST: " + !isHttpsRequest(ctx.request()));
        if (Play.isProd() && !isHttpsRequest( ctx.request() )) {
            return F.Promise.promise(() -> status(403, " <a href=\"http://www.w3schools.com\">Visit W3Schools</a> "));
        }

        // let request proceed
        return this.delegate.call(ctx);
    }

    private static boolean isHttpsRequest(Http.Request request) {
        // heroku passes header on
        System.out.println("SSL HEADER: " + request.getHeader(SSL_HEADER));
        System.out.println("SSL CONTIENE: " + request.getHeader(SSL_HEADER).contains("https"));
        return request.getHeader(SSL_HEADER) != null && request.getHeader(SSL_HEADER).contains("https");
    }

}
