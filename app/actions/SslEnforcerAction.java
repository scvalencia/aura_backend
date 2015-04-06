package actions;


import controllers.Aura;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.Play;
import play.libs.F;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.SimpleResult;

public class SslEnforcerAction  extends play.mvc.Action<SslEnforced>  {


    @Override
    public F.Promise<Result> call(Http.Context context) throws Throwable {

        Logger.info("Running ssl enforcer");

        String sslEnabled = Play.application().configuration().getString("app.ssl.enabled");
        if(!StringUtils.equals(sslEnabled, "true")) {
            return delegate.call(context);
        }

        Logger.info("X-Forwarded-Proto : {}", context.request().getHeader("X-Forwarded-Proto"));

        String protocolHeaders = context.request().getHeader("X-Forwarded-Proto");
        if(protocolHeaders != null) {
            String[] split = protocolHeaders.split(",");
            for(int i=0;i<split.length;i++) {
                if(split[i].trim().equalsIgnoreCase("https")) {
                    return delegate.call(context);
                }
            }
        }

        Controller.flash("success", "For your security we've switched to SSL");

        String target = "";
        if(configuration.response() == SslEnforcedResponse.SELF) {
            target = "https://" + context.request().host() + context.request().uri();
        }
        else {
            target = String.valueOf(Aura.index());
        }
        //if we are here then ssl is enabled and the request wasn't ssl, so reject them
        return F.Promise.pure(Controller.redirect(target));
    }
}
