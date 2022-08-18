package org.study.response;

import lombok.Data;

@Data
public class CasesUrlResponse {
    private long confirmed;
    private long recovered;
    private long deaths;
}
