package org.study.response;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class HistoryUrlResponse {
    @SerializedName("dates")
    private Map<Date, Long> confirmedByDateMap;
}
