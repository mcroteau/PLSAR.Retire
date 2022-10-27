package giga.model;

public class ItemGroupImport {
    Long id;
    String spreadsheetId;
    String startCell;
    String endCell;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSpreadsheetId() {
        return spreadsheetId;
    }

    public void setSpreadsheetId(String spreadsheetId) {
        this.spreadsheetId = spreadsheetId;
    }

    public String getStartCell() {
        return startCell;
    }

    public void setStartCell(String startCell) {
        this.startCell = startCell;
    }

    public String getEndCell() {
        return endCell;
    }

    public void setEndCell(String endCell) {
        this.endCell = endCell;
    }
}
