package io.amadeus.model;

public class ResponseRange {
    String range;
    Long openRange;
    Long endRange;

    public String getRange() {
        return range;
    }

    public void setRange(String range) {
        this.range = range;
    }

    public Long getOpenRange() {
        return openRange;
    }

    public void setOpenRange(Long openRange) {
        this.openRange = openRange;
    }

    public Long getEndRange() {
        return endRange;
    }

    public void setEndRange(Long endRange) {
        this.endRange = endRange;
    }

    public ResponseRange(String range) {
        this.range = range;
    }

    public ResponseRange(Long openRange, Long endRange) {
        this.openRange = openRange;
        this.endRange = endRange;
    }
}
