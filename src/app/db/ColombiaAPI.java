package app.db;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class ColombiaAPI {

    private static final String API_DEPARTAMENTOS = "https://api-colombia.com/api/v1/Department";
    private static final String API_MUNICIPIOS = "https://api-colombia.com/api/v1/Department/%d/cities";

    public static List<String> obtenerDepartamentos() {
        List<String> departamentos = new ArrayList<>();
        try {
            JSONArray data = obtenerDatos(API_DEPARTAMENTOS);
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = data.getJSONObject(i);
                departamentos.add(obj.getString("name"));
            }
            Collections.sort(departamentos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return departamentos;
    }

    public static List<String> obtenerMunicipiosPorDepartamento(String departamento) {
        List<String> municipios = new ArrayList<>();
        try {
            JSONArray departamentos = obtenerDatos(API_DEPARTAMENTOS);
            int depId = -1;

            // Buscar el ID del departamento según el nombre
            for (int i = 0; i < departamentos.length(); i++) {
                JSONObject obj = departamentos.getJSONObject(i);
                if (obj.getString("name").equalsIgnoreCase(departamento)) {
                    depId = obj.getInt("id");
                    break;
                }
            }

            if (depId != -1) {
                String url = String.format(API_MUNICIPIOS, depId);
                JSONArray data = obtenerDatos(url);
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    municipios.add(obj.getString("name"));
                }
                Collections.sort(municipios);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return municipios;
    }

    private static JSONArray obtenerDatos(String apiUrl) throws Exception {
        URL url = new URL(apiUrl);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        return new JSONArray(content.toString());
    }
}
