package org.zad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevatorTest {
    private final int elevator_limit = 5;
    private final int starting_point = 0;
    private final int min_floor = -2;
    private final int max_floor = 5;
    private final int index = 0;
    private final Elevator elevator = new Elevator(elevator_limit, starting_point, min_floor, max_floor, index);

    @Test
    void addOuterRequest() {
        elevator.addOuterRequest(2, EDirection.getDirection(2), 5);
        elevator.addOuterRequest(3, EDirection.getDirection(2), 5);
        elevator.addOuterRequest(2, EDirection.getDirection(2), 5);
        assertEquals(2 , elevator.outer_requests.size());
        assertEquals(10, elevator.outer_requests.get(0).num_of_people);
        assertEquals(2, elevator.getNum_of_unique_requests());
    }

    @Test
    void decrementCooldown() {
        elevator.perform_actions();
        elevator.decrementCooldown();
        assertEquals(2, elevator.getCooldown());
    }

    @Test
    void changeDirection() {
        assertEquals(EDirection.IDLE, elevator.getDirection());
        elevator.setDirection(EDirection.UP);
        assertEquals(EDirection.UP, elevator.getDirection());
    }

    @Test
    void getLocation() {
        elevator.setDirection(EDirection.UP);
        elevator.move();
        elevator.move();
        assertEquals(2, elevator.getLocation());
    }

    @Test
    void getBase() {
        assertEquals(0,elevator.getBase());
        Elevator elevator1 = new Elevator(elevator_limit, 2, min_floor, max_floor, index);
        Elevator elevator2 = new Elevator(elevator_limit, -1, min_floor, max_floor, index);
        assertEquals(2, elevator1.getLocation());
        assertEquals(-1, elevator2.getLocation());
    }

    @Test
    void requestLimitReached() {
        elevator.addOuterRequest(1, EDirection.getDirection(2), 5);
        elevator.addOuterRequest(2, EDirection.getDirection(2), 5);
        assertFalse(elevator.requestLimitReached());
        elevator.addOuterRequest(3, EDirection.getDirection(2), 5);
        elevator.addOuterRequest(4, EDirection.getDirection(2), 5);
        elevator.addOuterRequest(5, EDirection.getDirection(2), 5);
        assertTrue(elevator.requestLimitReached());
    }

    @Test
    void move() {
        elevator.setDirection(EDirection.DOWN);
        elevator.move();
        elevator.move();
        assertEquals(-2, elevator.getLocation());
        elevator.move();
        assertEquals(-2, elevator.getLocation());
        assertEquals(EDirection.UP, elevator.getDirection());
        for (int i = 0; i<5; i++){
            elevator.move();
        }
        assertEquals(3, elevator.getLocation());
    }

    @Test
    void perform_actions() {
        elevator.perform_actions();
        assertEquals(0, elevator.getNum_of_unique_requests());
        elevator.addOuterRequest(0, EDirection.UP, 3);
        elevator.perform_actions();
        assertEquals(3, elevator.getNum_of_people_inside());
        assertEquals(0 ,elevator.outer_requests.size());
    }
}