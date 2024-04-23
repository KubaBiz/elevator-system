package org.zad;

public class AssignedRequest extends Request {
    public Elevator elevator;

    public AssignedRequest(int floor, EDirection direction, int num_of_people, Elevator elevator) {
        super(floor, direction, num_of_people);
        this.elevator = elevator;
    }

    @Override
    public String toString() {
        return "AssignedReq{" +
                "F=" + floor +
                ", N=" + num_of_people +
                ", D=" + direction +
                ", E=" + elevator.index +
                '}';
    }
}