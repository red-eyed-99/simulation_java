package simulation;

public class Main {
    public static void main(String[] args) {
        AreaGenerator generator = new AreaGenerator(new Area(10));
        generator.generateArea();
    }
}
