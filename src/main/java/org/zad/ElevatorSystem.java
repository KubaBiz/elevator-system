package org.zad;

import java.util.ArrayList;

public class ElevatorSystem {
    private final Elevator[] elevators;
    public int max_distance = 0;
    private final ArrayList<AssignedRequest> assignedRequests = new ArrayList<>();
    private final ArrayList<Request> waiting_list = new ArrayList<>();
    private final ArrayList<Request> removal_list = new ArrayList<>();
    public ElevatorSystem(int num_elevators, int elevator_limit, int[] floors){
        int[] tmp = {floors[0], floors[1]};
        while (tmp[0] <= tmp[1]){
            max_distance++;
            tmp[0]++;
        }
        int starting_point = floors[0] <= 0 && floors[1] >= 0 ? 0 : floors[0];
        elevators = new Elevator[num_elevators];
        for (int i = 0; i < num_elevators; i++) {
            elevators[i] = new Elevator(elevator_limit, starting_point, floors[0], floors[1], i);
        }
        System.out.println(max_distance);
    }

    // send pickup request to controller and handle it
    public void pickup(int floor, EDirection direction, int num_of_people){
        // ASSIGNED REQUESTS - requests assigned by controller (ElevatorSystem) in Elevator named outer_requests
        // check if any assigned request is not the same, merge them if they are
        for (AssignedRequest request : assignedRequests) {
            if (request.floor == floor && request.direction == direction) {
                request.elevator.addOuterRequest(floor, direction, num_of_people);
                request.num_of_people += num_of_people;
                removal_list.add(new Request(floor, direction, num_of_people));
                return;
            }
        }
        // check if any elevator is IDLE on the same floor as pickup and has no assigned requests
        for (Elevator elevator : elevators){
            if (elevator.getLocation() == floor && elevator.getDirection() == EDirection.IDLE && !assignedRequests.contains(new AssignedRequest(floor, direction, num_of_people, elevator))){
                elevator.addOuterRequest(floor, direction, num_of_people);
                assignedRequests.add(new AssignedRequest(floor, direction, num_of_people, elevator));
                removal_list.add(new Request(floor, direction, num_of_people));
                return;
            }
        }

        // check elevators if they go by floor of the pickup, go in the same way and don't have too many requests already
        Elevator idle_elevator = null;
        int distance = max_distance;
        if (direction == EDirection.UP){
            for (Elevator elevator : elevators) {
                if (elevator.getLocation() < floor && elevator.getDirection() == EDirection.UP && !elevator.requestLimitReached()){
                    boolean wrongDirection = false;
                    // they can go in the same direction but have assigned request with another direction already
                    for (Request request: elevator.outer_requests){
                        if (request.direction == EDirection.DOWN) {
                            wrongDirection = true;
                            break;
                        }
                    }
                    if (wrongDirection){
                        continue;
                    }
                    elevator.addOuterRequest(floor, direction, num_of_people);
                    assignedRequests.add(new AssignedRequest(floor, direction, num_of_people, elevator));
                    removal_list.add(new Request(floor, direction, num_of_people));
                    return;
                }
                if (elevator.getDirection() == EDirection.IDLE && Math.abs(elevator.getLocation() - floor) < distance && elevator.outer_requests.size() == 0){
                    distance = Math.abs(elevator.getLocation() - floor);
                    idle_elevator = elevator;
                }
            }

            // as the last hope assign IDLE elevator that is closest to the request
            if (idle_elevator != null){
                idle_elevator.addOuterRequest(floor, direction, num_of_people);
                idle_elevator.setDirection(EDirection.getDirection(floor - idle_elevator.getLocation()));
                assignedRequests.add(new AssignedRequest(floor, direction, num_of_people, idle_elevator));
                removal_list.add(new Request(floor, direction, num_of_people));
                return;
            }
        }
        else if (direction == EDirection.DOWN) {
            for (Elevator elevator : elevators) {
                if (elevator.getLocation() > floor && elevator.getDirection() == EDirection.DOWN && !elevator.requestLimitReached()){
                    boolean wrongDirection = false;
                    for (Request request: elevator.outer_requests){
                        if (request.direction == EDirection.UP) {
                            wrongDirection = true;
                            break;
                        }
                    }
                    if (wrongDirection){
                        continue;
                    }
                    elevator.addOuterRequest(floor, direction, num_of_people);
                    assignedRequests.add(new AssignedRequest(floor, direction, num_of_people, elevator));
                    removal_list.add(new Request(floor, direction, num_of_people));
                    return;
                }
                if (elevator.getDirection() == EDirection.IDLE && Math.abs(elevator.getLocation() - floor) < distance && elevator.outer_requests.size() == 0){
                    distance = Math.abs(elevator.getLocation() - floor);
                    idle_elevator = elevator;
                }
            }
            if (idle_elevator != null){
                idle_elevator.addOuterRequest(floor, direction, num_of_people);
                idle_elevator.setDirection(EDirection.getDirection(floor - idle_elevator.getLocation()));
                assignedRequests.add(new AssignedRequest(floor, direction, num_of_people, idle_elevator));
                removal_list.add(new Request(floor, direction, num_of_people));
                return;
            }
        }
    }

