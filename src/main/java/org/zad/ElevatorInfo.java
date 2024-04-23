package org.zad;

public class ElevatorInfo {
    private final int location;
    private final int num_of_unique_requests;
    private final int num_of_people_inside;
    private final EDirection direction;
    private final int index;
    public ElevatorInfo(Elevator elevator){
        location = elevator.getLocation();
        num_of_unique_requests = elevator.getNum_of_unique_requests();
        num_of_people_inside = elevator.getNum_of_people_inside();
        direction = elevator.getDirection();
        this.index = elevator.index;
    }

    @Override
    public String toString() {
        return "{" + index +
                ", FL=" + location +
                ", UQ_R=" + num_of_unique_requests +
                ", D=" + direction +
                ", N_OF_P=" + num_of_people_inside +
                '}';
    }
}
