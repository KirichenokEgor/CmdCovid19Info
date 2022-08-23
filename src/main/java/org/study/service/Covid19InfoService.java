package org.study.service;

import com.google.gson.Gson;
import org.study.entity.CountryCovidData;
import org.study.response.CasesUrlResponse;
import org.study.response.HistoryUrlResponse;
import org.study.response.VaccinesUrlResponse;
import org.study.utils.JsonUtils;
import org.study.utils.ParameterStringBuilder;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Covid19InfoService {

    Properties appProps;

    public Covid19InfoService() throws IOException {
        String appConfigPath = new File(".").getCanonicalPath() + "\\app.properties";
        appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
    }

    public CountryCovidData getCountryCovidData(String country) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("country", country);

        //do /cases request in order to get confirmed, recovered, deaths
        String content = safeDoRequest(appProps.getProperty("CASES_URL"), parameters);
        CasesUrlResponse response1 = getResponseObject(content, CasesUrlResponse.class);

        //do /vaccines request in order to get peopleVaccinated, population
        content = safeDoRequest(appProps.getProperty("VACCINES_URL"), parameters);
        VaccinesUrlResponse response2 = getResponseObject(content, VaccinesUrlResponse.class);

        //do /history request in order to get confirmed according to date
        parameters.put("status", "confirmed");
        content = safeDoRequest(appProps.getProperty("HISTORY_URL"), parameters);
        HistoryUrlResponse response3 = getResponseObject(content, HistoryUrlResponse.class);

        //fill result object
        return CountryCovidData.of(country, response1, response2, response3);
    }

    /**
     * Fills obj with info from JsonElement with name "All"
     *
     * @param content  content from response
     * @param classOfT class of response object
     * @param <T>      response object class parameter
     * @return response object
     */
    public <T> T getResponseObject(String content, Class<T> classOfT) {
        Gson gson = new Gson();
        return gson.fromJson(JsonUtils.getJsonElement(content, "All"), classOfT);
    }

    /**
     * doRequest with exception handling
     *
     * @param urlStr     String with URL
     * @param parameters Map of parameters for GET requestm response or empty String in case of IOException
     */
    public String safeDoRequest(String urlStr, Map<String, String> parameters) {
        String content = null;
        try {
            content = doRequest(urlStr, parameters);
        } catch (IOException e) {
            content = "";
            e.printStackTrace();
        }
        return content;
    }

    /**
     * performs a GET request with given url and parameters
     *
     * @param urlStr     String with URL
     * @param parameters Map of parameters for GET request
     * @return content from response or empty String in case of response code is not 200
     * @throws IOException if smth goes wrong with connection
     */
    public String doRequest(String urlStr, Map<String, String> parameters) throws IOException {
        URL url = new URL(urlStr + ParameterStringBuilder.getParamsString(parameters));
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
        con.setRequestMethod("GET");
//        con.setConnectTimeout(5000);
//        con.setReadTimeout(5000);

        if (con.getResponseCode() != 200) return "";

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        StringBuilder content = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();

        return content.toString();
    }


    public Properties getAppProps() {
        return appProps;
    }
}
