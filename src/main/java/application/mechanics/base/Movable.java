package application.mechanics.base;


import com.fasterxml.jackson.annotation.JsonProperty;


public class Movable {
    @JsonProperty("x")
    private final double posX;
    @JsonProperty("y")
    private final double posY;

    @JsonProperty("vx")
    private final double speedX;
    @JsonProperty("vy")
    private final double speedY;

    @JsonProperty("ax")
    private final double accelerationX;
    @JsonProperty("ay")
    private final double accelerationY;

    public Movable(double posX, double posY,
                   double speedX, double speedY,
                   double accelerationX, double accelerationY) {
        this.posX = posX;
        this.posY = posY;
        this.speedX = speedX;
        this.speedY = speedY;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
    }

    @Override
    public String toString() {
        return String.format("{ (x, y): (%s, %s), (v_x, v_y): (%s, %s), (a_x, a_y): (%s, %s) }",
                posX, posY, speedX, speedY, accelerationX, accelerationY);
    }
}
