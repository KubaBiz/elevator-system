package org.zad;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

import static java.lang.Thread.sleep;

public class Main {
    private static ElevatorSystem system;
    private static final int[] floors = new int[2];
    private static int num_of_elevators = -1, elevator_limit = 0, numbers_width = 0;
    private static Elevator[] elevators;
    // first argument - number of elevators
    // second argument - people limit in single elevator
    // third/fourth argument - min/max floors
    public static void main(String[] args)
    {
        catchWrongArguments(args);
        system = new ElevatorSystem(num_of_elevators, elevator_limit, floors);
        elevators = system.getElevators();
        commandLineInterface();
        System.exit(0);
    }
    private static void catchWrongArguments(String[] args){
        if (args.length == 4) {
            String firstArg = args[0];
            String secondArg = args[1];
            String thirdArg = args[2];
            String fourthArg = args[3];
            try {
                num_of_elevators = Integer.parseInt(firstArg);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format in first argument");
                System.exit(1);
            }
            try {
                elevator_limit = Integer.parseInt(secondArg);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format in second argument");
                System.exit(2);
            }
            try {
                floors[0] = Integer.parseInt(thirdArg);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format in third argument");
                System.exit(3);
            }
            try {
                floors[1] = Integer.parseInt(fourthArg);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format in fourth argument");
                System.exit(4);
            }
            if (num_of_elevators > 16 || num_of_elevators < 0){
                System.err.println("First argument (number of elevators) must be integer within range (0,16)");
                System.exit(11);
            }
            if (elevator_limit < 1){
                System.err.println("Second argument (elevator limit) must be integer greater than 0");
                System.exit(12);
            }
            if (floors[0] > floors[1]){
                System.err.println("Third argument must be lower or equal to fourth argument");
                System.exit(13);
            }
            int length1 = thirdArg.length();
            int length2 = fourthArg.length();
            numbers_width = Math.max(length1, length2);
        } else {
            System.err.println("Wrong number of arguments provided. [num_of_elevators, elevator_limit, min_floor, max_floor]");
            System.exit(10);
        }
    }

    private static void commandLineInterface(){
        String line = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        boolean view_simulation = true, tried_step = false;

        do {
            try {
                if (view_simulation){
                    System.out.println(visualization());
                    view_simulation = false;
                }
                System.out.println("Enter one of these: pickup, step, status, exit");
                System.out.print("==> ");
                line = in.readLine().trim();
                String[] tokens = line.split("\\s+");
                switch (tokens[0]) {
                    case "pickup":
                        if (tokens.length == 4) {
                            int floor, num_of_people;
                            EDirection direction;
                            try {
                                floor = Integer.parseInt(tokens[1]);
                                direction = EDirection.getDirection(Integer.parseInt(tokens[2]));
                                num_of_people = Integer.parseInt(tokens[3]);
                            } catch (NumberFormatException e) {
                                System.out.println("Arguments must be integers");
                                break;
                            }
                            if (direction == EDirection.IDLE){
                                System.out.println("You need to specify where you want to go");
                                break;
                            }
                            if (floor < floors[0] || floor > floors[1]
                                    || (floor == floors[0] && direction == EDirection.DOWN)
                                    || (floor == floors[1] && direction == EDirection.UP) ){
                                System.out.println("Target floor out of reach");
                                break;
                            }
                            if (num_of_people < 0){
                                System.out.println("Here we don't allow negative people!");
                            }
                            System.out.println("F:" + floor + " D:" + direction.toString() + " N:" + num_of_people);
                            system.pickup(floor, direction, num_of_people);
                        } else {
                            System.out.println("Wrong number of arguments for pickup command. " +
                                    "I need floor, direction and number of people");
                        }
                        break;
                    case "step":
                        if (tokens.length == 2){
                            int steps;
                            try {
                                steps = Integer.parseInt(tokens[1]);
                            } catch (NumberFormatException e) {
                                System.out.println("Argument must be integer");
                                break;
                            }
                            if (steps < 1){
                                System.out.println("I cannot move to the past YET");
                                break;
                            }
                            tried_step = true;
                            for (int i = 1; i <= steps; i++){
                                system.step();
                                System.out.println("Performed step " + i);
                                System.out.println(visualization());
                                sleep(350);
                            }
                        } else {
                            if (!tried_step) {
                                System.out.println("What about giving step another argument like 10? \"step 10\"");
                            }
                            system.step();
                            view_simulation = true;
                        }
                        break;
                    case "status":
                        ArrayList<ElevatorInfo> status =  system.status();
                        System.out.println(status);
                        break;
                    case "exit":
                        break;
                    default:
                        System.out.println("Unknown command " + tokens[0]);
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.err);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        while (!Objects.equals(line, "exit"));
    }

    private static String visualization(){
        StringBuilder vis = new StringBuilder();
        vis.append(" ").append(padLeft("", numbers_width)).append(" ");
        for (int i = 0; i < elevators.length ; i++){
            vis.append(padLeft("", 4 - String.valueOf(i).length()))
                    .append(padLeft(Integer.toString(i), String.valueOf(elevators[i].getNum_of_people_inside()).length()))
                    .append("     ");
        }
        vis.append("\n");
        for (int i = floors[1]; i>floors[0]-1; i--){
            vis.append("[").append(padLeft(Integer.toString(i), numbers_width)).append("]");
            for (Elevator elevator : elevators){
                vis.append(" { ");
                if (elevator.getLocation() == i){
                    vis.append(elevator.getNum_of_people_inside()).append(" } ")
                            .append(elevator.getDirection().toChar()).append(" ");
                } else {
                    vis.append(padLeft("", String.valueOf(elevator.getNum_of_people_inside()).length()))
                            .append(" } ").append("  ");
                }
            }

            vis.append("\n");
        }
        vis.deleteCharAt(vis.length() - 1);
        return vis.toString();
    }
    private static String padLeft(String str, int width) {
        int spacesToAdd = width - str.length();
        return " ".repeat(Math.max(0, spacesToAdd)) + str;
    }
}