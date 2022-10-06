package oceanblue.implement;

import oceanblue.model.HttpRequest;

public interface ViewRenderer {

    /**
     * example would be sec:auth
     * within the view &lt;sec:auth&gt;Hi!&lt;/sec:auth&gt;
     *
     * @return key:value pair
     */
    public String getKey();

    /**
     * @return true if conditional snipit, false if content is rendered.
     */
    public Boolean isEval();

    public boolean truthy(HttpRequest httpRequest);

    public String render(HttpRequest httpRequest);

}
