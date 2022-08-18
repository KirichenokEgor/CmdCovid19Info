package org.study.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class VaccinesUrlResponse {
    @SerializedName("people_vaccinated")
    private long peopleVaccinated;
    private long population;
}
