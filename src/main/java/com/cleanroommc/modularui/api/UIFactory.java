package com.cleanroommc.modularui.api;

import com.cleanroommc.modularui.factory.GuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.ModularScreen;
import com.cleanroommc.modularui.value.sync.GuiSyncManager;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * An interface for UI factories. They are responsible for opening synced GUIs and syncing necessary data.
 *
 * @param <D> gui data type
 */
@ApiStatus.AvailableSince("2.4.0")
public interface UIFactory<D extends GuiData> {

    /**
     * The name of this factory. Must be constant.
     *
     * @return the factory name
     */
    @NotNull
    String getFactoryName();

    /**
     * Creates the main panel for the GUI. Is called on client and server side.
     *
     * @param guiData     gui data
     * @param syncManager sync manager
     * @return new main panel
     */
    @ApiStatus.OverrideOnly
    ModularPanel createPanel(D guiData, GuiSyncManager syncManager);

    /**
     * Creates the screen for the GUI. Is only called on client side.
     *
     * @param guiData   gui data
     * @param mainPanel main panel created in {@link #createPanel(GuiData, GuiSyncManager)}
     * @return new main panel
     */
    @SideOnly(Side.CLIENT)
    @ApiStatus.OverrideOnly
    ModularScreen createScreen(D guiData, ModularPanel mainPanel);

    /**
     * Writes the gui data to a buffer.
     *
     * @param guiData gui data
     * @param buffer  buffer
     */
    @ApiStatus.OverrideOnly
    void writeGuiData(D guiData, PacketBuffer buffer);

    /**
     * Reads and creates the gui data from the buffer.
     *
     * @param player player
     * @param buffer buffer
     * @return new gui data
     */
    @NotNull
    @ApiStatus.OverrideOnly
    D readGuiData(EntityPlayer player, PacketBuffer buffer);
}
