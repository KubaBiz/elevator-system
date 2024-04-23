package org.zad;

import java.util.Objects;

public class Request {
    public int floor;
    public int num_of_people;
    public EDirection direction;
    public Request(int floor, EDirection direction, int num_of_people){
        this.floor = floor;
        this.num_of_people = num_of_people;
        this.direction = direction;
    }

    @Override
    public String toString() {
        return "Req{" +
                "F=" + floor +
                ", N=" + num_of_people +
                ", D=" + direction +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request myClass = (Request) o;
        return floor == myClass.floor &&
                Objects.equals(direction, myClass.direction);
    }
}