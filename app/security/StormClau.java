package security;


import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


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

        JSONObject value = new JSONObject(r);
        boolean ans = Boolean.parseBoolean((String) value.get("Respuesta"));

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

    public JSONObject getPatientsByDoctor(Long doctor) throws JSONException {
        String urln = url + "/stormclau/doctor/pacientes/" + doctor;
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
}