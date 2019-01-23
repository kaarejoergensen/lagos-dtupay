package models;
/**
 * @author Kåre
 */
public enum Roles {
    User(0), Merchant(1), Admin(2);

    private int identification;

    private Roles(int id){
        identification = id;
    }

    public int getIdentificationNumber(){
        return identification;
    }

    public static Roles getRole(int id){
        switch (id){
            case 1:
                return Merchant;
            case 2:
                return Admin;
            default:
                return User;
        }
    }
}
