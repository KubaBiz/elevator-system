This project is my one-week implementation of an elevator system with simple console interface.
Main program allows to run simulation of the elevator system with 4 custom parameters:
* (1) number of elevators 
* (2) limit of people in the elevator
* (3) the lowest floor
* (4) the highest floor

## How to build
This project is built with Gradle. To build it , run the following command:
```shell
./gradlew build
```
Remember to check if you are in the project root directory!

## How to run
To run the program, run the following command:
```shell
java -cp build/classes/java/main org.zad.Main arg1 arg2 arg3 arg4
```
Remember to change arguments to desirable integers.

You can also open the project in IDE (e.g. IntelliJ IDEA) and run it with modified run configuration.

## How to use
If you managed to start the program with 4 arguments - congratulations! Now you can choose one of four commands:
```
- pickup <floor> <direction> <num_of_people> :: Request a pickup on the floor in the given direction for given number of people
- step <optional_integer> :: Move one or more steps in the simulation
- status :: Gives some information about every elevator in the building
- exit :: Exits the program
```
I recommend to start with ```pickup``` followed by ```step```

## Modified C-LOOK Algorithm
Everyone knows what a C-LOOK algorithm is, right? No? Then why don't you try [googling](https://www.geeksforgeeks.org/c-look-disk-scheduling-algorithm/) it yourself ;)

After some research I didn't find C-LOOK version for multiple disk heads 
(I don't think someone needs it actually) so I tried to modify it to work with multiple elevators instead.

Main system controller ```class ElevatorSystem``` distributes requests to multiple elevators ```class Elevator```. 
Request ```class Request``` can be assigned to one Elevator, but Elevator can have multiple Requests. Elevator knows only fraction of the system that is sent to it by main controller, 
it moves in one direction unless Request from controller tells it to change directions or there are no more Requests to handle (it looks similar to the C-LOOK algorithm if you have one elevator, right?).

So how does main controller assign Requests to Elevators? Below algorithm in points:
1. Try to merge Request with already assigned Request ```class AssignedRequest``` if they are from the same floor and point to the same direction.
2. Check idle Elevators with no assigned Requests on the same floor, assign one if found.
3. Check if there is Elevator that goes by this floor and is not flooded with Requests already (<80% of the limit set in second program parameter)

Third point is taken from C-LOOK algorithm modified to fit elevators (they don't have unlimited space).

What happens to Request that didn't make it to one of the Elevators? 
Don't worry, it goes to waiting list that is checked by main controller
every step of simulation to see if any Elevator wants them (they cannot say no).

There is one thing that was not thoroughly explained. 
How the limit of people in the elevator is calculated?
Sadly we can only guess how many people there is without additional equipment like cameras in elevators.
Elevator is calculating number of unique Requests every time it gets one. 
If there are more than 80% of the limit taken - it cannot accept new Requests from the controller.
Of course there is a flaw in design, what if there are hundreds of people waiting on one floor,
it is certain that Elevator won't know this and maybe will arrive with space already taken by people from earlier Requests.

If I didn't explain it good enough, feel free to check comments I wrote in ```src/main/java/org/zad```, 
especially in two files: ```Elevator.java``` and ```ElevatorSystem.java```

## Additional simulation features
1. When Elevator arrives at the floor to pick up or drop off passengers, 
it needs to wait set amount of time ```Elevator.wait```, as entering and exiting elevator does not happen immediately.
2. When Elevator doesn't get new Requests for few steps of simulation ```Elevator.cooldown``` 
it returns to the base assigned at the start of simulation.
3. You can run ```./gradlew test``` and check out tests I made in ```src/test/java/org/zad```. They are not that impressive by the way.
