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

import java.util.List;

@CorsComposition.Cors
public class DoctorController extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result create() throws Exception {
        JsonNode j = Controller.request().body().asJson();
        Doctor d = Doctor.bind(j);
        d.save();
        return ok(Json.toJson(d));
    }

    public static Result read(long id) {
        Doctor doctor = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(id);
        ObjectNode result = Json.newObject();
        if(doctor == null)
            return ok(Json.toJson(result));
        else
            return ok(Json.toJson(doctor));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result update(long id) {
        JsonNode j = Controller.request().body().asJson();
        Doctor oldDoctor = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(id);
        Doctor newDoctor = Doctor.bind(j);
        ObjectNode result = Json.newObject();
        if(oldDoctor == null)
            return ok(Json.toJson(result));
        else {
            oldDoctor.updateDoctor(newDoctor);
            oldDoctor.save();
            return ok(Json.toJson(oldDoctor));
        }
    }

    public static Result delete(long id) {
        Doctor doctor = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(id);
        ObjectNode result = Json.newObject();
        if(doctor == null)
            return ok(Json.toJson(result));
        else {
            doctor.delete();
            return ok(Json.toJson(doctor));
        }
    }

    public static Result get() {
        List<Doctor> doctors = new Model.Finder(String.class, Doctor.class).all();
        return ok(Json.toJson(doctors));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result addLink(long id) {
        JsonNode j = Controller.request().body().asJson();
        Doctor doctor = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(id);
        ObjectNode result = Json.newObject();
        if(doctor == null)
            return ok(Json.toJson(result));
        else {
            String link = Doctor.bindLink(j);
            doctor.setLink(link);
            doctor.save();
            return ok(Json.toJson(doctor));
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
            return ok(Json.toJson(doctorObject));
        }
        return ok(Json.toJson(result));
    }
}