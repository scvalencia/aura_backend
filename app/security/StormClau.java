package security;


import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import play.libs.Json;

import java.util.ArrayList;
import java.util.List;


public class StormClau {

    public String url = "https://murmuring-beyond-8483.herokuapp.com/";
    public HttpClient httpclient = new DefaultHttpClient();

    public void registerDoctorForPatient(Long patientId, Long DoctorId) {
        String urln = url + "stormclau/paciente/registrarDoctor/" + patientId + "/" + DoctorId;
        HttpPost httpPost = new HttpPost(urln);

        try {
            HttpResponse response = httpclient.execute(httpPost);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerDoctor(Long id) {
        String urln = url + "stormclau/doctor/registrar/" + id;
        HttpPost httpPost = new HttpPost(urln);

        try {
            HttpResponse response = httpclient.execute(httpPost);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void registerPatient(Long id) {
        String urln = url + "stormclau/paciente/registrar/" + id;
        HttpPost httpPost = new HttpPost(urln);

        try {
            HttpResponse response = httpclient.execute(httpPost);
            String r = EntityUtils.toString(response.getEntity());
            System.out.println(r);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean patientHasDoctor(Long doctor, Long patient) throws JSONException {
        String urln = url + "stormclau/doctor/acceso/" + patient + "/" + doctor;
        HttpGet httpPost = new HttpGet(urln);
        String r = "";

        try {
            HttpResponse response = httpclient.execute(httpPost);
            r = EntityUtils.toString(response.getEntity());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        com.fasterxml.jackson.databind.JsonNode value = Json.toJson(r);
        boolean ans = value.findPath("Respuesta").asBoolean();

        return ans;
    }

    public JSONObject getDoctorsByPatient(Long patient) throws JSONException {
        String urln = url + "stormclau/paciente/doctores/" + patient;
        HttpGet httpPost = new HttpGet(urln);
        String r = "";

        try {
            HttpResponse response = httpclient.execute(httpPost);
            r = EntityUtils.toString(response.getEntity());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return new JSONObject(r);

    }

    public List<Long> getPatientsByDoctor(Long doctor) throws JSONException {
        String urln = url + "stormclau/doctor/pacientes/" + doctor;
        HttpGet httpPost = new HttpGet(urln);
        String r = "";

        try {
            HttpResponse response = httpclient.execute(httpPost);
            r = EntityUtils.toString(response.getEntity());
            System.out.println(urln);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        for(int i = 0; i < 100; i++) System.out.println(r);

        JSONArray collection = new JSONArray(r);
        List<Long> ans = new ArrayList<Long>();


        for(int i = 0; i < collection.length(); i++) {
            JSONObject o = collection.getJSONObject(i);
            Integer id = (Integer) o.get("id");
            ans.add(id.longValue());
        }

        return ans;
    }
}