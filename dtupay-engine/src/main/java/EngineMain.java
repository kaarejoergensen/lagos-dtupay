

import models.Roles;
import models.User;

import javax.management.relation.Role;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


public class EngineMain {


    public EngineMain(){

    }





    public static void main(String args[]){
        Database db = new Database();

        /*
        TODO: Lage ordentlig mongodb testshit
         */


        User test = new User();
        test.setFirstName("Fredrik");
        test.setLastName("asd");
        test.setCprNumber("1234");
        test.setRole(Roles.User);



        db.addUser(test);

        User getched = db.getUser("1234");
        System.out.println(getched);


    }


}
