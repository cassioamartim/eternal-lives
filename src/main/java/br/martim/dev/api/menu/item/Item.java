package br.martim.dev.api.menu.item;

import br.martim.dev.api.menu.item.objects.ItemClick;
import br.martim.dev.api.menu.item.objects.ItemInteract;
import br.martim.dev.util.Util;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
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

    public Item(Material type, int amount, int id) {
        super(type, amount, (short) id);
    }

    public static Item of(Material type) {
        return new Item(type);
    }

    public static Item of(Material type, String name) {
        return new Item(type).name(name);
    }

    public static Item of(Material type, int id) {
        return new Item(type, 1, id);
    }

    public static Item of(Material type, int id, String name, String... lore) {
        return new Item(type, 1, id).name(name).lore(lore);
    }

    public static Item of(Material type, int id, String name, List<String> lore) {
        return new Item(type, 1, id).name(name).lore(lore);
    }

    public static Item of(Material type, int id, int amount) {
        return new Item(type, amount, id);
    }

    public static Item of(Material type, int id, String name) {
        return new Item(type, 1, id).name(name);
    }

    public static Item of(Material type, String name, String... lore) {
        return new Item(type).name(name).lore(lore);
    }

    public static Item of(Material type, String name, List<String> lore) {
        return new Item(type).name(name).lore(lore);
    }

    public Item clone() {
        Item clone = new Item(getType(), getAmount(), getDurability());

        if (this.hasItemMeta()) {
            ItemMeta meta = this.getItemMeta().clone();

            clone.updateMeta(meta);
        }

        return clone;
    }

    /* Object Methods */
    public String getName() {
        return getType().name();
    }

    public void updateMeta(ItemMeta meta) {
        this.meta = meta;
        setItemMeta(meta);
    }

    public static Item convertItem(ItemStack stack) {
        return itemList.stream().filter(item -> item.isSimilar(stack)).findFirst().orElse(null);
    }

    public static Item fromStack(ItemStack stack) {
        Item item = new Item(stack.getType());

        item.setAmount(stack.getAmount());
        item.setDurability(stack.getDurability());
        item.setData(stack.getData());

        ItemMeta meta = stack.getItemMeta();

        if (meta != null) {
            if (meta instanceof BannerMeta) {
                BannerMeta bm = (BannerMeta) meta;

                item.setItemMeta(bm);
            } else if (meta instanceof SkullMeta) {
                SkullMeta sm = (SkullMeta) meta;

                item.setItemMeta(sm);
            } else {
                item.setItemMeta(meta);

                if (meta.hasEnchants()) {
                    for (Enchantment enchantment : meta.getEnchants().keySet()) {
                        try {
                            item.enchantment(enchantment, meta.getEnchantLevel(enchantment));
                        } catch (IllegalArgumentException ignored) {
                            item.addUnsafeEnchantment(enchantment, meta.getEnchantLevel(enchantment));
                        }
                    }
                }
            }
        }

        return item;
    }

    public static boolean exists(ItemStack stack) {
        if (stack == null || stack.getType().equals(Material.AIR)) return false;

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

    /* Item Stack Methods */
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
        LeatherArmorMeta armorMeta = (LeatherArmorMeta) meta;

        armorMeta.setLore(null);
        armorMeta.setColor(color);

        updateMeta(armorMeta);
        return this;
    }

    public Item amount(int amount) {
        setAmount(amount);
        return this;
    }

    public Item durability(int durability) {
        setDurability((short) durability);
        return this;
    }

    public Item lore(List<String> lore) {
        List<String> translatedLore = new ArrayList<>();

        lore.forEach(line -> translatedLore.add(ChatColor.translateAlternateColorCodes('&', line)));

        meta.setLore(translatedLore);

        updateMeta(meta);
        return this;
    }

    public Item lore(String... lore) {
        meta.setLore(Arrays.asList(lore));

        updateMeta(meta);
        return this;
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
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) this.meta;

        meta.addStoredEnchant(enchantment, level, true);
        updateMeta(meta);

        return this;
    }

    public boolean hasEnchantments() {
        return meta.hasEnchants();
    }

    public Item skullByName(String name) {
        SkullMeta skullMeta = (SkullMeta) meta;

        skullMeta.setOwner(name);
        updateMeta(skullMeta);

        return this;
    }
}