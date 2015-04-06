package controllers;

import actions.SslEnforced;
import play.mvc.Controller;
import play.mvc.With;

/**
 * Created by scvalencia on 3/29/15.
 */
@With(HttpsRequired.class)
@SslEnforced
public class HttpsController extends Controller {

}
