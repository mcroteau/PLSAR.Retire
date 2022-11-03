package foo.model.response;

public class GenericResponse {
    String status;
    String message;
    public GenericResponse(String status, String message){
        this.status = status;
        this.message = message;
    }
}
