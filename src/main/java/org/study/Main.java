package org.study;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import org.study.response.CasesUrlResponse;
import org.study.response.HistoryUrlResponse;
import org.study.response.VaccinesUrlResponse;
import org.study.utils.JsonUtils;
import org.study.utils.ParameterStringBuilder;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    static final String CASES_URL = "https://covid-api.mmediagroup.fr/v1/cases";
    static final String VACCINES_URL = "https://covid-api.mmediagroup.fr/v1/vaccines";
    static final String HISTORY_URL = "https://covid-api.mmediagroup.fr/v1/history";

    public static void main(String[] args) {
        String country = "";
        Scanner in = new Scanner(System.in);
        do {
            System.out.println("Please, enter the country: ");
            country = in.nextLine().trim();
        } while (country.isEmpty());

        Map<String, String> parameters = new HashMap<>();
        parameters.put("country", country);

        String content = safeDoRequest(CASES_URL, parameters);
        Gson gson = new GsonBuilder().create();
        CasesUrlResponse response1 = gson.fromJson(JsonUtils.getJsonElement(content, "All"), CasesUrlResponse.class);

        content = safeDoRequest(VACCINES_URL, parameters);
        VaccinesUrlResponse response2 = gson.fromJson(JsonUtils.getJsonElement(content, "All"), VaccinesUrlResponse.class);

        parameters.put("status", "confirmed");
        content = safeDoRequest(HISTORY_URL, parameters);
        JsonElement jel = JsonUtils.getJsonElement(content, "All");
        HistoryUrlResponse response3 = gson.fromJson(jel, HistoryUrlResponse.class);

        CountryCovidData ccd = new CountryCovidData();
        ccd.setData(country, response1, response2, response3);
        System.out.println(ccd);
    }

    static String safeDoRequest(String urlStr, Map<String, String> parameters) {
        String content = null;
        try {
            content = doRequest(urlStr, parameters);
        } catch (IOException e) {
            content = "";
            e.printStackTrace();
        }
        return content;
    }

    static String doRequest(String urlStr, Map<String, String> parameters) throws IOException {
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

    private static class CountryCovidData {
        private String country;
        private Long confirmed;
        private Long recovered;
        private Long deaths;
        private Double vaccinatedLevel;
        private Long confirmedSinceLastHistoryData;
        private Date lastHistoryDataDate;

        public void setData(String country, CasesUrlResponse r1, VaccinesUrlResponse r2, HistoryUrlResponse r3) {
            this.country = country;
            if (r1 != null) {
                confirmed = r1.getConfirmed();
                recovered = r1.getRecovered();
                deaths = r1.getDeaths();
            }
            if (r2 != null && r2.getPopulation() != 0L) {
                vaccinatedLevel = (r2.getPeopleVaccinated() + 0d) / r2.getPopulation() * 100;
            }
            if (r3 != null && r3.getConfirmedByDateMap() != null && !r3.getConfirmedByDateMap().isEmpty() && confirmed != null) {
                for (Map.Entry<Date, Long> entry : r3.getConfirmedByDateMap().entrySet()) {
                    lastHistoryDataDate = entry.getKey();
                    confirmedSinceLastHistoryData = confirmed - entry.getValue();
                    break;
                }
            }
        }

        private String nullableObjectToString(Object obj) {
            return Objects.isNull(obj) ? "info not available" : obj.toString();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("COUNTRY : ").append(country).append('\n')
                    .append("DATA : ").append('\n')
                    .append("    confirmed : ").append(nullableObjectToString(confirmed)).append('\n')
                    .append("    recovered : ").append(nullableObjectToString(recovered)).append('\n')
                    .append("    deaths : ").append(nullableObjectToString(deaths)).append('\n')
                    .append("    vaccinated level (%) : ").append(nullableObjectToString(vaccinatedLevel)).append('\n')
                    .append("    confirmed since ").append(nullableObjectToString(lastHistoryDataDate)).append(" : ")
                    .append(nullableObjectToString(confirmedSinceLastHistoryData)).append('\n');
            return sb.toString();
        }
    }
}
