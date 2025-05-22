package br.martim.dev.api.menu.page;

@FunctionalInterface
public interface ItemPageBuilder<T> {
    void accept(T item, int slot);
}