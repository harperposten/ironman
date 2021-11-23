package ironman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Ironman {
    private static ArrayList<String> roster;
    private static List<String> fightersLeftP1;
    private static List<String> fightersLeftP2;
    private static int numFighters;
    private static String game;
    private static String previousChar;
    private static int previousWinner;
    private static boolean canUndo;

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        newGame(scan);
        String input = "";
        while(!input.equals("6")) {
            menu();
            input = scan.nextLine();
            menuChoices(input, scan);
        }
        scan.close();
    }

    /**
     * Allows the user to choose whether to start a new game or load from file
     * pre: none
     * @param scan
     * @return 1 if the user choos
     */
    private static void newGame(Scanner scan) {
        int input = 0;
        while(input != 1 && input != 2) {
            System.out.println("Enter 1 to start a new Ironman" + " or 2 to load a saved Ironman.");
            try {
                input = scan.nextInt();
            }
            catch(Exception e) {
                System.out.println("That is not a valid input.");
                scan.next();
            }
        }
        if(input == 1) {
            chooseGame(scan);
            chooseFighters(scan);
            randomize();
        }
        else {
            load(scan);
        }
    }

    /*
     * Allows the user to choose which game they are playing
     * pre: none
     * post: game is set to the chosen game, and roster is created based on that game
     */
    private static void chooseGame(Scanner scan) {
        System.out.println("Which game would you like to play?");
        int input = 0;
        while(input != 1 && input != 2) {
            System.out.println("Enter 1 for Super Smash Bros. Ultimate "
                    + "or 2 for Nickelodeon All-Star Brawl.");
            try {
                input = scan.nextInt();
            }
            catch(Exception e) {
                System.out.println("That is not a valid input.");
                scan.next();
            }
        }
        switch(input) {
            case 1 : {
                game = "SSBU";
                buildRoster("ultFighters.txt");
                break;
            }
            case 2 : {
                game = "NASB";
                buildRoster("allStarFighters.txt");
                break;
            }
        }
    }

    /*
     * Creates a roster based on the chosen game
     * pre: none
     * post: roster contains every fighter in the chosen game
     */
    private static void buildRoster(String rosterFile) {
        roster = new ArrayList<>();
        //System.out.println(new File(".").getAbsoluteFile());
        try {
            Scanner scan = new Scanner(new File(rosterFile));
            while(scan.hasNextLine()) {
                roster.add(scan.nextLine().toLowerCase());
            }
            scan.close();
        }
        catch(FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Unable to find this file: " + rosterFile);
            System.out.print("Program running in this directory: ");
            System.out.println(System.getProperty("user.dir"));
            System.out.println("Be sure the roster file is in "
                    + "that directory");
        }

        fightersLeftP1 = new ArrayList<>(roster);
    }

    /*
     * Lets the user choose if they want to remove fighters
     * pre: none
     * post: fightersLeftP1 and fightersLeftP2 have any fighters chosen removed
     */
    private static void chooseFighters(Scanner scan) {
        System.out.println("How many fighters would you like?");
        int input = 0;
        while(input < 1 || input > roster.size()) {
            System.out.println("The minimum is 1 and the maximum is " + roster.size() + ".");
            try {
                input = scan.nextInt();
            }
            catch(Exception e) {
                System.out.println("That is not a valid input.");
                scan.next();
            }
        }
        numFighters = input;
        scan.nextLine();
        if(numFighters != roster.size()) {
            System.out.println("Would you like to remove any specific fighters?");
            String strInput = "";
            while(!strInput.equals("Y") && !strInput.equals("N")) {
                System.out.println("Type Y for yes or N for no.");
                strInput = scan.nextLine().toUpperCase();
            }
            if(strInput.equals("Y")) {
                removeFighters(scan);
            }
        }
    }

    /*
     * Removes fighters chosen by user
     * pre: fightersLeft.size() > numFighters
     * post: fightersLeftP1 has any fighters chosen removed
     */
    private static void removeFighters(Scanner scan) {
        //Check preconditions
        if(fightersLeftP1.size() <= numFighters) {
            throw new IllegalStateException("Violation of precondition: removeFighters. "
                    + "You are at the minimum number of fighters.");
        }
        System.out.println("Type the name of the fighter you wish to remove, "
                + "undo to re-add the previous fighter, or done to stop removing fighters.");
        String input = scan.nextLine().toLowerCase();
        String prevRemoved = null;
        while(!input.equals("done")) {
            if(input.equals("done")) {
                break;
            }

            else if(input.equals("undo") && prevRemoved != null) {
                fightersLeftP1.add(prevRemoved);
                System.out.println(prevRemoved + " re-added.");
                prevRemoved = null;
            }
            else if(fightersLeftP1.remove(input)) {
                prevRemoved = input;
                System.out.println(input + " removed.");
            }
            else {
                System.out.println(input + " is not in the list.");
            }
            if(fightersLeftP1.size() == numFighters) {
                System.out.println("Minimum number of fighters reached.");
                input = "done";
            }
            else {
                input = scan.nextLine().toLowerCase();
            }
        }
    }

    /**
     * Randomizes the lists of fighters
     * pre: none
     * post: fightersLeftP1 and fightersLeftP2 will be randomized
     */
    private static void randomize() {
        Random random = new Random();
        ArrayList<String> tempP1 = new ArrayList<>(fightersLeftP1);
        ArrayList<String> tempP2 = new ArrayList<>(fightersLeftP1);
        fightersLeftP1 = new ArrayList<>();
        fightersLeftP2 = new ArrayList<>();
        for(int i = 0; i < numFighters; i++) {
            int randNum = random.nextInt(tempP1.size());
            fightersLeftP1.add(i, tempP1.get(randNum));
            tempP1.remove(randNum);
            randNum = random.nextInt(tempP2.size());
            fightersLeftP2.add(tempP2.get(randNum));
            tempP2.remove(randNum);
        }
    }

    /**
     * Prints menu for choices
     * pre: none
     * post: Menu printed to console
     */
    private static void menu() {
        System.out.println("\nEnter a number: ");
        System.out.println("1: Mark a win for Player 1.");
        System.out.println("2: Mark a win for Player 2.");
        System.out.println("3: Undo previous change.");
        System.out.println("4: Save.");
        System.out.println("5: See Remaining Fighters");
        System.out.println("6: End program.\n");
    }

    /**
     * Executes action chosen by user
     * pre: none
     * @param input the user's choice
     * @param scan
     * post: does actions shown in menu
     */
    private static void menuChoices(String input, Scanner scan) {
        switch(input) {
            case "1" : {
                System.out.println(fightersLeftP1.get(0));
                previousChar = fightersLeftP1.remove(0);
                previousWinner = 1;
                canUndo = true;
                System.out.println("Player 1 has won as " + previousChar + ".");
                break;
            }
            case "2" : {
                previousChar = fightersLeftP2.remove(0);
                previousWinner = 2;
                canUndo = true;
                System.out.println("Player 2 has won as " + previousChar + ".");
                break;
            }
            case "3" : {
                undo();
                break;
            }
            case "4" : {
                save(scan);
                break;
            }
            case "5" : {
                System.out.println("Player 1: " + fightersLeftP1.toString());
                System.out.println("Player 2: " + fightersLeftP2.toString());
                break;
            }
            case "6" : {
                System.out.println("Program terminated.");
                break;
            }
            default: {
                System.out.println("That is not a valid input");
                break;
            }
        }
    }

    /**
     * Readds previous fighter removed
     * pre: none
     * post: previously removed fighter is readded
     */
    private static void undo() {
        if(canUndo) {
            System.out.println("Undoing previous win.");
            if(previousWinner == 1) {
                fightersLeftP1.add(0, previousChar);
            }
            else {
                fightersLeftP2.add(0, previousChar);
            }
            canUndo = false;
        }
        else {
            System.out.println("Can't undo again.");
        }
    }

    /**
     * Saves current state to new file or overwrites other file
     * pre: none
     * @param scan
     * post: new file with current data is written, or old file is overwritten if chosen
     */
    private static void save(Scanner scan) {
        System.out.println("What would you like the file to be called?");
        String input = scan.nextLine() + ".ironman";
        try {
            File newFile = new File(input);
            if(!newFile.createNewFile()) {
                System.out.println("File already exists. Enter 1 to overwrite it or 2 to stop.");
                int over = scan.nextInt();
                while(over != 1 && over != 2) {
                    try {
                        over = scan.nextInt();
                    }
                    catch(Exception e) {
                        System.out.println("That is not a valid input.");
                        scan.next();
                    }
                }
                if(over == 2) {
                    throw new IOException();
                }
                scan.nextLine();
            }
            try {
                FileWriter myWriter = new FileWriter(input);
                myWriter.write(game + "\n" + numFighters + "\n"
                        + fightersLeftP1.toString() + "\n" + fightersLeftP2.toString());
                myWriter.close();
                System.out.println("Save successful.");
            }
            catch(IOException e) {
                System.out.println("An error occurred.");
            }
        }
        catch(IOException e) {
            System.out.println("An error occurred.");
        }
    }

    /**
     * Sets game state based on what is in file
     * pre: none
     * @param scan
     * post: game state is now set to what was in file
     */
    private static void load(Scanner scan) {
        System.out.println("What is the file name?");
        scan.nextLine();
        String input = scan.nextLine();
        if(input.length() < 8 || !input.substring(input.length() - 8).equals(".ironman")) {
            input = input + ".ironman";
        }
        try {
            Scanner fileScanner = new Scanner(new File(input));
            game = fileScanner.nextLine();
            numFighters = Integer.parseInt(fileScanner.nextLine());
            String temp = fileScanner.nextLine();
            temp = temp.substring(1, temp.length() - 1);
            fightersLeftP1 = new ArrayList<>(Arrays.asList(temp.split(", ")));
            temp = fileScanner.nextLine();
            temp = temp.substring(1, temp.length() - 1);
            fightersLeftP2 = new ArrayList<>(Arrays.asList(temp.split(", ")));
            fileScanner.close();
        }
        catch(Exception e) {
            System.out.println("Invalid file.");
            newGame(scan);
        }
    }
}

/* Todo:
 * Stage select
 * Make GUI
 */