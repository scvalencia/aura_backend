package models;

import com.fasterxml.jackson.databind.JsonNode;
import play.db.ebean.Model;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * Created by scvalencia on 3/8/15.
 */
@Entity
public class Symptom extends Model {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private int symptom;

    public Symptom() { }

    public static Symptom create(int sintoma) {
        Symptom s = new Symptom();
        s.symptom = sintoma;
        return s;
    }

    public Long getId() {
        return id;
    }

    public int getSymptom() {
        return symptom;
    }

    public void setSymptom(int symptom) {
        this.symptom = symptom;
    }

    public static Symptom bind(JsonNode j) {
        int symptom = j.findPath("symptom").asInt();
        Symptom s = create(symptom);
        return s;
    }
}
