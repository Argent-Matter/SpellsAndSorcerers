package dev.screret.mui.theme;

import net.neoforged.bus.api.Event;

public class ReloadThemeEvent extends Event {

    public static class Pre extends ReloadThemeEvent {}

    public static class Post extends ReloadThemeEvent {}
}
