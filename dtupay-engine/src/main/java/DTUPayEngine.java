

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/engine")
public class DTUPayEngine {

    private Database db;

    public DTUPayEngine(){
        db = new Database();
    }

    @GET
    @Path("test")
    public String getUser(){
        String s =  db.getUser();
        System.out.println(s);
        return s;
    }


}
