package controllers;


import actions.CorsComposition;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.Doctor;
import play.db.ebean.Model;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.libs.Json;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;
import java.security.SecureRandom;

@CorsComposition.Cors
public class DoctorController extends Controller {

    private static SecureRandom random = new SecureRandom();
    private static AuraAuthManager auth = new AuraAuthManager("CAESAR_CIPHER");

    @BodyParser.Of(BodyParser.Json.class)
    public static Result create() throws Exception {
        JsonNode j = Controller.request().body().asJson();
        Doctor d = Doctor.bind(j);
        Doctor doctor = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(d.getId());
        if(doctor != null)
            return ok("El doctor con número de identificación " + d.getId() + " ya existe en Aura.");
        else {
            d.setToken(new BigInteger(130, random).toString(32).toString());
            d.save();
            session().put(d.getId().toString(), auth.auraEncrypt(d.getToken()));
            return ok(Json.toJson(d.cleverMute()));
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
            return ok("AUTH ERROR");
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
            return ok("AUTH ERROR");
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
            return ok("AUTH ERROR");
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
            return ok("AUTH ERROR");
        }
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result authenticate() {
        JsonNode j = Controller.request().body().asJson();
        ObjectNode result = Json.newObject();
        String password = j.path("password").asText();
        Long id = j.path("id").asLong();

        Doctor doctorObject = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(id);
        if(doctorObject == null) {
            return ok(Json.toJson(result));
        }

        boolean authentication = Doctor.checkPassword(password, doctorObject.getPassword());

        if(authentication) {
            doctorObject.setToken(new BigInteger(130, random).toString(32).toString());
            doctorObject.save();
            response().setHeader("auth-token", auth.auraEncrypt(doctorObject.getToken()));
            session().put(id.toString(), auth.auraEncrypt(doctorObject.getToken()));
            return ok(Json.toJson(doctorObject.cleverMute()));
        }
        return ok(Json.toJson(result));
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
            return ok("AUTH ERROR");
        }
        else
            return ok("ERROR");
    }
}