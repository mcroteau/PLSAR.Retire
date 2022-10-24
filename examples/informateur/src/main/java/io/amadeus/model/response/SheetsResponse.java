package io.amadeus.model.response;

import io.amadeus.model.Paper;

import java.util.List;

public class SheetsResponse {
    Long papersCount;
    List<Paper> papers;

    public SheetsResponse(Long papersCount, List<Paper> papers){
        this.papersCount = papersCount;
        this.papers = papers;
    }
}

