package br.martim.dev.api.user;

import br.martim.dev.Eternal;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
public class User {

    private final UUID id;

    private int lives;

    private long returnsAt, createdAt;

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
