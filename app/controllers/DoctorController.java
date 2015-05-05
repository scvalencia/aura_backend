package controllers;


import actions.CorsComposition;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.resource.ResourceException;
import models.Doctor;
import play.db.ebean.Model;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.libs.Json;
import views.html.unauthorizedAccess;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;
import java.security.SecureRandom;

@CorsComposition.Cors
public class DoctorController extends Controller {

    private static SecureRandom random = new SecureRandom();
    private static AuraAuthManager auth = new AuraAuthManager("CAESAR_CIPHER");
    private static Stormpath stormpath = Stormpath.getInstance();
    private static Client client = stormpath.getClient();
    private static Application application = stormpath.getApplication();

    @BodyParser.Of(BodyParser.Json.class)
    public static Result create() throws Exception {
        JsonNode j = Controller.request().body().asJson();
        Doctor d = Doctor.bind(j);
        Doctor doctor = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(d.getId());
        if(doctor != null) {
            return ok("{}");
        }
        else {
            d.setToken(new BigInteger(130, random).toString(32).toString());

            try {
                Account account = client.instantiate(Account.class);
                //Set the account properties

                String[] fullName = d.getName().split(" ");
                String lastName = fullName[fullName.length-1];
                String name = "";
                System.out.println("c");
                for(int i = 0; i < fullName.length-1; i++) {
                    name += fullName[i]+" ";
                }
                //Set the account properties
                account.setGivenName(name.trim());
                account.setSurname(lastName);
                account.setUsername(d.getId() + ""); //optional, defaults to email if unset
                account.setEmail(d.getEmail());
                account.setPassword(d.getPassword());
                application.createAccount(account);
                boolean added = stormpath.addDoctorToGroup(account);

                if (!added) {
                    throw new Exception("No se pudo agregar a grupo");
                }

                d.save();
                session().put(d.getId().toString(), auth.auraEncrypt(d.getToken()));
                return ok(Json.toJson(d.cleverMute()));
            } catch (ResourceException ex) {
                System.out.println(ex.getMessage());
                System.out.println(ex.getDeveloperMessage());
                System.out.println(ex.getMoreInfo());
                System.out.println(ex.getStatus());
                System.out.println(ex.getStormpathError());
                return badRequest(ex.getMessage());
            }

        }
    }

    public static Result read(long id) {
        Doctor doctor = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(id);
        ObjectNode result = Json.newObject();
        if(doctor == null)
            return ok(Json.toJson(result));
        else {
            String token = request().getHeader("auth-token");
            String authToken = auth.auraDecrypt(auth.auraDecrypt(token));
            if(authToken.equals(doctor.getToken())) {
                return ok(Json.toJson(doctor.cleverMute()));
            }
            return ok(unauthorizedAccess.render(""));
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result update(long id) {
        JsonNode j = Controller.request().body().asJson();
        Doctor oldDoctor = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(id);
        ObjectNode result = Json.newObject();
        if(oldDoctor == null)
            return ok(Json.toJson(result));
        else {
            String token = request().getHeader("auth-token");
            String authToken = auth.auraDecrypt(auth.auraDecrypt(token));
            if(authToken.equals(oldDoctor.getToken())) {
                Doctor newDoctor = Doctor.bind(j);
                newDoctor.setLink(oldDoctor.getLink());
                oldDoctor.updateDoctor(newDoctor);
                oldDoctor.save();
                return ok(Json.toJson(oldDoctor.cleverMute()));
            }
            return ok(unauthorizedAccess.render(""));
        }
    }

    public static Result delete(long id) {
        Doctor doctor = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(id);
        ObjectNode result = Json.newObject();
        if(doctor == null)
            return ok(Json.toJson(result));
        else {
            String token = request().getHeader("auth-token");
            String authToken = auth.auraDecrypt(auth.auraDecrypt(token));
            if(authToken.equals(doctor.getToken())) {
                doctor.delete();
                return ok(Json.toJson(doctor.cleverMute()));
            }
            return ok(unauthorizedAccess.render(""));
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result addLink(long id) {
        JsonNode j = Controller.request().body().asJson();
        Doctor doctor = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(id);
        ObjectNode result = Json.newObject();
        if(doctor == null)
            return ok(Json.toJson(result));
        else {
            String token = request().getHeader("auth-token");
            String authToken = auth.auraDecrypt(auth.auraDecrypt(token));
            if(authToken.equals(doctor.getToken())) {
                String link = Doctor.bindLink(j);
                doctor.setLink(link);
                doctor.save();
                return ok(Json.toJson(doctor.cleverMute()));
            }
            return ok(unauthorizedAccess.render(""));
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result authenticate() {
        JsonNode j = Controller.request().body().asJson();
        ObjectNode result = Json.newObject();
        String password = j.path("password").asText();
        Long id = j.path("id").asLong();

        Doctor doctorObject = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(id);
        if(doctorObject != null) {
            boolean authentication = Doctor.checkPassword(password, doctorObject.getPassword());

            if(authentication) {
                Account account = stormpath.authenticate(id + "", doctorObject.getPassword());
                if (account != null && account.isMemberOfGroup("Doctors")) {
                    doctorObject.setToken(new BigInteger(130, random).toString(32).toString());
                    doctorObject.save();
                    response().setHeader(ETAG, auth.auraEncrypt(doctorObject.getToken()));
                    session().put(id.toString(), auth.auraEncrypt(doctorObject.getToken()));
                    return ok(Json.toJson(doctorObject.cleverMute()));
                }
                else {
                    return unauthorized("Usted no es un doctor");
                }
            } else {
                return unauthorized(Json.toJson(result));
            }
        }
        else {
            return ok(Json.toJson(result));
        }
    }

    public static Result logout(long id) {
        //TODO cambio, adicion siguiente linea
        session().clear();
        Doctor doctorObject = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(id);
        if(doctorObject != null) {
            String token = request().getHeader("auth-token");
            String authToken = auth.auraDecrypt(auth.auraDecrypt(token));
            if(authToken.equals(doctorObject.getToken())) {
                doctorObject.setToken(null);
                doctorObject.save();
                return ok();
            }
            return ok(unauthorizedAccess.render(""));
        }
        else
            return ok("ERROR");
    }
}
