package foo.model.response;

import java.util.List;

public class SheetsResponse {
    Long papersCount;
    List<Paper> papers;

    public SheetsResponse(Long papersCount, List<Paper> papers){
        this.papersCount = papersCount;
        this.papers = papers;
    }
}

