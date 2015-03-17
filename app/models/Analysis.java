package models;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Created by scvalencia on 3/16/15.
 */
public class Analysis {

    public int intensity;
    public Date date;
    public int hours;

    public Analysis(int intensity, Date date, int hours) {
        this.intensity = intensity;
        this.date = date;
        this.hours = hours;
    }

    public int getIntensity() {
        return intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }
}
