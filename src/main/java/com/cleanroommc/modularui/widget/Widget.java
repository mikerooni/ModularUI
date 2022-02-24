package com.cleanroommc.modularui.widget;

import com.cleanroommc.modularui.api.IWidgetParent;
import com.cleanroommc.modularui.api.math.*;
import com.cleanroommc.modularui.internal.ModularUI;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * This class depicts a functional element of a ModularUI
 */
public abstract class Widget extends Gui {

    private ModularUI gui = null;
    private IWidgetParent parent = null;
    private Size size = Size.zero();
    private Pos2d relativePos = Pos2d.zero();
    private Pos2d pos = null;
    private Pos2d fixedPos = null;
    private boolean fillParent;
    private boolean initialised = false;
    protected boolean enabled = true;
    private int layer = -1;
    private boolean needRebuild = false;

    public Widget() {
    }

    public Widget(Size size) {
        this.size = size;
    }

    public Widget(Size size, Pos2d pos) {
        this.size = size;
        this.relativePos = pos;
    }

    /**
     * Only used internally
     */
    public final void initialize(ModularUI modularUI, IWidgetParent parent, int layer) {
        if (modularUI == null || parent == null || initialised) {
            throw new IllegalStateException("Illegal initialise call to widget!! " + toString());
        }
        this.gui = modularUI;
        this.parent = parent;
        this.layer = layer;

        if (fillParent) {
            size = parent.getSize();
        }

        onInit();
        this.initialised = true;

        this.needRebuild = true;
        if (ModularUI.isClient()) {
            rebuildInternal(false);
        }

        if (this instanceof IWidgetParent) {
            int nextLayer = layer + 1;
            for (Widget widget : ((IWidgetParent) this).getChildren()) {
                widget.initialize(this.gui, (IWidgetParent) this, nextLayer);
            }
        }
        onRebuildPost();
        this.needRebuild = false;
    }

    public final void screenUpdateInternal() {
        if (needRebuild) {
            rebuildInternal(true);
            needRebuild = false;
        }
        onScreenUpdate();
    }

    protected final void rebuildInternal(boolean runForChildren) {
        if (!initialised) {
            return;
        }
        if (isFixed()) {
            setPos(this.fixedPos.subtract(parent.getAbsolutePos()));
            setAbsolutePos(this.fixedPos);
        } else {
            setAbsolutePos(parent.getAbsolutePos().add(getPos()));
        }
        onRebuildPre();
        if (runForChildren && this instanceof IWidgetParent) {
            for (Widget child : ((IWidgetParent) this).getChildren()) {
                child.rebuildInternal(true);
            }
            onRebuildPost();
        }
    }

    /**
     * Called once per tick
     */
    @SideOnly(Side.CLIENT)
    public void onScreenUpdate() {
    }

    /**
     * Called each frame, approximately 60 times per second
     */
    @SideOnly(Side.CLIENT)
    public void onFrameUpdate() {
    }

    public void onRebuildPre() {
    }

    public void onRebuildPost() {
    }

    protected void onInit() {
    }

    public boolean isUnderMouse() {
        return isUnderMouse(gui.getMousePos(), getAbsolutePos(), getSize());
    }

    public ModularUI getGui() {
        return gui;
    }

    public IWidgetParent getParent() {
        return parent;
    }

    public GuiArea getArea() {
        return GuiArea.of(size, pos);
    }

    public Pos2d getPos() {
        return relativePos;
    }

    public Pos2d getAbsolutePos() {
        return pos;
    }

    public Size getSize() {
        return size;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getLayer() {
        return layer;
    }

    public final boolean isInitialised() {
        return initialised;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isFixed() {
        return fixedPos != null;
    }

    public Widget setSize(Size size) {
        checkNeedsRebuild();
        this.size = size;
        return this;
    }

    public Widget setPos(Pos2d relativePos) {
        checkNeedsRebuild();
        this.relativePos = relativePos;
        return this;
    }

    protected void setAbsolutePos(Pos2d relativePos) {
        this.pos = relativePos;
    }

    public Widget setFixedPos(@Nullable Pos2d pos) {
        checkNeedsRebuild();
        this.fixedPos = pos;
        return this;
    }

    public Widget fillParent() {
        this.fillParent = true;
        return this;
    }

    public void checkNeedsRebuild() {
        if (initialised && !needRebuild) {
            this.needRebuild = true;
        }
    }

    public static boolean isUnderMouse(Pos2d mouse, Pos2d areaTopLeft, Size areaSize) {
        return mouse.x >= areaTopLeft.x &&
                mouse.x <= areaTopLeft.x + areaSize.width &&
                mouse.y >= areaTopLeft.y &&
                mouse.y <= areaTopLeft.y + areaSize.height;
    }
}
