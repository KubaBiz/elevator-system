package org.zad;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElevatorSystemTest {
    private final int num_elevators = 5;
    private final int elevator_limit = 5;
    private final int[] floors = new int[]{-2, 5};
    private final ElevatorSystem system = new ElevatorSystem(num_elevators, elevator_limit, floors);

    @Test
    void init() {
        assertEquals(5 , system.getElevators().length);
        assertEquals(5, system.getElevators()[0].limit);
        assertEquals(8, system.max_distance);
    }

    @Test
    void pickup() {
        system.pickup(1, EDirection.getDirection(2), 10);
        system.pickup(1, EDirection.getDirection(2), 10);
        system.pickup(2, EDirection.getDirection(2), 10);
        system.pickup(3, EDirection.getDirection(2), 10);
        system.pickup(4, EDirection.getDirection(2), 10);
        system.pickup(1, EDirection.getDirection(-2), 10);
        assertEquals(5 , system.getAssignedRequests().size());
        assertEquals(0, system.getWaiting_list().size());
    }

    @Test
    void status() {
        assertEquals(5 ,system.status().size());
    }
}