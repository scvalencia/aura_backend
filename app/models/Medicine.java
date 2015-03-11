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
public class Medicine extends Model {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int hoursAgo;

    public Medicine() { }

    public static Medicine create(String nombre, int horasTomadoAntes) {
        Medicine m = new Medicine();
        m.name = nombre;
        m.hoursAgo = horasTomadoAntes;
        return m;
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

    public int getHoursAgo() {
        return hoursAgo;
    }

    public void setHoursAgo(int hoursAgo) {
        this.hoursAgo = hoursAgo;
    }

    public static Medicine bind(JsonNode j) {
        String name = j.findPath("name").asText();
        int hours = j.findPath("hoursAgo").asInt();
        Medicine m = create(name, hours);
        return m;
    }
}
