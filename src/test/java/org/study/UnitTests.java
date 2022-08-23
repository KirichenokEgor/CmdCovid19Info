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

    //CasesUrlResponse
    //r1 from good content
    @Test
    void fillCasesUrlResponseFromGoodContent() {
        String content = "{\"All\": {\"confirmed\": 33357883, \"recovered\": 0, \"deaths\": 149992, \"country\": \"France\"," +
                " \"population\": 64979548, \"sq_km_area\": 551500, \"life_expectancy\": \"78.8\", \"elevation_in_meters\": 375," +
                " \"continent\": \"Europe\", \"abbreviation\": \"FR\", \"location\": \"Western Europe\", \"iso\": 250," +
                " \"capital_city\": \"Paris\", \"lat\": \"46.2276\", \"long\": \"2.2137\", \"updated\": \"2022-08-19 04:20:54\"} }";
        CasesUrlResponse r1 = infoService.getResponseObject(content, CasesUrlResponse.class);
        assertEquals(r1.getConfirmed(), 33357883L);
        assertEquals(r1.getRecovered(), 0L);
        assertEquals(r1.getDeaths(), 149992L);
    }

    //r1 from bad content
    @Test
    void fillCasesUrlResponseFromBadContent() {
        String content = "{\"Alldfdsdf\": {\"confirmed\": 33357883, \"recovered\": 0, \"deaths\": 149992, \"country\": \"France\"," +
                " \"population\": 64979548, \"sq_km_area\": 551500, \"life_expectancy\": \"78.8\", \"elevation_in_meters\": 375," +
                " \"continent\": \"Europe\", \"abbreviation\": \"FR\", \"location\": \"Western Europe\", \"iso\": 250," +
                " \"capital_city\": \"Paris\", \"lat\": \"46.2276\", \"long\": \"2.2137\", \"updated\": \"2022-08-19 04:20:54\"} }";
        CasesUrlResponse r1 = infoService.getResponseObject(content, CasesUrlResponse.class);
        assertNull(r1);
    }

    //r1 from empty content
    @Test
    void fillCasesUrlResponseFromEmptyContent() {
        String content = "";
        CasesUrlResponse r1 = infoService.getResponseObject(content, CasesUrlResponse.class);
        assertNull(r1);
    }

    //VaccinesUrlResponse
    //r2 from good content
    @Test
    void fillVaccinesUrlResponseFromGoodContent() {
        String content = "{\"All\": {\"administered\": 152404977, \"people_vaccinated\": 53019788," +
                " \"people_partially_vaccinated\": 54536637, \"country\": \"France\", \"population\": 64979548," +
                " \"sq_km_area\": 551500, \"life_expectancy\": \"78.8\", \"elevation_in_meters\": 375," +
                " \"continent\": \"Europe\", \"abbreviation\": \"FR\", \"location\": \"Western Europe\", \"iso\": 250," +
                " \"capital_city\": \"Paris\", \"updated\": \"2022/08/19 00:00:00+00\"} }";
        VaccinesUrlResponse r2 = infoService.getResponseObject(content, VaccinesUrlResponse.class);
        assertEquals(r2.getPeopleVaccinated(), 53019788L);
        assertEquals(r2.getPopulation(), 64979548L);
    }

    //r2 from bad content
    @Test
    void fillVaccinesUrlResponseFromBadContent() {
        String content = "{\"Alqwewql\": {\"administered\": 152404977, \"people_vaccinated\": 53019788," +
                " \"people_partially_vaccinated\": 54536637, \"country\": \"France\", \"population\": 64979548," +
                " \"sq_km_area\": 551500, \"life_expectancy\": \"78.8\", \"elevation_in_meters\": 375," +
                " \"continent\": \"Europe\", \"abbreviation\": \"FR\", \"location\": \"Western Europe\", \"iso\": 250," +
                " \"capital_city\": \"Paris\", \"updated\": \"2022/08/19 00:00:00+00\"} }";
        VaccinesUrlResponse r2 = infoService.getResponseObject(content, VaccinesUrlResponse.class);
        assertNull(r2);
    }

    //r2 from empty content
    @Test
    void fillVaccinesUrlResponseFromEmptyContent() {
        String content = "";
        VaccinesUrlResponse r2 = infoService.getResponseObject(content, VaccinesUrlResponse.class);
        assertNull(r2);
    }

    //HistoryUrlResponse
    //r3 from good content
    @Test
    void fillHistoryUrlResponseFromGoodContent() throws ParseException {
        String content = "{\"All\": {\"country\": \"France\", \"population\": 64979548, \"sq_km_area\": 551500," +
                " \"life_expectancy\": \"78.8\", \"elevation_in_meters\": 375, \"continent\": \"Europe\"," +
                " \"abbreviation\": \"FR\", \"location\": \"Western Europe\", \"iso\": 250, \"capital_city\": \"Paris\"," +
                " \"dates\": {\"2022-08-18\": 33357883, \"2022-08-17\": 33334278} } }";
        HistoryUrlResponse r3 = infoService.getResponseObject(content, HistoryUrlResponse.class);
        assertNotNull(r3.getConfirmedByDateMap());
        Map<Date, Long> map = new TreeMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        map.put(sdf.parse("2022-08-18"), 33357883L);
        map.put(sdf.parse("2022-08-17"), 33334278L);
        for (Map.Entry<Date, Long> entry : r3.getConfirmedByDateMap().entrySet()) {
            assertEquals(entry.getValue(), map.get(entry.getKey()));
        }
//        assertEquals(r2.getPopulation(), 64979548L);
    }

    //r3 from bad content
    @Test
    void fillHistoryUrlResponseFromBadContent() {
        String content = "{\"Aasdasll\": {\"country\": \"France\", \"population\": 64979548, \"sq_km_area\": 551500," +
                " \"life_expectancy\": \"78.8\", \"elevation_in_meters\": 375, \"continent\": \"Europe\"," +
                " \"abbreviation\": \"FR\", \"location\": \"Western Europe\", \"iso\": 250, \"capital_city\": \"Paris\"," +
                " \"dates\": {\"2022-08-18\": 33357883, \"2022-08-17\": 33334278} } }";
        HistoryUrlResponse r3 = infoService.getResponseObject(content, HistoryUrlResponse.class);
        assertNull(r3);
    }

    //r3 from empty content
    @Test
    void fillHistoryUrlResponseFromEmptyContent() {
        String content = "";
        HistoryUrlResponse r3 = infoService.getResponseObject(content, HistoryUrlResponse.class);
        assertNull(r3);
    }

    CasesUrlResponse getCasesUrlResponse() {
        CasesUrlResponse r1 = new CasesUrlResponse();
        r1.setConfirmed(1L);
        r1.setRecovered(2L);
        r1.setDeaths(3L);
        return r1;
    }

    VaccinesUrlResponse getVaccinesUrlResponse() {
        VaccinesUrlResponse r2 = new VaccinesUrlResponse();
        r2.setPeopleVaccinated(3L);
        r2.setPopulation(4L);
        return r2;
    }

    HistoryUrlResponse getHistoryUrlResponse() throws ParseException {
        HistoryUrlResponse r3 = new HistoryUrlResponse();
        Map<Date, Long> map = new LinkedHashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        map.put(sdf.parse("2022-08-18"), 0L);
        map.put(sdf.parse("2022-08-17"), 0L);
        r3.setConfirmedByDateMap(map);
        return r3;
    }

    //ccd from good r1,r2,r3
    @Test
    void fillCountryCovidData123() throws ParseException {
        String country = "France";
        CasesUrlResponse r1 = getCasesUrlResponse();
        VaccinesUrlResponse r2 = getVaccinesUrlResponse();
        HistoryUrlResponse r3 = getHistoryUrlResponse();

        CountryCovidData ccd = CountryCovidData.of(country, r1, r2, r3);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        assertEquals(ccd.getCountry(), country);
        assertEquals(ccd.getConfirmed(), 1L);
        assertEquals(ccd.getRecovered(), 2L);
        assertEquals(ccd.getDeaths(), 3L);
        assertEquals(ccd.getVaccinatedLevel(), 75.0d);
        assertEquals(ccd.getLastHistoryDataDate(), sdf.parse("2022-08-18"));
        assertEquals(ccd.getConfirmedSinceLastHistoryData(), 1L);
    }

    //ccd from good r1,r2
    @Test
    void fillCountryCovidData12() throws ParseException {
        String country = "France";
        CasesUrlResponse r1 = getCasesUrlResponse();
        VaccinesUrlResponse r2 = getVaccinesUrlResponse();

        CountryCovidData ccd = CountryCovidData.of(country, r1, r2, null);

        assertEquals(ccd.getCountry(), country);
        assertEquals(ccd.getConfirmed(), 1L);
        assertEquals(ccd.getRecovered(), 2L);
        assertEquals(ccd.getDeaths(), 3L);
        assertEquals(ccd.getVaccinatedLevel(), 75.0d);
        assertEquals(ccd.getLastHistoryDataDate(), null);
        assertEquals(ccd.getConfirmedSinceLastHistoryData(), null);
    }

    //ccd from good r1,r3
    @Test
    void fillCountryCovidData13() throws ParseException {
        String country = "France";
        CasesUrlResponse r1 = getCasesUrlResponse();
        HistoryUrlResponse r3 = getHistoryUrlResponse();

        CountryCovidData ccd = CountryCovidData.of(country, r1, null, r3);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        assertEquals(ccd.getCountry(), country);
        assertEquals(ccd.getConfirmed(), 1L);
        assertEquals(ccd.getRecovered(), 2L);
        assertEquals(ccd.getDeaths(), 3L);
        assertEquals(ccd.getVaccinatedLevel(), null);
        assertEquals(ccd.getLastHistoryDataDate(), sdf.parse("2022-08-18"));
        assertEquals(ccd.getConfirmedSinceLastHistoryData(), 1L);
    }

    //ccd from good r2,r3
    @Test
    void fillCountryCovidData23() throws ParseException {
        String country = "France";
        VaccinesUrlResponse r2 = getVaccinesUrlResponse();
        HistoryUrlResponse r3 = getHistoryUrlResponse();

        CountryCovidData ccd = CountryCovidData.of(country, null, r2, r3);

        assertEquals(ccd.getCountry(), country);
        assertEquals(ccd.getConfirmed(), null);
        assertEquals(ccd.getRecovered(), null);
        assertEquals(ccd.getDeaths(), null);
        assertEquals(ccd.getVaccinatedLevel(), 75.0d);
        assertEquals(ccd.getLastHistoryDataDate(), null);
        assertEquals(ccd.getConfirmedSinceLastHistoryData(), null);
    }
}
