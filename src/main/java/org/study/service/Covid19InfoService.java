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
import java.util.Date;
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
        CasesUrlResponse cases = getResponseObject(content, CasesUrlResponse.class);

        //do /vaccines request in order to get peopleVaccinated, population
        content = doRequest(appProps.getProperty("VACCINES_URL"), parameters);
        VaccinesUrlResponse vaccines = getResponseObject(content, VaccinesUrlResponse.class);

        //do /history request in order to get confirmed according to date
        parameters.put("status", "confirmed");
        content = doRequest(appProps.getProperty("HISTORY_URL"), parameters);
        HistoryUrlResponse history = getResponseObject(content, HistoryUrlResponse.class);

        //fill result object
        return getCountryCovidData(country, cases, vaccines, history);
    }

    /**
     * generates CountryCovidData from given params
     *
     * @param country  String
     * @param cases    CasesUrlResponse
     * @param vaccines VaccinesUrlResponse
     * @param history  HistoryUrlResponse
     */
    public CountryCovidData getCountryCovidData(String country, CasesUrlResponse cases,
                                                VaccinesUrlResponse vaccines, HistoryUrlResponse history) {
        if (country == null || country.isEmpty()) return null;
        CountryCovidData.CountryCovidDataBuilder builder = CountryCovidData.builder();
        builder.country(country);
//        CountryCovidData ccd = new CountryCovidData(country);
        Long tempConfirmed = null;
        if (cases != null) {
            builder.confirmed(cases.getConfirmed())
                    .recovered(cases.getRecovered())
                    .deaths(cases.getDeaths());
            tempConfirmed = cases.getConfirmed();
        }
        if (vaccines != null && vaccines.getPopulation() != 0L) {
            builder.vaccinatedLevel((vaccines.getPeopleVaccinated() + 0d) / vaccines.getPopulation() * 100);
        }
        if (history != null && history.getConfirmedByDateMap() != null && !history.getConfirmedByDateMap().isEmpty()
                && tempConfirmed != null) {
            Map.Entry<Date, Long> e = history.getConfirmedByDateMap().entrySet().stream().findFirst().orElse(null);//.limit(1L).forEach(e -> {
            if (e != null) {
                builder.lastHistoryDataDate(e.getKey());
                builder.confirmedSinceLastHistoryData(tempConfirmed - e.getValue());
            }
        }
        return builder.build();
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
