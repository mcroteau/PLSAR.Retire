package giga.support;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Logger;

public class Papi implements DataSource {

    Logger Log = Logger.getLogger("Papi");

    int cc;

    Properties props;
    Queue<Connection> queue;

    public Papi(New config){
        this.cc = config.c;
        this.props = config.props;
        this.queue = new LinkedBlockingDeque<>();
        this.make();
    }

    public void make() {
        try {
            Executable executable = null;
            for (int qzo = 0; qzo < cc; qzo++) {
                executable = new Executable(this);
                executable.run();
            }
            executable.join();
            setupShutdown();
        }catch (InterruptedException ex) {}
    }

    private void setupShutdown(){
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Log.info("papi clean!");
//                for(Connection connection : queue){
//                    connection.close();
//                }
            } catch (Exception ex) {}
        }));
    }

    protected void addConnection() {
        Connection connection = createConnection();
        if(connection != null) {
            queue.add(connection);
        }
    }

    public Connection getConnection() {
        if(queue.peek() != null) {
            new Executable(this).run();
            return queue.poll();
        }
        return getConnection();
    }

    protected Connection createConnection(){
        Connection connection = null;
        try{
            connection = DriverManager.getConnection(
                    props.getProperty("url"),
                    props);
            connection.setAutoCommit(false);
        } catch (SQLException ex) {}
        return connection;
    }

    public static class Executable extends Thread {

        Papi papi;

        public Executable(Papi papi){
            this.papi = papi;
        }

        @Override
        public void run() {
            papi.addConnection();
        }
    }

    public static class New {
        int c;
        String ur;
        String u;
        String p;
        String d;

        Properties props;

        public New url(String ur){
            this.ur = ur;
            return this;
        }
        public New connections(int c){
            this.c = c;
            return this;
        }
        public New user(String u){
            this.u = u;
            return this;
        }
        public New password(String p){
            this.p = p;
            return this;
        }
        public New driver(String d){
            this.d = d;
            return this;
        }
        public Papi make(){
            try {
                Class.forName(d);

                props = new Properties();
                props.setProperty("user", u);
                props.setProperty("password", p);
                props.setProperty("url", ur);

            }catch (Exception ex){
                ex.printStackTrace();
            }

            return new Papi(this);
        }
    }

    public static class PapiException extends SQLException {
        public PapiException(String message){
            super(message);
        }
    }

    public Connection getConnection(String username, String password) throws PapiException {
        throw new PapiException("this is a simple implementation, use get connection() no parameters");
    }


    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new PapiException("no log writer here... just papi");
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new PapiException("no log writer here... just papi");
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new PapiException("no login timeout... just papi");
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new PapiException("no login timeout... just papi");
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException("parent logger, what? just papi");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws PapiException {
        throw new PapiException("no wrapper.");
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws PapiException {
        throw new PapiException("no wrapper.");
    }

}