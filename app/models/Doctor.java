package models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mindrot.jbcrypt.BCrypt;
import play.db.ebean.Model;
import play.libs.Json;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class Doctor extends Model {

    private static final int MALE = 0;

    private static final int FEMALE = 1;

    private static final int NEUROLOGIST = 0;

    @Id
    private Long id;

    private int gender;

    private String name;

    private String email;

    private Date date = new Date();

    private String password;

    private int discipline;

    private String link;

    public Doctor() { }

    public static Doctor create(String emailP, String nombreP, int especialidadP, String passwordP, Long docIdentidadP, Date bDate, int genderP) {
        Doctor d  = new Doctor();
        d.id = docIdentidadP;
        d.name = nombreP;
        d.gender = genderP;
        d.email = emailP;
        d.date = bDate;
        d.password = BCrypt.hashpw(passwordP, BCrypt.gensalt());
        d.discipline= especialidadP;
        return d;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date getDate() {
        return date;
    }

    public void setDate(Date birthDate) {
        this.date = birthDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getDiscipline() {
        return discipline;
    }

    public void setDiscipline(int discipline) {
        this.discipline = discipline;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public static Doctor bind(JsonNode j) {
        Long docIdentidad = Long.parseLong(j.findPath("id").asText());
        String nombre = j.findPath("name").asText();
        String email=j.findPath("email").asText();
        String password = j.findPath("password").asText();
        Integer especialidad = Integer.parseInt(j.findPath("discipline").asText());
        Date date = Doctor.parseDate(j.findPath("date").asText());
        Integer gen = Integer.parseInt(j.findPath("gender").asText());

        Doctor d = Doctor.create(email, nombre, especialidad, password, docIdentidad, date, gen);
        return d;
    }

    public static String bindLink(JsonNode j) {
        String link = j.findPath("link").asText();
        return link;
    }

    public ObjectNode plainUnbind() throws  Exception {
        JsonNode e = Json.toJson(this);
        ObjectNode o = (ObjectNode) e;
        o.remove("password");
        return o;
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

    public void updateDoctor(Doctor newDoctor) {
        this.setName(newDoctor.name);
        this.setEmail(newDoctor.email);
        this.setPassword(newDoctor.password);
        this.setDiscipline(newDoctor.discipline);
        this.setDate(newDoctor.date);
        this.setGender(newDoctor.gender);
        this.setLink(newDoctor.link);
    }

    public static boolean checkPassword(String candidate, String encryptedPassword) {
        if (candidate == null) {
            return false;
        }
        if (encryptedPassword == null) {
            return false;
        }

        return BCrypt.checkpw(candidate, encryptedPassword);
    }
}

