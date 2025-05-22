package br.martim.dev.api.menu.sound;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Sound;

@Getter
@AllArgsConstructor
public enum MenuSound {

    ACTION_CONFIRMED(Sound.BLOCK_NOTE_BLOCK_PLING),
    ERROR(Sound.BLOCK_ANVIL_LAND),
    NO(Sound.ENTITY_VILLAGER_NO),
    SUCCESS(Sound.BLOCK_NOTE_BLOCK_PLING),
    PAGE_SWITCH(Sound.BLOCK_WOODEN_BUTTON_CLICK_ON),
    MENU_OPEN(Sound.BLOCK_CHEST_OPEN),
    MENU_CLOSE(Sound.BLOCK_CHEST_CLOSE),
    MENU_CHANGE(Sound.UI_BUTTON_CLICK),
    ITEM_ADDED(Sound.ENTITY_ITEM_PICKUP),
    LEVEL_UP(Sound.ENTITY_PLAYER_LEVELUP);

    private final Sound sound;
}
