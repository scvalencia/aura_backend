package models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import play.db.ebean.Model;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by scvalencia on 3/8/15.
 */
@Entity
public class Patient extends Model {

    private static final int MASCULINO = 1;

    private static final int FEMENINO = 1;

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    private String email;

    private int gender;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Episode> episodes;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Medicine> medicines;

    public static Finder find = new Finder(Long.class, Patient.class);

    public Patient() {
    }

    public static Patient create(String name, String password, Date date, String email, int gender, Long idP) {
        Patient p = new Patient();
        //p.id = idP;
        p.name = name;
        p.password = password;
        p.date = date;
        p.email = email;
        p.gender = gender;
        p.episodes = new ArrayList<Episode>();
        return p;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public List<Episode> getEpisodes() {
        return episodes;
    }

    public void addEpisode(Episode episode) {
        this.episodes.add(episode);
    }

    public List<Medicine> getMedicines() {
        return medicines;
    }

    public void addMedicine(Medicine medicine) {
        this.medicines.add(medicine);
    }

    public static Patient bind(JsonNode j) {


        Long id = Long.parseLong(j.findPath("id").asText());
        String name = j.findPath("name").asText();
        String email=j.findPath("email").asText();
        String password = j.findPath("password").asText();
        Date date = Patient.parseDate(j.findPath("date").asText());
        Integer gen = Integer.parseInt(j.findPath("gender").asText());

        Patient p = Patient.create(name, password, date, email, gen, id);
        return p;
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

    public void updatePatient(Patient newPatient) {
        this.setDate(newPatient.date);
        this.setEmail(newPatient.email);
        this.setGender(newPatient.gender);
        this.setName(newPatient.name);
        this.setPassword(newPatient.password);
    }
}
