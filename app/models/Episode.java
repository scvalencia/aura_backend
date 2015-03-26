package models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import play.db.ebean.Model;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by scvalencia on 3/8/15.
 */
@Entity
public class Episode extends Model {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private Long urlId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date pubDate;

    private int intensity;

    private int sleepHours;

    private boolean regularSleep;

    private int location;

    private boolean stress;

    @OneToOne
    private S3File voiceEpisode;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Symptom> symptoms;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Food> foods;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Sport> sports;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Medicine> medicines;

    public static Finder find = new Finder(Long.class, Episode.class);

    public Episode() {
    }

    public static Episode create(Long urlId, int intensity, int sllepHours, boolean regularSleep, int location, boolean stress) {
        Episode e = new Episode();
        e.urlId = urlId;
        e.pubDate = new Date();
        e.intensity = intensity;
        e.sleepHours = sllepHours;
        e.regularSleep = regularSleep;
        e.location = location;
        e.stress = stress;
        e.symptoms = new ArrayList<Symptom>();
        e.foods = new ArrayList<Food>();
        e.sports = new ArrayList<Sport>();
        e.medicines = new ArrayList<Medicine>();
        e.voiceEpisode = null;
        return e;
    }

    public Long getId() {
        return id;
    }

    public Long getUrlId() {
        return urlId;
    }

    public void setUrlId(Long urlId) {
        this.urlId = urlId;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    public Date getPubDate() {
        return pubDate;
    }

    public void setPubDate(Date pubDate) {
        this.pubDate = pubDate;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public int getSllepHours() {
        return sleepHours;
    }

    public void setSllepHours(int sllepHours) {
        this.sleepHours = sllepHours;
    }

    public boolean isRegularSleep() {
        return regularSleep;
    }

    public void setRegularSleep(boolean regularSleep) {
        this.regularSleep = regularSleep;
    }

    public int getLocation() {
        return location;
    }

    public void setLocation(int location) {
        this.location = location;
    }

    public boolean isStress() {
        return stress;
    }

    public void setStress(boolean stress) {
        this.stress = stress;
    }

    public S3File getVoiceEpisode() {
        return voiceEpisode;
    }

    public void setVoiceEpisode(S3File voiceEpisode) {
        this.voiceEpisode = voiceEpisode;
    }

    public List<Symptom> getSymptoms() {
        return symptoms;
    }

    public void addSymptom(Symptom symptom) {
        this.symptoms.add(symptom);
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void addFood(Food food) {
        this.foods.add(food);
    }

    public List<Sport> getSports() {
        return sports;
    }

    public void addSport(Sport sport) {
        this.sports.add(sport);
    }

    public List<Medicine> getMedicines() {
        return medicines;
    }

    public void addMedicine(Medicine medicine) {
        this.medicines.add(medicine);
    }

    public static Episode bind(JsonNode j) {
        Long url = j.findPath("urlId").asLong();
        int intensity = j.findPath("intensity").asInt();
        int sleep = j.findPath("sleepHours").asInt();
        boolean regular = j.findPath("regularSleep").asBoolean();
        int location = j.findPath("location").asInt();
        boolean stress = j.findPath("stress").asBoolean();
        Episode e = create(url, intensity, sleep, regular, location, stress);
        return e;
    }

    public ObjectNode plainUnbind() throws  Exception {
        JsonNode e = Json.toJson(this);
        ObjectNode o = (ObjectNode) e;
        o.remove("voiceEpisode");
        return o;
    }

    public ObjectNode getNotification() {
        String intensity, sleep, stress, message;
        if(this.intensity > 7){
            intensity = "La intensidad de su dolor es muy fuerte. Considere acudir al médico";
        }
        else if(this.intensity > 4 && this.intensity <= 7) {
            intensity = "La intensidad de su dolor es estandar. Procure reposar para evitar que empeore.";
        }
        else {
            intensity = "La intensidad de su dolor es suave.";
        }
        if(this.sleepHours > 8){
            sleep = "Sus horas de sueño son adecuadas y probablemente no sean la causa da sus migrañas";
        }
        else if(this.sleepHours > 5 && this.sleepHours <= 8) {
            sleep = "Sus horas de sueño son suficientes, pero debería considerar dormir un poco más.";
        }
        else {
            sleep = "Sus horas de sueño son muy bajas y causan que tenga migraña.";
        }
        if(this.stress) {
            stress = "El estrés produce dolores de cabeza muy fuertes, y es posible que ésto le cause migrañas";
        }
        else {
            stress = "";
        }

        message = "Debe tener en cuenta las siguientes consideraciones:";
        ObjectNode simple = Json.newObject();
        simple.put("mensaje", message);
        simple.put("intensidad", intensity);
        simple.put("suenio", sleep);
        simple.put("estres", stress);
        return simple;
    }
}
