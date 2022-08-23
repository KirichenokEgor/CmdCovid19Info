package org.study.entity;

import lombok.Data;
import org.study.response.CasesUrlResponse;
import org.study.response.HistoryUrlResponse;
import org.study.response.VaccinesUrlResponse;

import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Data
public class CountryCovidData {
    private String country;
    private Long confirmed;
    private Long recovered;
    private Long deaths;
    private Double vaccinatedLevel;
    private Long confirmedSinceLastHistoryData;
    private Date lastHistoryDataDate;

    private CountryCovidData(String country) {
        this.country = country;
    }

    /**
     * generates CountryCovidData from given params
     *
     * @param country String
     * @param r1      CasesUrlResponse
     * @param r2      VaccinesUrlResponse
     * @param r3      HistoryUrlResponse
     */
    public static CountryCovidData of(String country, CasesUrlResponse r1, VaccinesUrlResponse r2, HistoryUrlResponse r3) {
        if (country == null || country.isEmpty()) return null;
        CountryCovidData ccd = new CountryCovidData(country);
        if (r1 != null) {
            ccd.confirmed = r1.getConfirmed();
            ccd.recovered = r1.getRecovered();
            ccd.deaths = r1.getDeaths();
        }
        if (r2 != null && r2.getPopulation() != 0L) {
            ccd.vaccinatedLevel = (r2.getPeopleVaccinated() + 0d) / r2.getPopulation() * 100;
        }
        if (r3 != null && r3.getConfirmedByDateMap() != null && !r3.getConfirmedByDateMap().isEmpty() && ccd.confirmed != null) {
            Map.Entry<Date, Long> e = r3.getConfirmedByDateMap().entrySet().stream().findFirst().orElse(null);//.limit(1L).forEach(e -> {
            if (e != null) {
                ccd.lastHistoryDataDate = e.getKey();
                ccd.confirmedSinceLastHistoryData = ccd.confirmed - e.getValue();
            }
        }
        return ccd;
    }

    /**
     * @param obj Object
     * @return String representation of obj or "info not available" if obj is null
     */
    private String nullableObjectToString(Object obj) {
        return Objects.isNull(obj) ? "info not available" : obj.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("COUNTRY : ").append(nullableObjectToString(country)).append('\n')
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
