package application.mechanics.base;


public class Movement {

    private double posX;
    private double posY;

    public Movement(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    @Override
    public String toString() {
        return "{ posX=" + posX + ", posY=" + posY + "}";
    }
}
