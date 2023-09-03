package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ui.ColorsModule;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.module.world.CoordsSpooferModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;

public class CoordsHudComponent extends HudComponentItem {

    public final Value<Modes> Mode = new Value<Modes>("Mode", new String[]
            {"Mode"}, "Mode of displaying coordinates", Modes.Inline);
    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);

    private boolean SpoofX = false;

    final DecimalFormat Formatter = new DecimalFormat("#.#");

    private final CoordsSpooferModule _getCoords = (CoordsSpooferModule) ModuleManager.Get().GetMod(CoordsSpooferModule.class);



    private final int i = 0;

    boolean SpoofZ = false;
    public CoordsHudComponent() {
        super("Coords", 3, 517);
        SetHidden(true);
    }

    public String format(double input) {
        String result = Formatter.format(input);

        if (!result.contains("."))
            result += ".0";

        return result;
    }

    @Override
    public void render(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.render(p_MouseX, p_MouseY, p_PartialTicks, context);

        String coords = "XYZ: " + Formatting.WHITE + format(getX()) + " , " + format(getY()) + " , " + format(getZ());

        switch (Mode.getValue()) {
            case Inline:


                context.drawTextWithShadow(mc.textRenderer, Text.of(coords), (int) GetX(), (int) GetY(), 0x2ACCED);

                SetWidth(Wrapper.GetMC().textRenderer.getWidth(coords));
                SetHeight(Wrapper.GetMC().textRenderer.fontHeight);

                break;
        }

    }

    private Boolean getCoordSpoofer() {
        return _getCoords.isEnabled();
    }
    private Boolean GetRandom() {
        return _getCoords.Random.getValue();
    }



    private int randX() {
        int i = (int) (Math.random() * 2) + 1;
        if (i == 1) {
            i = (int) ((Math.random() * 30000000) + 0) * -1;
        } else {
            i = (int) ((Math.random() * 30000000) + 0);
        }
        return i;
    }

    private int randZ() {
        int i = (int) (Math.random() * 2) + 1;
        if (i == 1) {
            i = (int) ((Math.random() * 30000000) + 0) * -1;
        } else {
            i = (int) ((Math.random() * 30000000) + 0);
        }
        return i;
    }

    private double getX() {
        if (getCoordSpoofer()) {
            if (GetRandom()) {
                return mc.player.getX() + randX();
            }
            if (!GetRandom()) {
                return mc.player.getX() + _getCoords.CoordsX.getValue() + _getCoords.CoordsNegativeX.getValue();
            }
        }
        return  mc.player.getX();
    }

    private double getZ() {
        if (getCoordSpoofer()) {
            if (GetRandom()) {
                return mc.player.getZ() + randZ();
            }
            if (!GetRandom()) {
                return mc.player.getZ() + _getCoords.CoordsZ.getValue() + _getCoords.CoordsNegativeZ.getValue();
            }
        }
        return mc.player.getZ();
    }

    private double getY() {
        return  mc.player.getY();
    }

    public enum Modes {
        Inline
    }
}