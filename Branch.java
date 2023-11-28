public class Branch {
    // instance variables
    public String districtName;
    public String cityName;
    public HashTable<Employee> employees;
    public int courierCount, cashierCount, cookCount;
    public int monthlyBonuses;
    public int overallBonuses;
    public Employee manager;
    public myQueue<Employee> cooksPromoting;
    public myQueue<Employee> cashiersPromoting;
    public myQueue<Employee> cooksWillBeDismissed;
    public myQueue<Employee> cashiersWillBeDismissed;
    public myQueue<Employee> managerWillBeDismissed;
    public myQueue<Employee> couriersWillBeDismissed;

    // constructors
    Branch() {
    }
    Branch(String cityName, String districtName) {
        this.cityName = cityName;
        this.districtName = districtName;
        this.employees = new HashTable<>();
        this.courierCount = 0;
        this.cashierCount = 0;
        this.cookCount = 0;
        this.monthlyBonuses = 0;
        this.overallBonuses = 0;

        cooksPromoting = new myQueue<>();
        cashiersPromoting = new myQueue<>();
        cooksWillBeDismissed = new myQueue<>();
        cashiersWillBeDismissed = new myQueue<>();
        managerWillBeDismissed = new myQueue<>();
        couriersWillBeDismissed = new myQueue<>();
    }

    // ------------------------ METHODS ---------------------------

    @Override
    public String toString(){ // returns the key for the branch class
        return cityName+districtName;
    }

    public void updateCounts(String role) {
        if (role.equals("COOK")) cookCount++;
        else if (role.equals("CASHIER")) cashierCount++;
        else if (role.equals("COURIER")) courierCount++;
    }


    // method to update the employee if it is in any of the queues
    public void updateQueues(Employee changedEmployee, int newPromPoints) {
        int initialPromPoints = changedEmployee.promotionPoints;
        changedEmployee.promotionPoints = newPromPoints;

        if (changedEmployee.role.equals("MANAGER")) {
            if ((initialPromPoints > -5) && (newPromPoints <= -5)) { // we want to dismiss the manager
                managerWillBeDismissed.enqueue(changedEmployee);
            }
            else if ((initialPromPoints <= -5) && (newPromPoints > -5)) { // remove manager from dismissal
                managerWillBeDismissed.dequeue();
            }
        }
        else if (changedEmployee.role.equals("COOK")) {
            if ((initialPromPoints > -5) && (newPromPoints <= -5)) { // we want to dismiss the cook
                cooksWillBeDismissed.enqueue(changedEmployee);
            }
            else if ((initialPromPoints <= -5) && (newPromPoints > -5)) { // remove the cook from dismissal
                cooksWillBeDismissed.delete(changedEmployee);
            }
            else if ((initialPromPoints >= 10) && (newPromPoints < 10)) { // remove the cook from promotion queue
                cooksPromoting.delete(changedEmployee);
            }
            else if (initialPromPoints < 10 && newPromPoints >= 10) { // enqueue promotion
                cooksPromoting.enqueue(changedEmployee);
            }
        }
        else if (changedEmployee.role.equals("CASHIER")) {
            if ((initialPromPoints > -5) && (newPromPoints <= -5)) { // dismiss the cashier
                cashiersWillBeDismissed.enqueue(changedEmployee);
            }
            else if ((initialPromPoints <= -5) && (newPromPoints > -5)) { // remove the cashier from dismissal
                cashiersWillBeDismissed.delete(changedEmployee);
            }
            else if ((initialPromPoints >= 3) && (newPromPoints < 3)) { // remove the cashier from prom. queue
                cashiersPromoting.delete(changedEmployee);
            }
            else if (initialPromPoints < 3 && newPromPoints >= 3) { // enqueue promotion
                cashiersPromoting.enqueue(changedEmployee);
            }
        }
        else { // changed employee is a courier
            if ((initialPromPoints > -5) && (newPromPoints <= -5)) {
                couriersWillBeDismissed.enqueue(changedEmployee);
            }
            else if ((initialPromPoints <= -5) && (newPromPoints > -5)) {
                couriersWillBeDismissed.delete(changedEmployee);
            }

        }
    }

    // a method to check the queues for promotions and dismissals
    public void updateRoles() {
        if (!cashiersPromoting.isEmpty() && cashierCount > 1) { // if we can promote a cashier
            promoteCashier(cashiersPromoting.dequeue());
        }
        if (!managerWillBeDismissed.isEmpty()) { // if we want to dismiss the manager
            if (!cooksPromoting.isEmpty() && cookCount > 1)
                dismissManager(manager);
        }
        if (!cooksWillBeDismissed.isEmpty()) { // if we want to dismiss a cook
            if (cookCount > 1) dismissCook(cooksWillBeDismissed.dequeue()); // if we can directly dismiss
            else if (!cashiersPromoting.isEmpty() && cashierCount > 1) { // if we can promote a cashier and then dismiss
                promoteCashier(cashiersPromoting.dequeue());
                dismissCook(cooksWillBeDismissed.dequeue());
            }
        }
        if (!cashiersWillBeDismissed.isEmpty()) { // if we want to dismiss a cashier
            if (cashierCount > 1) dismissCashier(cashiersWillBeDismissed.dequeue());
        }
        if (!couriersWillBeDismissed.isEmpty()) { // if we want to dismiss a courier
            if (courierCount > 1) dismissCourier(couriersWillBeDismissed.dequeue());
        }
    }


    // leaving and dismissal methods
    public void leaveManager(Employee leavingManager) {
        employees.remove(leavingManager);
        System.out.println(leavingManager + " is leaving from branch: " + this.districtName + ".");
        if (!managerWillBeDismissed.isEmpty())
            managerWillBeDismissed.dequeue();
        manager = cooksPromoting.dequeue();
        System.out.println(manager.nameSurname + " is promoted from Cook to Manager.");
        manager.role = "MANAGER";
        manager.promotionPoints -= 10;
        cookCount--;
    }

    public void dismissManager(Employee dismissedManager) {
        System.out.println(dismissedManager + " is dismissed from branch: " + this.districtName + ".");
        if (!managerWillBeDismissed.isEmpty())
            managerWillBeDismissed.dequeue();
        employees.remove(dismissedManager);
        manager = cooksPromoting.dequeue();
        System.out.println(manager.nameSurname + " is promoted from Cook to Manager.");
        // promote the cook, message will be printed
        manager.role = "MANAGER";
        manager.promotionPoints -= 10;
        cookCount--;
    }

    public void leaveCook(Employee leavingCook) {
        employees.remove(leavingCook);
        System.out.println(leavingCook + " is leaving from branch: " + this.districtName + ".");
        cookCount--;
        if (cooksPromoting.contains(leavingCook)) cooksPromoting.delete(leavingCook);
        else if (cooksWillBeDismissed.contains(leavingCook)) cooksWillBeDismissed.delete(leavingCook);
    }
    public void dismissCook(Employee dismissingCook) {
        employees.remove(dismissingCook);
        System.out.println(dismissingCook + " is dismissed from branch: " + this.districtName + ".");
        cookCount--;
        if (cooksPromoting.contains(dismissingCook)) cooksPromoting.delete(dismissingCook);
        else if (cooksWillBeDismissed.contains(dismissingCook)) cooksWillBeDismissed.delete(dismissingCook);
    }

    public void leaveCashier(Employee leavingCashier) {
        employees.remove(leavingCashier);
        System.out.println(leavingCashier + " is leaving from branch: " + this.districtName + ".");
        cashierCount--;
        if (cashiersPromoting.contains(leavingCashier)) cashiersPromoting.delete(leavingCashier);
        else if (cashiersWillBeDismissed.contains(leavingCashier)) cashiersWillBeDismissed.delete(leavingCashier);
    }
    public void dismissCashier(Employee dismissCashier) {
        employees.remove(dismissCashier);
        System.out.println(dismissCashier + " is dismissed from branch: " + this.districtName + ".");
        cashierCount--;
        if (cashiersPromoting.contains(dismissCashier)) cashiersPromoting.delete(dismissCashier);
        else if (cashiersWillBeDismissed.contains(dismissCashier)) cashiersWillBeDismissed.delete(dismissCashier);
    }

    public void leaveCourier(Employee leavingCourier) {
        employees.remove(leavingCourier);
        System.out.println(leavingCourier + " is leaving from branch: " + this.districtName + ".");
        if (couriersWillBeDismissed.contains(leavingCourier)) couriersWillBeDismissed.delete(leavingCourier);
        courierCount--;
    }
    public void dismissCourier(Employee dismissedCourier) {
        employees.remove(dismissedCourier);
        System.out.println(dismissedCourier + " is dismissed from branch: " + this.districtName + ".");
        if (couriersWillBeDismissed.contains(dismissedCourier)) couriersWillBeDismissed.delete(dismissedCourier);
        courierCount--;
    }

    // we don't need a promote manager, cook, or courier method since cookPromotion is handled in the leaveManager method
    // and the other two basically can't promote
    private void promoteCashier(Employee promotingCashier) {
        promotingCashier.role = "COOK";
        cashierCount--;
        promotingCashier.promotionPoints -= 3;
        cookCount++;
        System.out.println(promotingCashier + " is promoted from Cashier to Cook.");
        if (promotingCashier.promotionPoints >= 10) {
            cooksPromoting.enqueue(promotingCashier);
            if (cooksPromoting.peek() == promotingCashier) { // if our cashier who has just become the cook is the only employee who is able to promote to manager
                updateRoles();
            }
        }
    }
}