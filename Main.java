import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * This project implements a generic hash table and with the help of 2 of them
 * it stores the branches of a company and the employee for each branch.
 * With the help of special methods, it logs the necessary actions.
 */

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        File file1 = new File(args[0]);
        File file2 = new File(args[1]);

        File output = new File(args[2]);
        FileOutputStream fileOutputStream = new FileOutputStream(output);
        PrintStream printStream = new PrintStream(fileOutputStream);
        System.setOut(printStream);

        Scanner initialScanner = new Scanner(file1);

        HashTable<Branch> mainHashTable = new HashTable<>();

        // the initial state of the company is read from the first input file
        while (initialScanner.hasNextLine()) {
            try {
                String cityName = initialScanner.next();
                cityName = cityName.substring(0, cityName.length() - 1);

                String districtName = initialScanner.next();
                districtName = districtName.substring(0, districtName.length() - 1);

                String nameSurname = initialScanner.next();
                nameSurname += " " + initialScanner.next();
                nameSurname = nameSurname.substring(0, nameSurname.length() - 1);

                String role = initialScanner.next();

                String branchKey = cityName + districtName;

                Branch currentBranch = mainHashTable.findInTable(branchKey);

                if (currentBranch == null) { // if the branch is not created already
                    mainHashTable.insert(new Branch(cityName, districtName));
                    currentBranch = mainHashTable.findInTable(branchKey);
                }
                currentBranch.employees.insert(new Employee(nameSurname, role));
                currentBranch.updateCounts(role);
                if (role.equals("MANAGER"))
                    currentBranch.manager = currentBranch.employees.findInTable(nameSurname);
            }
            catch (NoSuchElementException ignored) {}
        }

        Scanner secondScanner = new Scanner(file2);
        secondScanner.nextLine();

        String currentLine;
        // operations after the initial state is read and executed
        while (secondScanner.hasNextLine()) {
            try {
                currentLine = secondScanner.nextLine();
                String[] currentLineList = currentLine.split(",");

                if ( currentLineList.length == 4 ) { // if the operation is either ADD or PERFORMANCE_UPDATE
                    String cityName = currentLineList[0].split(" ")[1];
                    String districtName = currentLineList[1].strip();
                    String nameSurname = currentLineList[2].strip();
                    if (currentLineList[0].startsWith("ADD")) { // add operation
                        String role = currentLineList[3].strip();

                        Branch branch = mainHashTable.findInTable(cityName+districtName);
                        Employee emp = branch.employees.findInTable(nameSurname);
                        if (emp != null) { // employee already is in the branch
                            System.out.println("Existing employee cannot be added again.");
                        }
                        else {
                            branch.employees.insert(new Employee(nameSurname,role));
                            branch.updateCounts(role);
                            branch.updateRoles();
                        }
                    }
                    else { // performance_update operation
                        int performancePoint = Integer.parseInt(currentLineList[3].strip());

                        Branch currentBranch = mainHashTable.findInTable(cityName+districtName);
                        Employee currentEmployee = currentBranch.employees.findInTable(nameSurname);

                        if (currentEmployee == null) {
                            System.out.println("There is no such employee.");
                        }
                        else { // updated the performance point variable for the employee, check promotions and dismissals
                            // add the remaining money to bonuses
                            if (performancePoint > 0) {
                                int newPromotionPoints = (int) (performancePoint / 200.0);
                                currentBranch.updateQueues(currentEmployee,currentEmployee.promotionPoints+newPromotionPoints);
                                currentBranch.updateRoles();
                                currentBranch.monthlyBonuses += performancePoint % 200;
                                currentBranch.overallBonuses += performancePoint % 200;
                            }
                            else { // negative performance point
                                int newPromPoints = (int) (performancePoint / 200.0);
                                currentBranch.updateQueues(currentEmployee,currentEmployee.promotionPoints + newPromPoints);
                                currentBranch.updateRoles();
                            }
                        }
                    }
                }
                else if ( currentLineList.length == 3 ) { // if the operation is LEAVE
                    String cityName = currentLineList[0].split(" ")[1];
                    String districtName = currentLineList[1].strip();
                    String nameSurname = currentLineList[2].strip();

                    Branch currentBranch = mainHashTable.findInTable(cityName+districtName);
                    Employee leavingEmployee = currentBranch.employees.findInTable(nameSurname);

                    if (leavingEmployee == null) {
                        System.out.println("There is no such employee.");
                    }
                    else { // check if we can let the employee leave the branch

                        if (leavingEmployee.role.equals("MANAGER")) {
                            if (!currentBranch.cooksPromoting.isEmpty() && (currentBranch.cookCount > 1)) {
                                currentBranch.leaveManager(leavingEmployee);
                            }
                            else { // there is no replacement for the manager, give bonus
                                if (!(leavingEmployee.promotionPoints <= -5)) { // if it is not in dismissal queue
                                    currentBranch.overallBonuses += 200;
                                    currentBranch.monthlyBonuses += 200;
                                }
                            }
                        }
                        else if (leavingEmployee.role.equals("COOK")) {
                            if ((currentBranch.cookCount > 1)) {
                                currentBranch.leaveCook(leavingEmployee);
                            }
                            else { // there is no replacement for the cook, give bonus
                                if (!(leavingEmployee.promotionPoints <= -5)) { // if it is not in dismissal queue
                                    currentBranch.overallBonuses += 200;
                                    currentBranch.monthlyBonuses += 200;
                                }
                            }
                        }
                        else if (leavingEmployee.role.equals("CASHIER")) {
                            if (currentBranch.cashierCount > 1) {
                                currentBranch.leaveCashier(leavingEmployee);
                            }
                            else { // there is no replacement for the cashier, give bonus
                                if (!(leavingEmployee.promotionPoints <= -5)) { // if it is not in dismissal queue
                                    currentBranch.overallBonuses += 200;
                                    currentBranch.monthlyBonuses += 200;
                                }
                            }
                        }
                        else if (leavingEmployee.role.equals("COURIER")) {
                            if (currentBranch.courierCount > 1) {
                                currentBranch.leaveCourier(leavingEmployee);
                            }
                            else { // there is no replacement for the courier, give bonus
                                if (!(leavingEmployee.promotionPoints <= -5)) { // if it is not in dismissal queue
                                    currentBranch.overallBonuses += 200;
                                    currentBranch.monthlyBonuses += 200;
                                }
                            }
                        }
                    }
                    currentBranch.updateRoles();
                }
                // if the operation is PRINT_MONTHLY_BONUSES or PRINT_OVERALL_BONUSES or PRINT_MANAGER
                else if ( currentLineList.length == 2 ) {
                    if (currentLineList[0].startsWith("PRINT_MONTHLY_BONUSES")) { // monthly_bonuses operation
                        String cityName = currentLineList[0].split(" ")[1];
                        String districtName = currentLineList[1].strip();

                        Branch currentBranch = mainHashTable.findInTable(cityName+districtName);

                        System.out.println("Total bonuses for the "+
                                currentBranch.districtName +" branch this month are: " + currentBranch.monthlyBonuses);
                    }
                    else if (currentLineList[0].startsWith("PRINT_OVERALL_BONUSES")) { // overall_bonuses operation
                        String cityName = currentLineList[0].split(" ")[1];
                        String districtName = currentLineList[1].strip();

                        Branch currentBranch = mainHashTable.findInTable(cityName+districtName);

                        System.out.println("Total bonuses for the "+
                                currentBranch.districtName +" branch are: " + currentBranch.overallBonuses);
                    }
                    else if (currentLineList[0].startsWith("PRINT_MANAGER")) { // print_manager operation
                        String cityName = currentLineList[0].split(" ")[1];
                        String districtName = currentLineList[1].strip();

                        Branch currentBranch = mainHashTable.findInTable(cityName+districtName);

                        System.out.println("Manager of the "+ currentBranch.districtName
                                +" branch is " + currentBranch.manager.nameSurname + ".");
                    }
                }
                else { // end of the month
                    if (currentLineList[0].isEmpty()) { // reached an empty line
                        mainHashTable.clearMonthlyBonuses();
                    }
                }
            }
            catch (NoSuchElementException ignored) {}
        }

        initialScanner.close();
        secondScanner.close();
    }
}