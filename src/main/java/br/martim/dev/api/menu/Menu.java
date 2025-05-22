package br.martim.dev.api.menu;

import br.martim.dev.api.menu.item.Item;
import br.martim.dev.api.menu.page.ItemPageBuilder;
import br.martim.dev.api.menu.sound.MenuSound;
import br.martim.dev.controller.MenuController;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Getter
@Setter
public abstract class Menu {

    private final Player player;

    private String title;
    private final String initialTitle;

    private org.bukkit.inventory.Inventory holder;

    private final Menu last;
    private final Map<Integer, Item> contents;

    private final List<ItemStack> protectedContents;
    private final List<Integer> protectedSlots;


    // Variáveis configuráveis
    private boolean returnable, paginated, allowClick, allowClickItemWithQuantity,
            allowDrag = true, allowShift = true, showTitlePage = true, allowClose = true, whenClosingShow = true;

    private int size, rows, maxItems, pageIndex, pageNumber = 1, totalPages = 1;

    public Menu(Player player, String title, Menu last, int rows, int maxItems) {
        this.player = player;

        this.title = title;
        this.initialTitle = title;

        this.last = last;
        this.contents = new HashMap<>();

        this.returnable = last != null;
        this.paginated = maxItems > 0;

        this.rows = rows;
        this.size = rows * 9;

        this.maxItems = maxItems;

        this.protectedContents = new ArrayList<>();
        this.protectedSlots = new ArrayList<>();

        this.holder = Bukkit.createInventory(player, size, title);
    }

    public Menu(Player player, String title, Menu last, int rows) {
        this(player, title, last, rows, 0);
    }

    public Menu(Player player, String title, int rows, int maxItems) {
        this(player, title, null, rows, maxItems);
    }

    public Menu(Player player, String title, int rows) {
        this(player, title, null, rows, 0);
    }

    public Menu(Player player, String title, boolean allowClick, int rows) {
        this(player, title, null, rows, 0);

        setAllowClick(allowClick);
    }

    /**
     * Inicializar o processo de criação do inventário.
     */
    public abstract void build();

    public void show() {
        if (player == null) return;

        contents.forEach((slot, item) -> holder.setItem(slot, item));

        player.openInventory(holder);

        MenuController.save(this);
    }

    public void close() {
        player.closeInventory();
    }

    public void clear() {
        if (holder != null)
            holder.clear();

        if (!contents.isEmpty())
            contents.clear();
    }

    public void clear(int... selectedSlots) {
        for (int slot : selectedSlots) {

            Item item = contents.get(slot);
            if (item == null) continue;

            item.type(Material.AIR);
            contents.remove(slot);
        }
    }

    public void addProtectedContent(int... slots) {
        for (int slot : slots) {
            protectedSlots.add(slot);
        }
    }

    public boolean isProtectedContent(ItemStack stack) {
        return stack != null && protectedContents.stream().anyMatch(item -> item.isSimilar(stack));
    }

    public boolean isProtectedSlot(int slot) {
        return protectedSlots.contains(slot);
    }

    public void add(int slot, Item item) {
        add(slot, item, false);
    }

    public void add(int slot, Item item, boolean protection) {
        contents.put(slot, item);

        if (protection)
            protectedContents.add(item);
    }

    public void set(int slot, Item item) {
        holder.setItem(slot, item);

        contents.put(slot, item);
    }

    /**
     * Adicionar um item no inventário em uma determinada linha em uma coluna específica.
     *
     * @param row    Linha do inventário [1, 2, 3, 4, 5, 6]
     * @param column Coluna (Slot) se baseia na linha.
     * @param item   Item
     */
    public void add(int row, int column, Item item) {

        // Lidando com possíveis problemas
        if (row > 6)
            throw new IllegalArgumentException("Erro ao adicionar item no inventário. A linha '" + row + "' é maior que o limite de 6.");

        if (column > 9)
            throw new IllegalArgumentException("Erro ao adicionar item no inventário. A coluna '" + column + "' é maior que o limite de 9.");

        // Ex: add(1, 1, item); -> Slot: 0, add(1, 2, item); -> Slot: 1
        int slot = (row - 1) * 9 + (column - 1);

        contents.put(slot, item);
    }

    public void remove(int slot) {
        contents.remove(slot);

        holder.setItem(slot, Item.of(Material.AIR));
    }

    public void move(int from, int to) {

        Item item = contents.get(from);

        if (item == null) return;

        contents.remove(from);
        contents.put(to, item);

        holder.remove(item);

        holder.setItem(to, item);

        player.updateInventory();
    }

