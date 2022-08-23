package org.study.service;

import com.google.gson.Gson;
import org.study.entity.CountryCovidData;
import org.study.response.CasesUrlResponse;
import org.study.response.HistoryUrlResponse;
import org.study.response.VaccinesUrlResponse;
import org.study.utils.JsonUtils;
import org.study.utils.ParameterStringBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Covid19InfoService {

    Properties appProps;
    HttpClient httpClient;

    public Covid19InfoService() throws IOException {
        String appConfigPath = new File(".").getCanonicalPath() + "\\app.properties";
        appProps = new Properties();
        appProps.load(new FileInputStream(appConfigPath));
        httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    public CountryCovidData getCountryCovidData(String country) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("country", country);

        //do /cases request in order to get confirmed, recovered, deaths
        String content = doRequest(appProps.getProperty("CASES_URL"), parameters);
        CasesUrlResponse response1 = getResponseObject(content, CasesUrlResponse.class);

        //do /vaccines request in order to get peopleVaccinated, population
        content = doRequest(appProps.getProperty("VACCINES_URL"), parameters);
        VaccinesUrlResponse response2 = getResponseObject(content, VaccinesUrlResponse.class);

        //do /history request in order to get confirmed according to date
        parameters.put("status", "confirmed");
        content = doRequest(appProps.getProperty("HISTORY_URL"), parameters);
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
     * performs a GET request with given url and parameters
     *
     * @param urlStr     String with URL
     * @param parameters Map of parameters for GET request
     * @return content from response or empty String in case of response code is not 200 or Exception is thrown
     */

    public String doRequest(String urlStr, Map<String, String> parameters) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(URI.create(urlStr + ParameterStringBuilder.getParamsString(parameters)))
                    .build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) return "";
            return response.body();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }


    public Properties getAppProps() {
        return appProps;
    }
}