    // perform one step of the simulation
    public void step(){
        // check if any request from waiting list can be assigned to elevator
        int tmp = waiting_list.size();
        for (Request value : waiting_list) {
            if (!assignedRequests.contains(value)){
                pickup(value.floor, value.direction, value.num_of_people);
            }
        }

        // remove requests from waiting list that has been assigned to elevator
        // remove() throws ConcurrentModificationException when it is modified in earlier foreach loop
        waiting_list.removeAll(removal_list);
        removal_list.clear();

        // check what is happening with each elevator, move and/or perform_actions accordingly
        for (Elevator elevator: elevators){
            Message message = null;
            // elevator is IDLE and isn't in starting point
            if (elevator.getDirection() == EDirection.IDLE && elevator.getLocation() != elevator.getBase()){
                // elevator has some requests here and cannot move
                if (elevator.outer_requests.size() > 0 || elevator.inner_requests.size() > 0){
                    message = elevator.perform_actions();
                }
                // no requests so wait and go back to starting point
                else {
                    elevator.decrementCooldown();
                    continue;
                }
            }
            // if there was performed action step earlier (someone went in or left the elevator), wait some time
            if (message == null && elevator.getWait() > 0){
                elevator.decrementWait();
                message = new Message();
            }
            // if no action performed earlier then move and perform actions
            else if (message == null){
                elevator.move();
                message = elevator.perform_actions();
            }
            // if someone went in or left the elevator check if it was one of assigned requests or not
            if (message.performedAction) {
                for (AssignedRequest request: assignedRequests){
                    if (elevator == request.elevator && elevator.getLocation() == request.floor && message.direction == request.direction){
                        assignedRequests.remove(request);
                        break;
                    }
                }
                // if there was too many people waiting, simulate part where some people go in and
                // everyone else is clicking summon elevator button again
                if (message.overflow > 0){
                    int check = waiting_list.indexOf(new Request(elevator.getLocation(), message.direction, message.overflow));
                    if (check == -1){
                        waiting_list.add(new Request(elevator.getLocation(), message.direction, message.overflow));
                    }
                }
            }
        }
    }

    public ArrayList<ElevatorInfo> status(){
        ArrayList<ElevatorInfo> elevatorsStatus = new ArrayList<>();
        for (int i = 0; i < elevators.length; i++) {
            elevatorsStatus.add(new ElevatorInfo(elevators[i]));
        }
        return elevatorsStatus;
    }

    public ArrayList<AssignedRequest> getAssignedRequests() { return assignedRequests; }
    public Elevator[] getElevators() { return elevators; }
    public ArrayList<Request> getWaiting_list() { return waiting_list; }
}
