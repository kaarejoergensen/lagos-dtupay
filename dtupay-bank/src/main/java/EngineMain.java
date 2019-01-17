

public class EngineMain {

        public EngineMain(){

        }



       /*
        TODO: Lage ordentlig mongodb testshit
         */

    /*
    public static void main(String args[]){
        Database db = new Database();

        TokenProvider tkn = new TokenProvider();



        User test = new User();
        test.setUsername("Fredrik");
        test.setCprNumber("1234");
        test.setRole(Roles.User);

        db.addUser(test);


        String token = tkn.issueToken(test.getUsername(), test.getCprNumber(),5);
        db.addToken(test.getUsername(), token);

        System.out.println(token);
        System.out.println("Token added! (maybe)");

        List<String> tokenz = db.getTokens(test.getUsername());
        System.out.println(tokenz.size());
        for(String s : tokenz){
            System.out.println(s);
        }

    }
    */

}
