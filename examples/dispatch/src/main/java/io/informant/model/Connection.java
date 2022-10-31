package io.informant.model;

public class Connection {
    public Connection(Long userId, Long connectedUserId) {
        this.userId = userId;
        this.connectedUserId = connectedUserId;
    }

    Long id;
    Long userId;
    Long connectedUserId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getConnectedUserId() {
        return connectedUserId;
    }

    public void setConnectedUserId(Long connectedUserId) {
        this.connectedUserId = connectedUserId;
    }
}
