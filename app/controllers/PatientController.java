package controllers;

import actions.CorsComposition;
import actions.HttpsController;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlQuery;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stormpath.sdk.resource.ResourceException;
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
import security.AuraAuthManager;
import security.StormClau;
import security.Stormpath;
import views.html.unauthorizedAccess;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.stormpath.sdk.account.*;
import com.stormpath.sdk.application.*;
import com.stormpath.sdk.client.Client;

/**
 * Created by scvalencia on 3/9/15.
 */
@CorsComposition.Cors
public class PatientController extends Controller {

    private static SecureRandom random = new SecureRandom();
    private static AuraAuthManager auth = new AuraAuthManager("CAESAR_CIPHER");
    //private static Stormpath stormpath = Stormpath.getInstance();
    //private static Client client = stormpath.getClient();
    //private static Application application = stormpath.getApplication();
    private static StormClau sc = new StormClau();

    @BodyParser.Of(BodyParser.Json.class)
    public static Result create(Long id) throws Exception {

        JsonNode j = Controller.request().body().asJson();
        Doctor doctorObject = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(id);

        if(doctorObject == null) {
            ObjectNode result = Json.newObject();
            return ok(Json.toJson(result));
        }

        Patient p = Patient.bind(j);

        try {

            /*

            Account account = client.instantiate(Account.class);
            String[] fullName = p.getName().split(" ");
            String lastName = fullName[fullName.length-1];
            String name = "";

            for(int i = 0; i < fullName.length-1; i++)
                name += fullName[i]+" ";

            //Set the account properties
            account.setGivenName(name.trim());
            account.setSurname(lastName);
            account.setUsername(p.getId() + ""); //optional, defaults to email if unset
            account.setEmail(p.getEmail());
            account.setPassword(p.getPassword());
            application.createAccount(account);

            boolean added = stormpath.addPatientToGroup(account);

            if (!added) {
                throw new Exception("No se pudo agregar a grupo");
            }
            */

            p.save();

        } catch (ResourceException ex) {
            return badRequest(ex.getMessage());
        }

        sc.registerPatient(p.getId());
        sc.registerDoctorForPatient(p.getId(), doctorObject.getId());

        return ok(Json.toJson(p.cleverMute()));
    }

    public static Result read(long id) throws Exception {
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);
        ObjectNode result = Json.newObject();
        if(p == null)
            return ok(Json.toJson(result));
        else {
            String who = request().getHeader("who");
            String requestId = request().getHeader("id");
            String token = request().getHeader("auth-token");
            String authToken = auth.auraDecrypt(auth.auraDecrypt(token));
            if(who.equals("DOC") || sc.patientHasDoctor(Long.parseLong(requestId), p.getId())) {
                Doctor doc = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(Long.parseLong(requestId));
                if(doc != null) {
                    if(authToken.equals(doc.getToken())) {
                        return ok(Json.toJson(p.cleverMute()));
                    }
                } else {
                    return ok(unauthorizedAccess.render(""));
                }
            }
            else {
                if(requestId.equals(id + "")) {
                    if(authToken.equals(p.getToken())) {
                        return ok(Json.toJson(p.cleverMute()));
                    } else {
                        return ok(unauthorizedAccess.render(""));
                    }
                }

            }


        }

