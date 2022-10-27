import org.loadtest4j.LoadTester;
import org.loadtest4j.Request;
import org.loadtest4j.Result;
import org.loadtest4j.factory.LoadTesterFactory;

import java.time.Duration;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        LoadTester loadTester = LoadTesterFactory.getLoadTester();

        List<Request> requests = List.of(Request.get("/q"));

        Result result = loadTester.run(requests);

        System.out.println("%:" + result.getResponseTime().getPercentile(90));
    }
}