package application.mechanics.game.spells;

import application.mechanics.GameConfig;
import application.mechanics.GameSession;
import org.jetbrains.annotations.NotNull;

public class Spell {

    public int getCooldown() {
        return GameConfig.DEFAULT_SPELL_COOLDOWN;
    }

    public void perform(@NotNull GameSession gameSession) {
        System.out.println("performing spell (Spell.class="
                + this.getClass().getName()
                + ") on game (session="
                + gameSession.toString()
        );
    }
}
