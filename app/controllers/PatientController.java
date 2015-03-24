package controllers;

import actions.CorsComposition;
import com.amazonaws.util.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import models.Patient;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import play.db.ebean.Model;
import play.libs.Json;
import play.mvc.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    public static Result getAnalysisSleepHours(Long idP, String f1, String f2){
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

    public static Result getAnalysisIntensity(Long idP, String f1, String f2) {
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(idP);
        HashMap<String, HashMap<Integer, Integer>> fin = new HashMap<String, HashMap<Integer, Integer>>();

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
                Date date = e.getPubDate();
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                String key = cal.get(Calendar.YEAR) + " " + cal.get(Calendar.MONTH);
                int intensity = e.getIntensity();
                if(!fin.containsKey(key)) {
                    HashMap<Integer, Integer> val = new HashMap<Integer, Integer>();
                    val.put(intensity, 1);
                    fin.put(key, val);
                }
                else {
                    HashMap<Integer, Integer> value = fin.get(key);
                    if(!value.containsKey(intensity)) {
                        value.put(intensity, 1);
                    }
                    else {
                        value.put(intensity, value.get(intensity) + 1);
                    }
                }
            }
        }

        

        /*
            {'año' : 2015, 'mes' : 02, 'frecuencias' : [{'intensidad' : 4}, {'frecuancia' : 5}, ... ]
         */

        return ok(Json.toJson(fin));
    }

    public static Result getAnalysisSpot(Long idP, String f1, String f2) {
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(idP);
        HashMap<Integer, Integer> fin = new HashMap<Integer, Integer>();

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
                int spot = e.getLocation();

            }
        }

        return ok(Json.toJson(fin));
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

    public static Result createVoiceEpisode(long id) {

        Patient patientObject = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);

        ObjectNode result = Json.newObject();

        if(patientObject != null) {
            Http.MultipartFormData body = request().body().asMultipartFormData();
            Http.MultipartFormData.FilePart uploadFilePartBody = body.getFile("upload");
            if(uploadFilePartBody == null)
                return ok(Json.toJson(result));

            String url = "http://aura-voice.herokuapp.com/upload/" + id;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);

            FileBody uploadFilePart = new FileBody(uploadFilePartBody.getFile());
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addPart("upload", uploadFilePart);
            httpPost.setEntity(builder.build());

            S3File s3File = new S3File();
            s3File.name = uploadFilePartBody.getFilename();
            s3File.file = uploadFilePartBody.getFile();
            s3File.save();

            try {
                HttpResponse response = httpclient.execute(httpPost);
                String json = EntityUtils.toString(response.getEntity());
                JSONObject j = new JSONObject(json);
                Episode episode = new Episode();

                episode.setUrlId(Long.parseLong(j.get("url").toString()));
                episode.setIntensity(10);
                episode.setVoiceEpisode(s3File);
                episode.setPubDate(new Date());

                patientObject.addEpisode(episode);
                patientObject.save();

                return ok(json);
            } catch(Exception e) {
                return badRequest("File upload error");
            }
        }
        return badRequest("File upload error");
    }
}