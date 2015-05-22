package controllers;


import actions.CorsComposition;
import actions.HttpsController;
import com.amazonaws.util.json.JSONException;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;
import com.avaje.ebean.RawSql;
import com.avaje.ebean.RawSqlBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.resource.ResourceException;
import com.sun.org.apache.xpath.internal.operations.Bool;
import models.Doctor;
import models.Episode;
import models.Patient;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import play.db.ebean.Model;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.libs.Json;
import play.mvc.Results;
import scala.collection.JavaConverters;
import security.AuraAuthManager;
import security.StormClau;
import security.Stormpath;
import views.html.MailBody;
import views.html.unauthorizedAccess;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import play.libs.mailer.Email;
import play.libs.mailer.Email;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

@CorsComposition.Cors
public class DoctorController extends HttpsController {

    private static SecureRandom random = new SecureRandom();
    private static AuraAuthManager auth = new AuraAuthManager("CAESAR_CIPHER");
    static Stormpath stormpath = Stormpath.getInstance();
    private static Client client = stormpath.getClient();
    private static Application application = stormpath.getApplication();
    private static StormClau sc = new StormClau();

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
                sc.registerDoctor(d.getId());
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

    public static Result getPatients(Long id) throws Exception {
        Doctor doctor = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(id);
        ObjectNode result = Json.newObject();
        if(doctor == null)
            return ok(Json.toJson(result));
        List<Long> ans = sc.getPatientsByDoctor(id);
        List<Patient> ps = new ArrayList<Patient>();
        for(Long itm : ans) {
            ps.add((Patient) new Model.Finder(Long.class, Patient.class).byId(itm));
        }

        return ok(Json.toJson(ps));
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
                if (account != null && account.isMemberOfGroup("Doctors")) { // account != null && account.isMemberOfGroup("Doctors")
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

    public static Result notificate(Long doctor, Long doctor2) throws EmailException {

        Doctor doctorFrom = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(doctor);
        Doctor doctorTo = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(doctor2);

        if(doctorFrom != null && doctorTo != null) {

            JsonNode j = Controller.request().body().asJson();
            List<Patient> ans = new ArrayList<Patient>();

            for(Object o : j.findPath("patients")) {
                Long patientId = Long.parseLong(o.toString());
                Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(patientId);

                if(p != null) {
                    ans.add(p);
                    sc.registerDoctorForPatient(p.getId(), doctorTo.getId());
                }
            }

            String host = "smtp.gmail.com";
            String port = "587";
            String mailFrom = "sc.valencia606@gmail.com";
            String password = "camila123";

            // outgoing message information
            String mailTo = doctorTo.getEmail();
            String subject = "Aura notification";

            // message contains HTML markups
            String body = views.html.MailBody.render(doctorFrom, doctorTo, ans).body();

            try {
                sendHtmlEmail(host, port, mailFrom, password, mailTo,
                        subject, body);
                System.out.println("Email sent.");
                return ok("Email enviado");
            } catch (Exception ex) {
                System.out.println("Failed to sent email.");
                ex.printStackTrace();
                return ok("Error");
            }
        }
        return Results.ok("Doctores no encontrados");
    }

    // ====================================================================================================================================================

    public static Result sortEpisodesByIntensity(Long idP, Long idD) {
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(idP);

        if(p == null) {
            ObjectNode result = Json.newObject();
            return ok(Json.toJson(result));
        }

        else {
            List<Episode> episodes = p.getEpisodes();
            episodes = episodes.stream().sorted((e1, e2) -> Integer.compare(e1.getIntensity(),e2.getIntensity())).collect(Collectors.toList());
            return ok(Json.toJson(episodes));
        }
    }

    public static void sendHtmlEmail(String host, String port,
                                     final String userName, final String password, String toAddress,
                                     String subject, String message) throws AddressException,
            MessagingException {

        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // creates a new session with an authenticator
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        });

        // creates a new e-mail message
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(userName));
        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        // set plain text message
        msg.setContent(message, "text/html");

        // sends the e-mail
        Transport.send(msg);

    }

    public static Result addPatient(long idD, long idP) {
        Doctor doctorObject = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(idD);
        Patient p = (Patient) new Model.Finder(Long.class, Patient.class).byId(idP);

        if(doctorObject == null)
            return ok("El doctor no existe");

        if(p == null)
                return ok("El paciente no existe");

        sc.registerDoctorForPatient(idP, idD);
        return ok();
    }

    public static Result filter(Long doctor, Long id) throws JSONException {
        Doctor doctorObject = (Doctor) new Model.Finder(Long.class, Doctor.class).byId(doctor);
        JsonNode j = Controller.request().body().asJson();

        System.out.println(j);

        if(doctorObject != null) {
            Patient patientObject = (Patient) new Model.Finder(Long.class, Patient.class).byId(id);

            int intensity = j.findPath("intensity").asInt();
            int timeslept = j.findPath("timeslept").asInt();
            int stress = j.findPath("stress").asInt();
            int symptom = j.findPath("symptom").asInt();
            int place = j.findPath("place").asInt();

            boolean realStress = stress == 1 ? Boolean.TRUE : Boolean.FALSE;

            List<Episode> es = new ArrayList<Episode>();

            if(intensity == -1 && timeslept == -1 && stress == -1 && symptom == -1 && place == -1)
                return ok(Json.toJson(es));

            else if(intensity == -1 && timeslept == -1 && stress == -1 && symptom == -1) {
                es = Episode.find.where().eq("location", place).findList();
            }

            else if(intensity == -1 && timeslept == -1 && stress == -1) {
                es = Episode.find.where().eq("location", place).eq("symptoms.symptom", symptom).findList();
            }

            else if(intensity == -1 && timeslept == -1) {
                es = Episode.find.where().eq("location", place).eq("symptoms.symptom", symptom).eq("stress", realStress).findList();
            }

            else if(intensity == -1) {
                es = Episode.find.where().eq("location", place).eq("symptoms.symptom", symptom).
                        eq("stress", realStress).le("sleepHours", timeslept).findList();
            }

            else {
                es = Episode.find.where().ge("intensity", intensity).findList();

            }

            /*
            eq("episodes.intensity", intensity)
                                            .eq("episodes.sleepHours", timeslept)
                                                .eq("episodes.stress", stress == 1 ? Boolean.TRUE : Boolean.FALSE)
                                                    .eq("episodes.symptoms.symptom", symptom).eq("episodes.location", place).findList();
             */

            List<Episode> ans = new ArrayList<Episode>();

            for(int i = 0; i < es.size(); i++)
                if(patientObject.getEpisodes().contains(es.get(i)))
                    ans.add(es.get(i));

            return ok(Json.toJson(ans));
        }

        return ok();

    }
}
