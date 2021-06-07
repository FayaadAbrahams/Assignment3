package za.ac.cput.assignment3project;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author Fayaad Abrahams - 218221630 -> Assignment 3 Main File to open and
 * retrieve data from ser File and printing to text Files
 */
public class runSerToText {

    private ObjectInputStream input;

    ArrayList<Customer> cusList = new ArrayList<Customer>();
    ArrayList<Supplier> supList = new ArrayList<Supplier>();

    //Opens the ser File
    public void openSerFile() {
        try {
            input = new ObjectInputStream(new FileInputStream("stakeholder.ser"));
            System.out.println("*** ser File opened for reading ***");
        } catch (IOException ioe) {
            System.out.println("Error opening ser File: " + ioe.getMessage());
        }
    }

    //Closes the ser File to prevent memory leaks
    public void closeSerFile() {
        try {
            input.close();
        } catch (IOException ioe) {
            System.out.println("Error closing ser File: " + ioe.getMessage());
        }
    }

    //Adds objects to ArrayLists
    public void readFromSerFile() {
        try {
            Object ob = null;
            while (!(ob = input.readObject()).equals(null)) {
                if (ob instanceof Customer) {
                    cusList.add((Customer) ob);
                    System.out.println("Adding To Customer: " + ((Customer) ob).getFirstName());
                }
                if (ob instanceof Supplier) {
                    supList.add((Supplier) ob);
                    System.out.println("Adding To Supplier: " + ((Supplier) ob).getName());
                }
            }
            System.out.println("Completed File Read ");
        } catch (IOException ioe) {
            System.out.println("*** EOF Reached ***");
        } catch (ClassNotFoundException ex) {
            System.out.println("Class not Found: " + ex.getMessage());;
        } finally {
            //Closes file, so there is no memory leaks
            closeSerFile();
        }
    }

    //This is where the object data is sorted for 
    //writing to text 
    public void sortCus() {
        Collections.sort(cusList, (c1, c2) -> {
            return c1.getStHolderId().compareTo(c2.getStHolderId());
        });
    }

    private int getAgeFormat(String date) {
        LocalDate d1 = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate d2 = LocalDate.now();
        return Period.between(d1, d2).getYears();
    }

    public String formatBirthDate(Customer c) {
        DateFormat fm = new SimpleDateFormat("dd MMM yyyy");
        try {
            Date dob = new SimpleDateFormat("yyyy-MM-dd").parse(c.getDateOfBirth());
            return fm.format(dob);
        } catch (ParseException e) {
            System.out.println("Error converting the Date of Birth" + e.getMessage());
        }
        return null;
    }

    public void sortSupList() {
        Collections.sort(supList, (c1, c2) -> {
            return c1.getName().compareTo(c2.getName());
        });
    }

    //This function prints the formated data to the customerOutFile.txt
    public void writeCusToFile() {
        try {
            FileWriter fw = new FileWriter("customerOutFile.txt");
            fw.write("============================ CUSTOMERS ============================\n");
            fw.write(String.format("%-10s\t%-10s\t%-10s\t%-15s\t%-10s\n", "ID", "Name", "Surname", "Date of birth", "Age"));
            fw.write("===================================================================\n");
            for (Customer cus : cusList) {
                String output = String.format("%-10s\t%-10s\t%-10s\t%-15s\t%-10s", cus.getStHolderId(), cus.getFirstName(), cus.getSurName(), formatBirthDate(cus), getAgeFormat(cus.getDateOfBirth()));
                fw.write(output + "\n");
            }
            fw.write("\nNumber of customers who can rent: " + cusList.stream().filter(Customer::getCanRent).collect(Collectors.toList()).size() + "\n");
            fw.write("\nNumber of customers who cannot rent: " + cusList.stream().filter(c -> !c.getCanRent()).collect(Collectors.toList()).size());
            fw.close();
        } catch (Exception e) {
            System.out.println("Found an Error: " + e.getMessage());
            System.out.println("Error Writing to file");
        }
    }

    //This function prints the formated data to the supplierOutFile.txt
    public void writeSupToFile() {
        try {
            FileWriter fw = new FileWriter("supplierOutFile.txt");
            fw.write("========================== SUPPLIERS  ============================\n");
            fw.write(String.format("%-15s\t%-15s\t%-15s\t%-15s\n", "ID", "Name", "Prod Type", "Description"));
            fw.write("==================================================================\n");
            for (Supplier sup : supList) {
                String output = String.format("%-15s\t%-20s\t%-15s\t%-15s", sup.getStHolderId(), sup.getName(), sup.getProductType(), sup.getProductDescription());
                fw.write(output + "\n");
            }
            fw.close();
        } catch (Exception e) {
            System.out.println("Found an Error: " + e.getMessage());
            System.out.println("error writing to file");
        }
    }

    public static void main(String[] args) {
        //Creates an object of the class, sorts and stores 
        //the information to seperate txt files
        runSerToText serObj = new runSerToText();
        serObj.openSerFile();
        serObj.readFromSerFile();
        serObj.sortCus();
        serObj.writeCusToFile();
        serObj.sortSupList();
        serObj.writeSupToFile();
    }
}
