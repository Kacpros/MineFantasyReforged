package minefantasy.mfr.container;

import minefantasy.mfr.tile.TileEntityCarpenterMFR;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ContainerCarpenterMFR extends ContainerBase {
    private TileEntityCarpenterMFR tile;
    private boolean isGuiContainer;

    public ContainerCarpenterMFR(TileEntityCarpenterMFR tile) {
        isGuiContainer = false;
        this.tile = tile;
        int width = tile.width;
        int height = tile.height;

        //main grid
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int slot = y * width + x;
                this.addSlotToContainer(new SlotItemHandler(tile.getInventory(), slot, 44 + x * 18, 54 + y * 18));
            }
        }

        //special slots
        this.addSlotToContainer(new SlotItemHandler(tile.getInventory(), tile.getInventory().getSlots() - 5, 174, 80));
        for (int y = 0; y < 4; y++) {
            int slot = tile.getInventory().getSlots() - 4 + y;
            this.addSlotToContainer(new SlotItemHandler(tile.getInventory(), slot, 3, 54 + y * 18));
        }
    }

    public ContainerCarpenterMFR(EntityPlayer player, TileEntityCarpenterMFR tile) {
        super(player.inventory, tile);
        isGuiContainer = true;
        this.tile = tile;
        int width = tile.width;
        int height = tile.height;

        //main grid
        addTileSlots(width, height, 44, 54);

        //special slots
        this.addSlotToContainer(new SlotItemHandler(tile.getInventory(), tile.getInventory().getSlots() - 5, 174, 80));
        for (int y = 0; y < 4; y++) {
            int slot = tile.getInventory().getSlots() - 4 + y;
            this.addSlotToContainer(new SlotItemHandler(tile.getInventory(), slot, 3, 54 + y * 18));
        }

        addPlayerSlots(player.inventory, 8, 216);
    }

    @Override
    public void detectAndSendChanges() {
        for (int i = 0; i < this.inventorySlots.size(); ++i) {
            ItemStack itemstack = this.inventorySlots.get(i).getStack();
            ItemStack itemstack1 = this.inventoryItemStacks.get(i);

            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
                if (isGuiContainer) {
                    tile.onInventoryChanged();
                }

                itemstack1 = itemstack.isEmpty() ? ItemStack.EMPTY : itemstack.copy();
                this.inventoryItemStacks.set(i, itemstack1);

                for (IContainerListener listener : this.listeners) {
                    (listener).sendSlotContents(this, i, itemstack1);
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return this.tile.isUsableByPlayer(player);
    }

    @Nonnull
    @Override
    public ItemStack transferStackInSlot(EntityPlayer user, int currentSlot) {
        int slotCount = tile.getInventory().getSlots();
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(currentSlot);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (currentSlot < slotCount) {
                if (!this.mergeItemStack(itemstack1, slotCount, this.inventorySlots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, slotCount - 5, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.getCount() <= 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }
}