        return ok(unauthorizedAccess.render(""));
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
            String token = request().getHeader("auth-token");
            String authToken = auth.auraDecrypt(auth.auraDecrypt(token));
            if(authToken.equals(oldPatient.getToken())) {
                oldPatient.updatePatient(newPatient);
                oldPatient.save();
                return ok(Json.toJson(oldPatient.cleverMute()));
            }
        }
        return ok("El paciente no existe");
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
        String who = request().getHeader("who");
        String requestId = request().getHeader("id");
        String token = request().getHeader("auth-token");
        String authToken = auth.auraDecrypt(auth.auraDecrypt(token));

        if(who.equals("DOC")) {
            Doctor doc = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(Long.parseLong(requestId));
            if(doc != null) {
                if(authToken.equals(doc.getToken())) {
                    List<Patient> Patients = new Model.Finder(String.class, Patient.class).all();
                    return ok(Json.toJson(Patients));
                }
            } else {
                return ok(unauthorizedAccess.render(""));
            }
        }
        return ok(unauthorizedAccess.render(""));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result createEpisode(long id) {
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);
        JsonNode j = Controller.request().body().asJson();
        ObjectNode result = Json.newObject();
        if(p == null)
            return ok(Json.toJson(result));
        else {
            String token = request().getHeader("auth-token");
            String authToken = auth.auraDecrypt(auth.auraDecrypt(token));

            if(authToken.equals(p.getToken())) {
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
            } else {
                return ok(unauthorizedAccess.render(""));
            }

        }
    }


    public static Result getEpisodes(long id) throws JSONException {
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);
        ObjectNode result = Json.newObject();
        if(p == null)
            return ok(Json.toJson(result));
        else {
            String who = request().getHeader("who");
            String requestId = request().getHeader("id");
            String token = request().getHeader("auth-token");
            String authToken = auth.auraDecrypt(auth.auraDecrypt(token));

            if(who.equals("DOC") && sc.patientHasDoctor(Long.parseLong(requestId), p.getId())) {
                Doctor doc = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(requestId);
                if(doc != null) {
                    if(authToken.equals(doc.getToken())) {
                        List<Episode> es = p.getEpisodes();
                        return ok(Json.toJson(es));
                    }
                } else {
                    return ok(unauthorizedAccess.render(""));
                }
            }
            else {
                if(requestId.equals(id + "")) {
                    if(authToken.equals(p.getToken())) {
                        List<Episode> es = p.getEpisodes();
                        return ok(Json.toJson(es));
                    } else {
                        return ok(unauthorizedAccess.render(""));
                    }
                }

            }
        }
        return ok(Json.toJson(result));
    }

    public static Result getEpisodesInRange(long id, String f1, String f2) throws JSONException {
        Date date1, date2;
        date1 = parseDate(f1);
        date2 = parseDate(f2);
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);
        if(p == null) {
            ObjectNode result = Json.newObject();
            return ok(Json.toJson(result));
        }
        else {

            List<Episode> es = p.getEpisodes();
            List<Episode> ans = new ArrayList<Episode>();
            for(Episode e : es)
                    if(e.getPubDate().after(date1) && e.getPubDate().before(date2))
                        ans.add(e);

            String who = request().getHeader("who");
            String requestId = request().getHeader("id");
            String token = request().getHeader("auth-token");
            String authToken = auth.auraDecrypt(auth.auraDecrypt(token));

            if(who.equals("DOC") && sc.patientHasDoctor(Long.parseLong(requestId), p.getId())) {
                Doctor doc = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(requestId);
                if(doc != null) {
                    if(authToken.equals(doc.getToken())) {
                        return ok(Json.toJson(ans));
                    }
                } else {
                    return ok(unauthorizedAccess.render(""));
                }
            }
            else {
                if(requestId.equals(id + "")) {
                    if(authToken.equals(p.getToken())) {
                        return ok(Json.toJson(ans));
                    } else {
                        return ok(unauthorizedAccess.render(""));
                    }
                }

            }

        }
        return ok(unauthorizedAccess.render(""));
    }

    public static Result getEpisode(long id1, long id2) throws Exception {
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(id1);
        ObjectNode result = Json.newObject();
        if(p == null)
            return ok(Json.toJson(result));
        else {
            String who = request().getHeader("who");
            String requestId = request().getHeader("id");
            String token = request().getHeader("auth-token");
            String authToken = auth.auraDecrypt(auth.auraDecrypt(token));

            if(who.equals("DOC") && sc.patientHasDoctor(Long.parseLong(requestId), p.getId())) {
                Doctor doc = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(requestId);
                if(doc != null) {
                    if(authToken.equals(doc.getToken())) {
                        Episode e = (Episode) new Model.Finder(Long.class, Episode.class).byId(id2);
                        if(e == null)
                            return ok(Json.toJson(result));
                        else {
                            if(e.getVoiceEpisode() == null) {
                                return ok(Json.toJson(e.plainUnbind()));
                            }
                            return ok(Json.toJson(e));
                        }
                    }
                } else {
                    return ok("AUTH ERROR");
                }
            }
            else {
                if(requestId.equals(p.getId() + "")) {
                    if(authToken.equals(p.getToken())) {
                        Episode e = (Episode) new Model.Finder(Long.class, Episode.class).byId(id2);
                        if(e == null)
                            return ok(Json.toJson(result));
                        else {
                            if(e.getVoiceEpisode() == null) {
                                return ok(Json.toJson(e.plainUnbind()));
                            }
                            return ok(Json.toJson(e));
                        }
                    } else {
                        return ok("AUTH ERROR");
                    }
                }

            }

        }
        return ok("AUTH ERROR");

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

        return ok(Json.toJson(fin));
    }

    public static Result getAnalysisSpot(Long idP, String f1, String f2) {
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(idP);
        HashMap<Integer, Integer> fin = new HashMap<Integer, Integer>();
        ArrayList<Analysis3> finalAns = new ArrayList<Analysis3>();

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
                if(!fin.containsKey(spot))
                    fin.put(spot, 1);
                else
                    fin.put(spot, fin.get(spot) + 1);

            }

            Iterator it = fin.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                int spot = (Integer) pair.getKey();
                int freq = (Integer) pair.getValue();
                Analysis3 add = new Analysis3(spot, freq);
                finalAns.add(add);
            }
        }

        return ok(Json.toJson(finalAns));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public static Result authenticate() {
        JsonNode j = Controller.request().body().asJson();
        ObjectNode result = Json.newObject();
        String password = j.path("password").asText();
        Long id = j.path("id").asLong();

        Patient patientObject = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);
        if(patientObject != null) {

            boolean authentication = Patient.checkPassword(password, patientObject.getPassword());

            if(authentication) {
                //Account account = stormpath.authenticate(id + "", patientObject.getPassword());
                if (true) { // account != null && account.isMemberOfGroup("Patients")
                    patientObject.setToken(new BigInteger(130, random).toString(32).toString());
                    patientObject.save();
                    response().setHeader(ETAG, auth.auraEncrypt(patientObject.getToken()));
                    return ok(Json.toJson(patientObject.cleverMute()));
                }
                else {
                    return unauthorized("Usted no es un paciente");
                }
            }
            else {
                    return unauthorized(Json.toJson(result));
            }
        }
        else {
            return ok(Json.toJson(result));
        }

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


    public static Result logout(long id) {
        Patient patientObject = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);
        if(patientObject != null) {
            String token = request().getHeader("auth-token");
            String authToken = auth.auraDecrypt(auth.auraDecrypt(token));
            if(authToken.equals(patientObject.getToken())) {
                patientObject.setToken(null);
                patientObject.save();
                return ok();
            }
            return ok("AUTH ERROR");

        }
        else
            return ok("ERROR");
    }
}

