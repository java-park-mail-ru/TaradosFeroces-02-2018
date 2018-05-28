package application.mechanics.game;

import application.mechanics.GameConfig;
import application.mechanics.base.Movement;
import application.mechanics.base.Position;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;


public class Scene {

    @JsonProperty("width")
    private final int width;

    @JsonProperty("height")
    private final int height;

    @JsonProperty("arena_width")
    private final int arenaWidth;

    @JsonProperty("arena_height")
    private final int arenaHeight;

    @JsonProperty("arena_left_edge")
    private final int arenaLeftEdge;

    @JsonProperty("arena_top_edge")
    private final int arenaTopEdge;

    @JsonProperty("step")
    private final double step;


    public Scene() {
        this.width = GameConfig.SCENE_WIDTH;
        this.height = GameConfig.SCENE_HEIGHT;

        this.step = GameConfig.STEP;

        this.arenaWidth = GameConfig.ARENA_WIDTH;
        this.arenaHeight = GameConfig.ARENA_HEIGHT;

        this.arenaLeftEdge = GameConfig.ARENA_LEFT_EDGE;
        this.arenaTopEdge = GameConfig.ARENA_TOP_EDGE;
    }

    @NotNull
    public Position calcNextPosition(@NotNull Position currentPosition,
                                     @NotNull Movement movement) {

        double posX = currentPosition.getPosX();
        double posY = currentPosition.getPosY();

        if (movement.getPosX() > 0) {
            posX += step;
        } else if (movement.getPosX() < 0) {
            posX -= step;
        }

        if (movement.getPosY() > 0) {
            posY += step;
        } else if (movement.getPosY() < 0) {
            posY -= step;
        }

        if (posX < 0) {
            posX = 0;
        } else if (posX > width) {
            posX = width;
        }

        if (posY < 0) {
            posY = 0;
        } else if (posY > height) {
            posY = height;
        }

        return new Position(posX, posY);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}