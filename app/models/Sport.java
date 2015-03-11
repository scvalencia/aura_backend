package models;

import com.fasterxml.jackson.databind.JsonNode;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by scvalencia on 3/8/15.
 */
@Entity
public class Sport extends Model {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private int description;

    private int intensity;

    private int place;

    private int climate;

    private boolean hydration;

    public Sport() {
    }

    public static Sport create(int description, int intensity, int place, int climate, boolean hydration) {
        Sport s = new Sport();
        s.description = description;
        s.intensity = intensity;
        s.place = place;
        s.climate = climate;
        s.hydration = hydration;
        return s;
    }

    public Long getId() {
        return id;
    }

    public int getDescription() {
        return description;
    }

    public void setDescription(int description) {
        this.description = description;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public int getPlace() {
        return place;
    }

    public void setPlace(int place) {
        this.place = place;
    }

    public int getClimate() {
        return climate;
    }

    public void setClimate(int climate) {
        this.climate = climate;
    }

    public boolean isHydration() {
        return hydration;
    }

    public void setHydration(boolean hydration) {
        this.hydration = hydration;
    }

    public static Sport bind(JsonNode j) {
        int desc = j.findPath("description").asInt();
        int intensity = j.findPath("intensity").asInt();
        int place = j.findPath("place").asInt();
        int climate = j.findPath("climate").asInt();
        boolean hydration = j.findPath("hydration").asBoolean();
        Sport s = create(desc, intensity, place, climate, hydration);
        return s;
    }
}
