package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.ui.HudModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeComponent extends HudComponentItem {
    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);
    private final int i = 0;
    public TimeComponent() {
        super("Time", 2, 13);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.render(mouseX, mouseY, partialTicks, context);

        final String time = new SimpleDateFormat("h:mm a").format(new Date());

        context.drawTextWithShadow(mc.textRenderer, Text.of(time), (int) GetX(), (int) GetY(), 0x2ACCED);

        SetWidth(Wrapper.GetMC().textRenderer.getWidth(time));
        SetHeight(Wrapper.GetMC().textRenderer.fontHeight);
    }
}