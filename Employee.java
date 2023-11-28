public class Employee {
    // instance variables
    public String nameSurname;
    public String role;
    public int promotionPoints;

    // constructors
    Employee() {

    }
    Employee(String nameSurname, String role) {
        this.nameSurname = nameSurname;
        this.role = role;
        this.promotionPoints = 0;
    }

    // methods
    @Override
    public String toString(){ // returns the key for the branch class
        return nameSurname;
    }

}
