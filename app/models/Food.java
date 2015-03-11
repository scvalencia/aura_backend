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
public class Food extends Model {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)

    private Long id;

    private String name;

    private int quantity;

    public Food() {
    }

    public static Food create(String name, int quantity) {
        Food f = new Food();
        f.name = name;
        f.quantity = quantity;
        return f;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public static Food bind(JsonNode j) {
        String name = j.findPath("name").asText();
        int quant = j.findPath("quantity").asInt();
        Food f = create(name, quant);
        return f;
    }
}
