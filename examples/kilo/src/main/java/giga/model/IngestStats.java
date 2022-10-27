package giga.model;

public class IngestStats {

    Integer count;
    Integer processed;
    Integer unprocessed;
    Long startTime;
    Long endTime;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getProcessed() {
        return processed;
    }

    public void setProcessed(Integer processed) {
        this.processed = processed;
    }

    public Integer getUnprocessed() {
        return unprocessed;
    }

    public void setUnprocessed(Integer unprocessed) {
        this.unprocessed = unprocessed;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }
}
