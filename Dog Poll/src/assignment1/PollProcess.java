/*
 * This class operates database and includes many methods to 
 * get data from database
 * 
 */
package assignment1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This class includes Connection, Statement, preparedStatement.
 *
 * @author Phoenix
 */
public class PollProcess {

    private Connection connection = null;
    private Statement stmt = null;

    private PreparedStatement addNewDogs, countPlusOne;
    ArrayList<Dog> dogs = new ArrayList<>();
    Scanner sc = new Scanner(System.in);

    PollProcess() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.err.println("Error: mysql Drivers not found!");
        }

    }

    /**
     * Connect database, if successfully, it will return true.
     */
    public void connect() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://dev.fast.sheridanc.on.ca:3306/geq_db?"
                    + "useSSL=false", "geq_db", "Dylankeh1988");

            try {
                stmt = connection.createStatement();
            } catch (SQLException ex) {
                System.err.println("Error: cannot create statement");
            }

            try {
                addNewDogs = connection.prepareStatement(
                        "INSERT INTO poll(breedName, voteNum) values(?,?);");
                countPlusOne = connection.prepareStatement(
                        "UPDATE poll SET voteNum=voteNum+1 WHERE breedID=?;");
            } catch (SQLException ex) {
                System.err.println("Error: cannot create prepared statement");
            }

            processStart();

        } catch (SQLException e) {
            System.err.println("Error: cannot connect to database");

        }

    }

    /**
     * This method controls the process of display information, catch exception
     * and show result of the poll.
     *
     */
    public void processStart() {

        //retrieve dog ID and Name from database and display on console
        dogs = getInfoFromDB();
        displayAllDogs(dogs);

        try {

                      
            int pickedNum = Integer.parseInt(sc.nextLine());
            
            //User picked a number from list.
            if (pickedNum < dogs.size() + 1 && pickedNum > 0) {
                countPlusOne(pickedNum);
                displayResult();
            }

            //User picked a number that is not in the list
            if (pickedNum > dogs.size() + 1 || pickedNum <= 0) {
                throw new Exception("Error: Input is out of range!");
            }

            //User picked "Other" option to add a new dog
            if (pickedNum == dogs.size() + 1) {
                System.out.print("What's the other answer? ");
                String newDog = sc.nextLine();

                //User input will be checked
                if (checkRepeatedName(newDog, dogs)) {

                    //Add the new dog in list
                    addNewDogs(newDog);

                    //display result of poll
                    displayResult();

                    //No repeated name is allowed.
                } else {
                    throw new Exception("Sorry, " + newDog
                            + " is already included in the poll!");

                }
            }

            //User entered String or double type, it will catch exception
        } catch (NumberFormatException ex) {
            System.err.println("Error: Input is not an integer!");
            processStart();
        } catch (Exception ex1) {
            System.err.println(ex1.getMessage());
            processStart();
        }

    }

    /**
     * This method is for getting information of dogs from database
     *
     * @return get data from database, create Dog objects and add into
     * ArrayList.
     */
    public ArrayList getInfoFromDB() {

        ArrayList<Dog> dog = new ArrayList<>();

        try {
            ResultSet rs = stmt.executeQuery(
                    "SELECT breedID, breedName FROM poll;");

            while (rs.next()) {
                Dog defineDog = new Dog(rs.getInt("breedID"),
                        rs.getString("breedName"));

                dog.add(defineDog);
            }

        } catch (SQLException e) {
            System.err.println("Error: couldn't create statement");
        }
        return dog;
    }

    /**
     * This method is for displaying information of options on console
     *
     * @param dogs receives ArrayList of Dog objects
     */
    public void displayAllDogs(ArrayList<Dog> dogs) {

        System.out.println("\nYour favourite dog poll");
        System.out.println("------------------------");

        for (Dog element : dogs) {
            System.out.println(element.getBreedID() + ". "
                    + element.getBreedName());
        }

        System.out.println((dogs.size() + 1) + ". Other");
        System.out.print("Your vote?" + "(1-" + (dogs.size() + 1) + "): ");
    }

    /**
     * This method is for comparing user input with existing name in database
     *
     * @param dogs receives arrayList of Dog objects
     * @param name the new dog name user entered.
     * @return if the user input does not exist in database, return true.
     */
    public boolean checkRepeatedName(String name, ArrayList<Dog> dogs) {

        for (int i = 0; i < dogs.size(); i++) {

            //If the new name is same with any existing one, return false
            if (dogs.get(i).getBreedName().equalsIgnoreCase(name)) {
                return false;
            }
        }

        return true;
    }

    /**
     * This method is to add new dog in database
     *
     * @param name the new dog name user entered.
     */
    public void addNewDogs(String name) {
        try {
            
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            addNewDogs.setString(1, name);
            addNewDogs.setInt(2, 1);
            addNewDogs.executeUpdate();
            System.out.println("*** Your Vote Has Been Recorded ***");
        } catch (SQLException ex) {
            System.err.println("Error: could not add new dog");
        }
    }

    /**
     * This method adds one on the number of dog user picked
     *
     * @param number is the ID of dog user picked to vote for
     */
    public void countPlusOne(int number) {
        try {
            countPlusOne.setInt(1, number);
            countPlusOne.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error: could not add 1");
        }
    }

    /**
     * This method display the poll result
     *
     */
    public void displayResult() {
        System.out.println("\n********** Poll Results: **********");
        try {
            ResultSet rs = stmt.executeQuery(
                    "SELECT breedName, voteNum, "
                    + "concat(ROUND("
                    + "(voteNum/(select sum(voteNum) from poll))*100, 2"
                    + "),'%') as rate from poll ORDER BY voteNum DESC;");

            while (rs.next()) {

                System.out.println(rs.getString("breedName") + ": "
                        + rs.getInt("voteNum")
                        + " (" + rs.getString("rate") + ")");
            }
        } catch (SQLException ex) {
            System.out.println("Could not create statement");
        }

    }
}
