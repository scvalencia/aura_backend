package controllers;

import actions.CorsComposition;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import models.Patient;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by scvalencia on 3/9/15.
 */
@CorsComposition.Cors
public class PatientController extends Controller {

    @BodyParser.Of(BodyParser.Json.class)
    public static Result create() throws Exception {
        JsonNode j = Controller.request().body().asJson();
        Patient p = Patient.bind(j);
        p.save();
        return ok(Json.toJson(p));
    }

    public static Result read(long id) {
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);
        ObjectNode result = Json.newObject();
        if(p == null)
            return ok(Json.toJson(result));
        else
            return ok(Json.toJson(p));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result update(long id) {
        JsonNode j = Controller.request().body().asJson();
        Patient oldPatient = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);
        Patient newPatient = Patient.bind(j);
        ObjectNode result = Json.newObject();
        if(oldPatient == null)
            return ok(Json.toJson(result));
        else {
            oldPatient.updatePatient(newPatient);
            oldPatient.save();
            return ok(Json.toJson(oldPatient));
        }
    }

    public static Result delete(long id) {
        Patient Patient = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);
        ObjectNode result = Json.newObject();
        if(Patient == null)
            return ok(Json.toJson(result));
        else {
            Patient.delete();
            return ok(Json.toJson(Patient));
        }
    }

    public static Result get() {
        List<Patient> Patients = new Model.Finder(String.class, Patient.class).all();
        return ok(Json.toJson(Patients));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result createEpisode(long id) {
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);
        JsonNode j = Controller.request().body().asJson();
        ObjectNode result = Json.newObject();
        if(p == null)
            return ok(Json.toJson(result));
        else {
            Episode e = Episode.bind(j);
            JsonNode symptoms = j.findValues("symptoms").get(0);
            JsonNode foods = j.findValues("foods").get(0);
            JsonNode medicines = j.findValues("medicines").get(0);
            JsonNode sports = j.findValues("sports").get(0);

            for(JsonNode symptom : symptoms) {
                Symptom symptomObject = Symptom.bind(symptom);
                e.addSymptom(symptomObject);
            }

            for(JsonNode food : foods) {
                Food foodObject = Food.bind(food);
                e.addFood(foodObject);
            }

            for(JsonNode medicine : medicines) {
                Medicine medicneObject = Medicine.bind(medicine);
                e.addMedicine(medicneObject);
            }

            for(JsonNode sport : sports) {
                Sport sportObject = Sport.bind(sport);
                e.addSport(sportObject);
            }

            p.addEpisode(e);
            p.save();

            return Results.created(Json.toJson(e.getNotification()));
        }
    }

    public static Result getEpisodes(long id) {
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);
        ObjectNode result = Json.newObject();
        if(p == null)
            return ok(Json.toJson(result));
        else {
            List<Episode> es = p.getEpisodes();
            return ok(Json.toJson(es));
        }
    }

    public static Result getEpisodesInRange(long id, String f1, String f2) {
        Date date1, date2;
        date1 = parseDate(f1);
        date2 = parseDate(f2);
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);
        if(p == null) {
            ObjectNode result = Json.newObject();
            return ok(Json.toJson(result));
        }
        List<Episode> es = p.getEpisodes();
        List<Episode> ans = new ArrayList<Episode>();
        for(Episode e : es)
                if(e.getPubDate().after(date1) && e.getPubDate().before(date2))
                    ans.add(e);
        return ok(Json.toJson(ans));
    }

    public static Result getEpisode(long id1, long id2) {
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(id1);
        ObjectNode result = Json.newObject();
        if(p == null)
            return ok(Json.toJson(result));
        else {
            Episode e = (Episode) new Model.Finder(Long.class, Episode.class).byId(id2);
            if(e == null)
                return ok(Json.toJson(result));
            else
                return ok(Json.toJson(e));
        }
    }

    private static Date parseDate(String representation) {
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("yyyy-MM-dd");

        Date fecha = null;

        try {
            fecha = formatoDelTexto.parse(representation);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return fecha;
    }

    public static List<Episode> episodesInRange(long id, String f1, String f2) {
        Date date1, date2;
        date1 = parseDate(f1);
        date2 = parseDate(f2);
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);
        List<Episode> es = p.getEpisodes();
        List<Episode> ans = new ArrayList<Episode>();
        for(Episode e : es)
            if(e.getPubDate().after(date1) && e.getPubDate().before(date2))
                ans.add(e);
        return ans;
    }

    public static Result getAnalysisSleepHpurs(Long idP, String f1, String f2){
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(idP);
        List<Analysis> fin = new ArrayList<Analysis>();

        if(p == null) {
            ObjectNode result = Json.newObject();
            return ok(Json.toJson(result));
        }
        else {

            List<Episode> es = p.getEpisodes();
            List<Episode> ans = new ArrayList<Episode>();

            Date d1 = parseDate(f1), d2 = parseDate(f2);

            for(Episode e : es) {
                if(e.getPubDate().after(d1) && e.getPubDate().before(d2))
                    ans.add(e);
            }

            for(Episode e : ans) {
                int intensity = e.getIntensity();
                int hours = e.getSllepHours();
                Date date = e.getPubDate();
                Analysis temp = new Analysis(intensity, date, hours);
                fin.add(temp);
            }
        }

        return ok(Json.toJson(fin));
    }

    public static Result getAnalysisMedicines(Long idP, String f1, String f2) {
        return TODO;
    }

    public static Result getAnalysisFood(Long idP, String f1, String f2) {
        return TODO;
    }

    public static Result getAnalysisSports(Long idP, String f1, String f2) {
        return TODO;
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result authenticate() {
        JsonNode j = Controller.request().body().asJson();
        ObjectNode result = Json.newObject();
        String password = j.path("password").asText();
        Long id = j.path("id").asLong();

        Patient patientObject = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);
        if(patientObject == null) {
            return ok(Json.toJson(result));
        }
        boolean authentication = Patient.checkPassword(password, patientObject.getPassword());

        if(authentication) {
            return ok(Json.toJson(patientObject));
        }
        return ok(Json.toJson(result));
    }

    public static Result plainPatient(Long id) {
        Patient patientObject = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);
        ObjectNode result = Json.newObject();

        if(patientObject == null)
            return ok(Json.toJson(result));

        try {
            ObjectNode e = patientObject.plainUnbind();
            return ok(Json.toJson(e));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ok(Json.toJson(result));
    }
}