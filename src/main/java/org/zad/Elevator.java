package org.zad;

import java.util.ArrayList;
import java.util.Random;

public class Elevator {
    protected final int limit;
    private final int max_floor;
    private final int min_floor;
    private final int base;
    private int cooldown;
    private int num_of_people_inside = 0;
    private EDirection direction;
    private int location;
    public ArrayList<Request> outer_requests = new ArrayList<>();
    public ArrayList<Request> inner_requests = new ArrayList<>();
    private int num_of_unique_requests = 0;
    private int wait = 0;
    public int index;
    public Elevator(int elevator_limit, int starting_point, int min_floor, int max_floor, int i){
        limit = elevator_limit;
        direction = EDirection.IDLE;
        location = starting_point;
        this.min_floor = min_floor;
        this.max_floor = max_floor;
        index = i;
        base = starting_point;
    }

    // add request from ElevatorSystem, merge it if there is already one like this
    public void addOuterRequest(int floor, EDirection direction, int num_of_people){
        for(Request request: outer_requests){
            if (request.floor == floor){
                request.num_of_people += num_of_people;
                return;
            }
        }
        outer_requests.add(new Request(floor, direction, num_of_people));
        for(Request request: inner_requests){
            if (request.floor == floor){
                return;
            }
        }
        num_of_unique_requests += 1;
    }

    // add request from people going inside the elevator, merge it if there is already one like this
    private void addInnerRequest(int floor, EDirection direction, int num_of_people){
        for(Request request: inner_requests){
            if (request.floor == floor){
                request.num_of_people += num_of_people;
                return;
            }
        }
        inner_requests.add(new Request(floor, direction, num_of_people));
        for(Request request: outer_requests){
            if (request.floor == floor){
                return;
            }
        }
        num_of_unique_requests += 1;
    }

    // after X steps (default = 3) elevator goes back where it started at the start of the simulation (mostly floor 0)
    public void decrementCooldown(){
        if (cooldown > 0 && direction == EDirection.IDLE){
            cooldown--;
        } else {
            addInnerRequest(base, EDirection.getDirection(base - location), 0);
            direction = EDirection.getDirection(base - location);
        }
    }

    // move elevator each step of simulation, if controller (ElevatorSystem) breaks and would give wrong request to the elevator,
    // then elevator wouldn't be stuck on the top or bottom floor and would cycle from top to bottom and bottom to top
    public void move() {
        if (direction == EDirection.UP) {
            if (location < max_floor){
                location += 1;
            }
            else {
                direction = EDirection.DOWN;
            }
        }
        else if (direction == EDirection.DOWN) {
            if (location > min_floor){
                location -= 1;
            }
            else {
                direction = EDirection.UP;
            }
        }
    }

    public Message perform_actions() {
        Message message = new Message();
        boolean isRequest = false;
        Random random = new Random();
        // check if someone wants to leave on current floor
        for(Request request: inner_requests){
            if (request.floor == location){
                num_of_people_inside -= request.num_of_people;
                inner_requests.remove(request);
                isRequest = true;
                break;
            }
        }

        // check if someone wants to go inside
        for(Request request: outer_requests){
            if (request.floor == location){
                outer_requests.remove(request);
                // this part is simulating too many people for one elevator, some people go in and
                // everybody else is waiting after clicking summon elevator button again
                if (request.num_of_people + num_of_people_inside > limit){
                    message.overflow = request.num_of_people + num_of_people_inside - limit;
                    message.taken = limit - num_of_people_inside;
                    for(int i = 0; i < limit - num_of_people_inside; i++){
                        int request_floor;
                        if (request.direction == EDirection.UP){
                            request_floor = random.nextInt(max_floor - location) + location + 1;
                            addInnerRequest(request_floor, request.direction, 1);
                        }
                        else if (request.direction == EDirection.DOWN){
                            request_floor = random.nextInt(location - min_floor) + min_floor;
                            addInnerRequest(request_floor, request.direction, 1);
                        }
                    }
                    num_of_people_inside = limit;
                }
                // part where everybody goes in, request is removed
                else {
                    message.taken = request.num_of_people;
                    for(int i = 0; i < request.num_of_people; i++){
                        int request_floor;
                        if (request.direction == EDirection.UP){
                            request_floor = random.nextInt(max_floor - location) + location + 1;
                            addInnerRequest(request_floor, request.direction, 1);
                        }
                        else if (request.direction == EDirection.DOWN){
                            request_floor = random.nextInt(location - min_floor) + min_floor;
                            addInnerRequest(request_floor, request.direction, 1);
                        }
                    }
                    num_of_people_inside += request.num_of_people;
                }
                isRequest = true;
                message.performedAction = true;
                message.direction = request.direction;
                this.direction = request.direction;
                break;
            }
        }
        // if someone left or went in, elevator needs to wait one step (adjustable), because people cannot teleport to/from elevator ;)
        if (isRequest) {
            num_of_unique_requests--;
            wait = 1;
        }
        // if there is noone in elevator then set direction to IDLE,
        // elevator doesn't need to go all the way to top/bottom (C-LOOK Algorithm)
        if (num_of_unique_requests == 0){
            this.direction = EDirection.IDLE;
            cooldown = 3;
        }
        return message;
    }

    // check if elevator is 80% "full", elevator can only assume how many people are inside
    public boolean requestLimitReached(){
        return num_of_unique_requests > 0.8 * limit;
    }
    public int getCooldown() { return cooldown; }
    public int getWait() {
        return wait;
    }
    public void decrementWait() {
        this.wait--;
    }
    public EDirection getDirection() {
        return direction;
    }
    public void setDirection(EDirection direction) {
        this.direction = direction;
    }
    public int getLocation() {
        return location;
    }
    public int getBase() {
        return base;
    }
    public int getNum_of_unique_requests() {
        return num_of_unique_requests;
    }
    public int getNum_of_people_inside() {
        return num_of_people_inside;
    }

}
