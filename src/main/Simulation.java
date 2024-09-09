package main;

public class Simulation {
    private int movesCount = 0;
    private Area area;
    private Actions actions;

    public Simulation() {
        AreaGenerator generator = new AreaGenerator(new Area(10));
        area = generator.generateArea();
    }

    public int getMovesCount() {
        return movesCount;
    }

    public Area getArea() {
        return area;
    }

    public void nextTurn() {

    }

    public void startSimulation() {

    }

    public void pauseSimulation() {

    }
}
