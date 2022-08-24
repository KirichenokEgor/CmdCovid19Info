package org.study.entity;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
@Builder
public class CountryCovidData {
    private String country;
    private Long confirmed;
    private Long recovered;
    private Long deaths;
    private Double vaccinatedLevel;
    private Long confirmedSinceLastHistoryData;
    private Date lastHistoryDataDate;

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
