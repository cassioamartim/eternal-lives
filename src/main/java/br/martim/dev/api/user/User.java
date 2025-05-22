package br.martim.dev.api.user;

import br.martim.dev.Eternal;
import br.martim.dev.api.life.LifeAPI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class User {

    private final UUID id;

    private int lives;

    private long returnsAt, createdAt;

    public void setLives(int lives) {
        this.lives = lives;

        Player player = Bukkit.getPlayer(id);

        if (player != null)
            LifeAPI.update(player, lives);
    }

    public void save() {
        Eternal.getUserController().save(this);
    }

    public boolean isDead() {
        return lives <= 0;
    }

    public boolean isBanned() {
        return returnsAt > System.currentTimeMillis();
    }

    public boolean hasAlreadyBanned() {
        return returnsAt > -1L;
    }
}
