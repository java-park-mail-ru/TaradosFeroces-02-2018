package application.mechanics.base;


import com.fasterxml.jackson.annotation.JsonProperty;


public class Position {

    @JsonProperty("x")
    private double posX;
    @JsonProperty("y")
    private double posY;

    public Position(double posX, double posY) {
        this.posX = posX;
        this.posY = posY;
    }


    // 25/05/18 Fix crutch which has been created for frontenders
    public Position(double radius, int index, int maxCount, int width, int height) {
        double angle = Math.PI / 2 + index * Math.atan((2 * Math.PI) / maxCount);

        this.posX = radius * Math.cos(angle) + width / 2;
        this.posY = radius * Math.sin(angle) + height / 2;
    }

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    @Override
    public String toString() {
        return String.format("{ (x, y): (%s, %s) }", posX, posY);
    }
}
