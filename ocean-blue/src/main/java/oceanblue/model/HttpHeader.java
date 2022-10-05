package oceanblue.model;

public class HttpHeader {
    String header;
    String content;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public HttpHeader(String header, String content) {
        this.header = header;
        this.content = content;
    }
}
