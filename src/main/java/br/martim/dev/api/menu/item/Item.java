package br.martim.dev.api.menu.item;

import br.martim.dev.api.menu.item.objects.ItemClick;
import br.martim.dev.api.menu.item.objects.ItemInteract;
import br.martim.dev.util.Util;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.util.*;

@Getter
public class Item extends ItemStack {

    @Getter
    protected static final Set<Item> itemList = new HashSet<>();

    protected ItemMeta meta = getItemMeta();

    private ItemClick click;
    private ItemInteract interact;

    public Item(Material type) {
        super(type);
    }

    public Item(Material type, int amount) {
        super(type, amount);
    }

    public static Item of(Material type) {
        return new Item(type);
    }

    public static Item of(Material type, int amount) {
        return new Item(type, amount);
    }

    public static Item of(Material type, String name) {
        return new Item(type).name(name);
    }

    public static Item of(Material type, String name, String... lore) {
        return new Item(type).name(name).lore(lore);
    }

    public static Item of(Material type, String name, List<String> lore) {
        return new Item(type).name(name).lore(lore);
    }

    @Override
    public Item clone() {
        Item clone = new Item(getType(), getAmount());

        if (hasItemMeta()) {
            ItemMeta clonedMeta = getItemMeta().clone();
            clone.updateMeta(clonedMeta);
        }

        if (meta instanceof Damageable dmg) {
            ItemMeta im = clone.getItemMeta();
            if (im instanceof Damageable cloneDmg) {
                cloneDmg.setDamage(dmg.getDamage());
                clone.updateMeta(cloneDmg);
            }
        }

        return clone;
    }

    public String getName() {
        return meta.hasDisplayName() ? meta.getDisplayName() : getType().name();
    }

    public void updateMeta(ItemMeta meta) {
        this.meta = meta;
        setItemMeta(meta);
    }

    public static Item convertItem(ItemStack stack) {
        if (stack == null) return null;
        return itemList.stream().filter(item -> item.isSimilar(stack)).findFirst().orElse(null);
    }

    public static Item fromStack(ItemStack stack) {
        if (stack == null) return null;

        Item item = new Item(stack.getType(), stack.getAmount());

        if (stack.hasItemMeta()) {
            item.updateMeta(stack.getItemMeta().clone());
        }

        // Copia dano, se aplicável
        ItemMeta im = stack.getItemMeta();
        if (im instanceof Damageable dmg) {
            Damageable newDmg = (Damageable) item.getItemMeta();
            newDmg.setDamage(dmg.getDamage());
            item.updateMeta(newDmg);
        }

        return item;
    }

    public static boolean exists(ItemStack stack) {
        if (stack == null || stack.getType() == Material.AIR) return false;
        return itemList.stream().anyMatch(item -> item.isSimilar(stack));
    }

    public String typeName() {
        return getType().name();
    }

    public Item click(ItemClick click) {
        this.click = click;
        itemList.add(this);
        return this;
    }

    public Item interact(ItemInteract interact) {
        this.interact = interact;
        itemList.add(this);
        return this;
    }

    public Item type(Material type) {
        setType(type);
        return this;
    }

    public Item name(String name) {
        meta.setDisplayName(Util.color(name));
        updateMeta(meta);
        return this;
    }

    public Item unbreakable() {
        meta.setUnbreakable(true);
        updateMeta(meta);
        return this;
    }

    public Item leatherColor(Color color) {
        if (meta instanceof LeatherArmorMeta armorMeta) {
            armorMeta.setColor(color);
            updateMeta(armorMeta);
        }
        return this;
    }

    public Item amount(int amount) {
        setAmount(amount);
        return this;
    }

    public Item damage(int damage) {
        if (meta instanceof Damageable dmg) {
            dmg.setDamage(damage);
            updateMeta(dmg);
        }
        return this;
    }

    public Item lore(List<String> lore) {
        List<String> translated = lore.stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .toList();
        meta.setLore(translated);
        updateMeta(meta);
        return this;
    }

    public Item lore(String... lore) {
        return lore(Arrays.asList(lore));
    }

    public Item flags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        updateMeta(meta);
        return this;
    }

    public Item enchantment(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        updateMeta(meta);
        return this;
    }

    public Item unsafeEnchantment(Enchantment enchantment, int level) {
        addUnsafeEnchantment(enchantment, level);
        updateMeta(meta);
        return this;
    }

    public Item enchantmentBook(Enchantment enchantment, int level) {
        if (meta instanceof EnchantmentStorageMeta book) {
            book.addStoredEnchant(enchantment, level, true);
            updateMeta(book);
        }
        return this;
    }

    public boolean hasEnchantments() {
        return meta.hasEnchants();
    }

    public Item skullByName(String name) {
        if (meta instanceof SkullMeta skullMeta) {
            OfflinePlayer offline = Bukkit.getOfflinePlayer(name);
            skullMeta.setOwningPlayer(offline);
            updateMeta(skullMeta);
        }
        return this;
    }
}