    public <T> void page(List<T> list, int lastPageSlot, int nextPageSlot, int initialSlot, ItemPageBuilder<T> itemPageBuilder) {
        setTotalPages((list.size() + maxItems - 1) / maxItems);
        addBorderPage(lastPageSlot, nextPageSlot);

        if (totalPages > 1 && showTitlePage)
            setTitle(initialTitle + " (" + pageNumber + "/" + totalPages + ")");

        int last = initialSlot;

        for (int i = 0; i < maxItems; i++) {
            int index = maxItems * (pageNumber - 1) + i;
            if (index >= list.size()) break;

            T item = list.get(index);

            if (item != null)
                itemPageBuilder.accept(item, initialSlot);

            initialSlot++;
            if (initialSlot == (last + 7)) {
                initialSlot += 2;
                last = initialSlot;
            }
        }
    }

    public <T> void page(List<T> list, int initialSlot, ItemPageBuilder<T> itemPageBuilder) {
        page(list, getTotalSlots() - 6, getTotalSlots() - 4, initialSlot, itemPageBuilder);
    }

    public void addBorderPage() {
        addBorderPage(getTotalSlots() - 6, getTotalSlots() - 4);
    }

    public void addBorderPage(int lastSlot, int nextSlot) {
        if (pageNumber > 1) {
            add(lastSlot, Item.of(Material.ARROW, "§cPrevious page",
                            "§ePage " + (pageNumber - 1))
                    .click(event -> {
                        setPageNumber(pageNumber - 1);

                        if (showTitlePage)
                            setTitle(initialTitle + " (" + pageNumber + "/" + totalPages + ")");

                        sound(MenuSound.MENU_CHANGE);
                        build();
                    }));
        }

        if (pageNumber < totalPages) {
            add(nextSlot, Item.of(Material.ARROW, "§aNext page",
                            "§ePage " + (pageNumber + 1))
                    .click(event -> {
                        setPageNumber(pageNumber + 1);

                        if (showTitlePage)
                            setTitle(initialTitle + " (" + pageNumber + "/" + totalPages + ")");

                        sound(MenuSound.MENU_CHANGE);
                        build();
                    }));
        }
    }

    public void addCloseButton() {
        addCloseButton(getTotalSlots() - (hasBackButton() ? 6 : 5));
    }

    public void addCloseButton(int slot) {
        add(slot, Item.of(Material.ARROW, "§cClose window")
                .click(event -> {
                    close();
                    sound(MenuSound.ERROR);
                }), true);
    }

    public boolean hasBackButton() {
        boolean found = false;

        for (Map.Entry<Integer, Item> entry : contents.entrySet()) {
            int slot = entry.getKey();

            Item item = entry.getValue();

            if (slot == (getTotalSlots() - 5) && item.getType().equals(Material.ARROW)) {
                found = true;
                break;
            }
        }

        return found;
    }

    public void addBackButton(int slot) {
        add(slot, Item.of(Material.ARROW, "§aBack",
                        last != null ? "§7To " + last.getInitialTitle() : "")
                .click(event -> {
                    if (last == null) {
                        sound(MenuSound.ERROR);
                        return;
                    }

                    last.build();

                    sound(MenuSound.MENU_CHANGE);
                }), true);
    }

    public void addBackButton() {
        if (pageNumber <= 1)
            addBackButton(getTotalSlots() - (totalPages > 1 ? 6 : 5));
    }

    public void addErrorButton(String name) {
        addErrorButton(13, name);
    }

    public void addErrorButton(int slot, String name) {
        add(slot, Item.of(Material.BARRIER, name), true);
    }

    public void addBackButtons() {
        if (isReturnable())
            addBackButton();
        else
            addCloseButton();
    }

    public void addBackButtons(int slot) {
        if (isReturnable())
            addBackButton(slot);
        else
            addCloseButton(slot);
    }

    public void sound(MenuSound sound) {
        this.sound(sound.getSound());
    }

    public void sound(Sound sound) {
        player.playSound(player.getLocation(), sound, 2f, 2.5f);
    }

    public int getItemSlot(Material material) {
        int slot = 0;

        for (Map.Entry<Integer, Item> entry : getContents().entrySet()) {
            Item item = entry.getValue();

            if (item.getType().equals(material))
                slot = entry.getKey();
        }

        return slot;
    }

    public int getTotalSlots() {
        return rows * 9;
    }
}