package io.informant.model.response;

public class ProgressResponse {
    String status;
    Long progressBytes;
    ProgressTracker progressTracker;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getProgressBytes() {
        return progressBytes;
    }

    public void setProgressBytes(Long progressBytes) {
        this.progressBytes = progressBytes;
    }

    public ProgressResponse(String status, Long progressBytes) {
        this.status = status;
        this.progressBytes = progressBytes;
    }

    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    public void setProgressTracker(ProgressTracker progressTracker) {
        this.progressTracker = progressTracker;
    }
}
