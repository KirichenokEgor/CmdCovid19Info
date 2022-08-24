package org.study;

import org.junit.jupiter.api.Test;
import org.study.entity.CountryCovidData;
import org.study.response.CasesUrlResponse;
import org.study.response.HistoryUrlResponse;
import org.study.response.VaccinesUrlResponse;
import org.study.service.Covid19InfoService;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnitTests {

    Covid19InfoService infoService;

    public UnitTests() throws IOException {
        try {
            infoService = new Covid19InfoService();
        } catch (IOException e) {
            System.out.println("Can't read properties");
            throw e;
        }
    }

    //safeDoRequest
    //correct
    @Test
    void safeDoRequestCorrect() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("country", "France");
        String content = infoService.doRequest(infoService.getAppProps().getProperty("CASES_URL"), parameters);
        assertFalse(content.isEmpty());
    }

    //with incorrect url
    @Test
    void safeDoRequestIncorrectUrl() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("country", "France");
        String content = infoService.doRequest(infoService.getAppProps().getProperty("CASES_URL") + "la", parameters);
        assertTrue(content.isEmpty());
    }

    //with incorrect additional params (names)
    @Test
    void safeDoRequestAdditionalParams() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("wrongParam", "ooo");
        parameters.put("country", "France");
        String content = infoService.doRequest(infoService.getAppProps().getProperty("CASES_URL"), parameters);
        assertFalse(content.isEmpty());
    }

    //with incorrect params (values)
    @Test
    void safeDoRequestIncorrectParamsValues() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("status", "bugaga");
        String content = infoService.doRequest(infoService.getAppProps().getProperty("HISTORY_URL"), parameters);
        assertTrue(content.contains("\"dates\": {}}}}"));
    }

    //with empty params
    @Test
    void safeDoRequestEmptyParams() {
        Map<String, String> parameters = new HashMap<>();
        String content = infoService.doRequest(infoService.getAppProps().getProperty("HISTORY_URL"), parameters);
        assertTrue(content.isEmpty());
    }

    //CasesUrlResponse from good content
    @Test
    void fillCasesUrlResponseFromGoodContent() {
        String content = "{\"All\": {\"confirmed\": 33357883, \"recovered\": 0, \"deaths\": 149992, \"country\": \"France\"," +
                " \"population\": 64979548, \"sq_km_area\": 551500, \"life_expectancy\": \"78.8\", \"elevation_in_meters\": 375," +
                " \"continent\": \"Europe\", \"abbreviation\": \"FR\", \"location\": \"Western Europe\", \"iso\": 250," +
                " \"capital_city\": \"Paris\", \"lat\": \"46.2276\", \"long\": \"2.2137\", \"updated\": \"2022-08-19 04:20:54\"} }";
        CasesUrlResponse cases = infoService.getResponseObject(content, CasesUrlResponse.class);
        assertEquals(33357883L, cases.getConfirmed());
        assertEquals(0L, cases.getRecovered());
        assertEquals(149992L, cases.getDeaths());
    }

    //CasesUrlResponse from bad content
    @Test
    void fillCasesUrlResponseFromBadContent() {
        String content = "{\"Alldfdsdf\": {\"confirmed\": 33357883, \"recovered\": 0, \"deaths\": 149992, \"country\": \"France\"," +
                " \"population\": 64979548, \"sq_km_area\": 551500, \"life_expectancy\": \"78.8\", \"elevation_in_meters\": 375," +
                " \"continent\": \"Europe\", \"abbreviation\": \"FR\", \"location\": \"Western Europe\", \"iso\": 250," +
                " \"capital_city\": \"Paris\", \"lat\": \"46.2276\", \"long\": \"2.2137\", \"updated\": \"2022-08-19 04:20:54\"} }";
        CasesUrlResponse cases = infoService.getResponseObject(content, CasesUrlResponse.class);
        assertNull(cases);
    }

    //CasesUrlResponse from empty content
    @Test
    void fillCasesUrlResponseFromEmptyContent() {
        String content = "";
        CasesUrlResponse cases = infoService.getResponseObject(content, CasesUrlResponse.class);
        assertNull(cases);
    }

    //VaccinesUrlResponse from good content
    @Test
    void fillVaccinesUrlResponseFromGoodContent() {
        String content = "{\"All\": {\"administered\": 152404977, \"people_vaccinated\": 53019788," +
                " \"people_partially_vaccinated\": 54536637, \"country\": \"France\", \"population\": 64979548," +
                " \"sq_km_area\": 551500, \"life_expectancy\": \"78.8\", \"elevation_in_meters\": 375," +
                " \"continent\": \"Europe\", \"abbreviation\": \"FR\", \"location\": \"Western Europe\", \"iso\": 250," +
                " \"capital_city\": \"Paris\", \"updated\": \"2022/08/19 00:00:00+00\"} }";
        VaccinesUrlResponse vaccines = infoService.getResponseObject(content, VaccinesUrlResponse.class);
        assertEquals(53019788L, vaccines.getPeopleVaccinated());
        assertEquals(64979548L, vaccines.getPopulation());
    }

    //VaccinesUrlResponse from bad content
    @Test
    void fillVaccinesUrlResponseFromBadContent() {
        String content = "{\"Alqwewql\": {\"administered\": 152404977, \"people_vaccinated\": 53019788," +
                " \"people_partially_vaccinated\": 54536637, \"country\": \"France\", \"population\": 64979548," +
                " \"sq_km_area\": 551500, \"life_expectancy\": \"78.8\", \"elevation_in_meters\": 375," +
                " \"continent\": \"Europe\", \"abbreviation\": \"FR\", \"location\": \"Western Europe\", \"iso\": 250," +
                " \"capital_city\": \"Paris\", \"updated\": \"2022/08/19 00:00:00+00\"} }";
        VaccinesUrlResponse vaccines = infoService.getResponseObject(content, VaccinesUrlResponse.class);
        assertNull(vaccines);
    }

    //VaccinesUrlResponse from empty content
    @Test
    void fillVaccinesUrlResponseFromEmptyContent() {
        String content = "";
        VaccinesUrlResponse vaccines = infoService.getResponseObject(content, VaccinesUrlResponse.class);
        assertNull(vaccines);
    }

    //HistoryUrlResponse from good content
    @Test
    void fillHistoryUrlResponseFromGoodContent() throws ParseException {
        String content = "{\"All\": {\"country\": \"France\", \"population\": 64979548, \"sq_km_area\": 551500," +
                " \"life_expectancy\": \"78.8\", \"elevation_in_meters\": 375, \"continent\": \"Europe\"," +
                " \"abbreviation\": \"FR\", \"location\": \"Western Europe\", \"iso\": 250, \"capital_city\": \"Paris\"," +
                " \"dates\": {\"2022-08-18\": 33357883, \"2022-08-17\": 33334278} } }";
        HistoryUrlResponse history = infoService.getResponseObject(content, HistoryUrlResponse.class);
        assertNotNull(history.getConfirmedByDateMap());
        Map<Date, Long> map = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        map.put(sdf.parse("2022-08-18"), 33357883L);
        map.put(sdf.parse("2022-08-17"), 33334278L);
        for (Map.Entry<Date, Long> entry : history.getConfirmedByDateMap().entrySet()) {
            assertEquals(map.get(entry.getKey()), entry.getValue());
        }
    }

    //HistoryUrlResponse from bad content
    @Test
    void fillHistoryUrlResponseFromBadContent() {
        String content = "{\"Aasdasll\": {\"country\": \"France\", \"population\": 64979548, \"sq_km_area\": 551500," +
                " \"life_expectancy\": \"78.8\", \"elevation_in_meters\": 375, \"continent\": \"Europe\"," +
                " \"abbreviation\": \"FR\", \"location\": \"Western Europe\", \"iso\": 250, \"capital_city\": \"Paris\"," +
                " \"dates\": {\"2022-08-18\": 33357883, \"2022-08-17\": 33334278} } }";
        HistoryUrlResponse history = infoService.getResponseObject(content, HistoryUrlResponse.class);
        assertNull(history);
    }

    //HistoryUrlResponse from empty content
    @Test
    void fillHistoryUrlResponseFromEmptyContent() {
        String content = "";
        HistoryUrlResponse history = infoService.getResponseObject(content, HistoryUrlResponse.class);
        assertNull(history);
    }

    CasesUrlResponse getCasesUrlResponse() {
        CasesUrlResponse cases = new CasesUrlResponse();
        cases.setConfirmed(1L);
        cases.setRecovered(2L);
        cases.setDeaths(3L);
        return cases;
    }

    VaccinesUrlResponse getVaccinesUrlResponse() {
        VaccinesUrlResponse vaccines = new VaccinesUrlResponse();
        vaccines.setPeopleVaccinated(3L);
        vaccines.setPopulation(4L);
        return vaccines;
    }

    HistoryUrlResponse getHistoryUrlResponse() throws ParseException {
        HistoryUrlResponse history = new HistoryUrlResponse();
        Map<Date, Long> map = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        map.put(sdf.parse("2022-08-18"), 0L);
        map.put(sdf.parse("2022-08-17"), 0L);
        history.setConfirmedByDateMap(map);
        return history;
    }

    //ccd from good CasesUrlResponse,VaccinesUrlResponse,HistoryUrlResponse
    @Test
    void fillCountryCovidData123() throws ParseException {
        String country = "France";
        CasesUrlResponse cases = getCasesUrlResponse();
        VaccinesUrlResponse vaccines = getVaccinesUrlResponse();
        HistoryUrlResponse history = getHistoryUrlResponse();

        CountryCovidData ccd = infoService.getCountryCovidData(country, cases, vaccines, history);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        assertEquals(country, ccd.getCountry());
        assertEquals(1L, ccd.getConfirmed());
        assertEquals(2L, ccd.getRecovered());
        assertEquals(3L, ccd.getDeaths());
        assertEquals(75.0d, ccd.getVaccinatedLevel());
        assertEquals(sdf.parse("2022-08-18"), ccd.getLastHistoryDataDate());
        assertEquals(1L, ccd.getConfirmedSinceLastHistoryData());
    }

    //ccd from good CasesUrlResponse,VaccinesUrlResponse
    @Test
    void fillCountryCovidData12() throws ParseException {
        String country = "France";
        CasesUrlResponse cases = getCasesUrlResponse();
        VaccinesUrlResponse vaccines = getVaccinesUrlResponse();

        CountryCovidData ccd = infoService.getCountryCovidData(country, cases, vaccines, null);

        assertEquals(country, ccd.getCountry());
        assertEquals(1L, ccd.getConfirmed());
        assertEquals(2L, ccd.getRecovered());
        assertEquals(3L, ccd.getDeaths());
        assertEquals(75.0d, ccd.getVaccinatedLevel());
        assertEquals(null, ccd.getLastHistoryDataDate());
        assertEquals(null, ccd.getConfirmedSinceLastHistoryData());
    }

    //ccd from good CasesUrlResponse,HistoryUrlResponse
    @Test
    void fillCountryCovidData13() throws ParseException {
        String country = "France";
        CasesUrlResponse cases = getCasesUrlResponse();
        HistoryUrlResponse history = getHistoryUrlResponse();

        CountryCovidData ccd = infoService.getCountryCovidData(country, cases, null, history);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        assertEquals(country, ccd.getCountry());
        assertEquals(1L, ccd.getConfirmed());
        assertEquals(2L, ccd.getRecovered());
        assertEquals(3L, ccd.getDeaths());
        assertEquals(null, ccd.getVaccinatedLevel());
        assertEquals(sdf.parse("2022-08-18"), ccd.getLastHistoryDataDate());
        assertEquals(1L, ccd.getConfirmedSinceLastHistoryData());
    }

    //ccd from good VaccinesUrlResponse,HistoryUrlResponse
    @Test
    void fillCountryCovidData23() throws ParseException {
        String country = "France";
        VaccinesUrlResponse vaccines = getVaccinesUrlResponse();
        HistoryUrlResponse history = getHistoryUrlResponse();

        CountryCovidData ccd = infoService.getCountryCovidData(country, null, vaccines, history);

        assertEquals(country, ccd.getCountry());
        assertEquals(null, ccd.getConfirmed());
        assertEquals(null, ccd.getRecovered());
        assertEquals(null, ccd.getDeaths());
        assertEquals(75.0d, ccd.getVaccinatedLevel());
        assertEquals(null, ccd.getLastHistoryDataDate());
        assertEquals(null, ccd.getConfirmedSinceLastHistoryData());
    }
}
