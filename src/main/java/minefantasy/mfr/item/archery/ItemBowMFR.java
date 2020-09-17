package minefantasy.mfr.item.archery;

import codechicken.lib.model.ModelRegistryHelper;
import minefantasy.mfr.MineFantasyReborn;
import minefantasy.mfr.api.archery.AmmoMechanicsMFR;
import minefantasy.mfr.api.archery.IAmmo;
import minefantasy.mfr.api.archery.IDisplayMFRAmmo;
import minefantasy.mfr.api.archery.IFirearm;
import minefantasy.mfr.api.archery.ISpecialBow;
import minefantasy.mfr.api.helpers.CustomToolHelper;
import minefantasy.mfr.api.material.CustomMaterial;
import minefantasy.mfr.client.render.RenderBow;
import minefantasy.mfr.init.CreativeTabMFR;
import minefantasy.mfr.init.SoundsMFR;
import minefantasy.mfr.network.NetworkHandler;
import minefantasy.mfr.proxy.IClientRegister;
import minefantasy.mfr.util.MFRLogUtil;
import minefantasy.mfr.util.ModelLoaderHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ItemBowMFR extends ItemBow implements ISpecialBow, IDisplayMFRAmmo, IFirearm, IClientRegister {
    public static final DecimalFormat decimal_format = new DecimalFormat("#.##");
    private final EnumBowType model;
    private int itemRarity;
    private float baseDamage = 1.0F;
    /**
     * Return the enchantability factor of the item, most of the time is based on
     * material.
     */
    private int enchantmentLvl = 1;
    // ===================================================== CUSTOM START
    // =============================================================\\
    private boolean isCustom = false;
    private boolean isPulling;
    int drawTime;
    private String designType = "standard";

    public ItemBowMFR(String name, EnumBowType type) {
        this(name, ToolMaterial.WOOD, type, 0);
    }

    public ItemBowMFR(String name, ToolMaterial mat, EnumBowType type, int rarity) {
        this(name, (int) (mat.getMaxUses() * type.durabilityModifier), type, mat.getAttackDamage(), rarity);
        this.enchantmentLvl = mat.getEnchantability();
    }

    private ItemBowMFR(String name, int dura, EnumBowType type, float damage, int rarity) {
        this.baseDamage = damage;
        model = type;
        this.maxStackSize = 1;
        this.setMaxDamage(dura);
        itemRarity = rarity;
        setRegistryName(name);
        setUnlocalizedName(name);
        setCreativeTab(CreativeTabMFR.tabOldTools);

        MineFantasyReborn.PROXY.addClientRegister(this);
    }

    @Override
    @SideOnly(Side.CLIENT)
    /**
     * Returns True is the item is renderer in full 3D when hold.
     */
    public boolean isFull3D() {
        return true;
    }

    /**
     * called when the player releases the use item button. Args: itemstack, world,
     * entityplayer, itemInUseCount
     */
    @Override
    public void onPlayerStoppedUsing(final ItemStack stack, final World worldIn, final EntityLivingBase entityLiving, final int timeLeft) {
        final int charge = this.getMaxItemUseDuration(stack) - timeLeft;
        fireArrow(stack, worldIn, entityLiving, charge);
    }

    /**
     * How long it takes to use or consume an item
     */
    @Override
    public int getMaxItemUseDuration(ItemStack item) {
        return 72000;
    }

    /**
     * returns the action that specifies what animation to play when the items is
     * being used
     */
    @Override
    public EnumAction getItemUseAction(ItemStack item) {
        return EnumAction.BOW;
    }

    @Override
    public void addInformation(ItemStack item, World world, List<String> list, ITooltipFlag flag) {
        super.addInformation(item, world, list, flag);

        CustomToolHelper.addBowInformation(item, list);
        ItemStack ammo = AmmoMechanicsMFR.getAmmo(item);
        if (!ammo.isEmpty()) {
            list.add(TextFormatting.DARK_GRAY + ammo.getDisplayName() + " x" + ammo.getCount());
        }

        list.add(TextFormatting.BLUE + I18n.format("attribute.bowPower.name",
                decimal_format.format(getBowDamage(item))));
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed.
     * Args: itemStack, world, entityPlayer
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(final World world, final EntityPlayer player, final EnumHand hand) {
        return nockArrow(player.getHeldItem(hand), world, player, hand);
    }

    public boolean canAccept(ItemStack ammo) {
        String ammoType = "null";
        ItemStack weapon = new ItemStack(this);
        if (!ammo.isEmpty() && ammo.getItem() instanceof IAmmo) {
            ammoType = ((IAmmo) ammo.getItem()).getAmmoType(ammo);
        }

        if (!weapon.isEmpty() && weapon.getItem() instanceof IFirearm) {
            return ((IFirearm) weapon.getItem()).canAcceptAmmo(weapon, ammoType);
        }

        return ammoType.equalsIgnoreCase("arrow");
    }

    private void reloadBow(ItemStack item, EntityPlayer player) {
        player.openGui(MineFantasyReborn.MOD_ID, NetworkHandler.GUI_RELOAD, player.world, 1, 0, 0);
    }

    @Override
    public int getItemEnchantability() {
        return enchantmentLvl;
    }

    @Override
    public void onUpdate(ItemStack item, World world, Entity entity, int i, boolean b) {
        super.onUpdate(item, world, entity, i, b);
        if (!item.hasTagCompound()) {
            item.setTagCompound(new NBTTagCompound());
            item.getTagCompound().setInteger("Use", i);
        }
        if  (isPulling){
            drawTime += 1;
        }
        else{
            drawTime = 0;
        }
    }

    public float getDrawAmount() {
        int delay = 34;
        if (drawTime > (delay))
            return 4F;
        else if (drawTime > (delay - 1))
            return 3F;
        else if (drawTime > (delay - 2))
            return 2F;
        else if (drawTime > (delay - 3))
            return 1F;

        return 0;
    }

    public void setPulling(boolean isPulling){
        this.isPulling = isPulling;
    }

    /**
     * Is ammunition required to fire this bow?
     *
     * @param bow     The bow
     * @param shooter The shooter
     * @return Is ammunition required?
     */
    protected boolean isAmmoRequired(final ItemStack bow, final EntityPlayer shooter) {
        return !shooter.capabilities.isCreativeMode && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, bow) == 0;
    }

    /**
     * Nock an arrow.
     *
     * @param bow     The bow ItemStack
     * @param player The player shooting the bow
     * @param world   The World
     * @param hand    The hand holding the bow
     * @return The result
     */
    protected ActionResult<ItemStack> nockArrow(final ItemStack bow, final World world, final EntityPlayer player, final EnumHand hand) {
        final boolean hasAmmo = !AmmoMechanicsMFR.isDepleted(bow);

        if (!world.isRemote && player.isSneaking() || AmmoMechanicsMFR.isDepleted(bow)) {
            reloadBow(bow, player);
            return ActionResult.newResult(EnumActionResult.FAIL, bow);
        }

        final ActionResult<ItemStack> ret = ForgeEventFactory.onArrowNock(bow, world, player, hand, hasAmmo);
        if (ret != null) return ret;

        if (isAmmoRequired(bow, player) && !hasAmmo) {
            return new ActionResult<>(EnumActionResult.FAIL, bow);
        } else {
            player.setActiveHand(hand);
            return new ActionResult<>(EnumActionResult.SUCCESS, bow);
        }
    }

    /**
     * Fire an arrow with the specified charge.
     *
     * @param bow     The bow ItemStack
     * @param world   The firing player's World
     * @param shooter The player firing the bow
     * @param charge  The charge of the arrow
     */
    protected void fireArrow(final ItemStack bow, final World world, final EntityLivingBase shooter, int charge) {
        if (!(shooter instanceof EntityPlayer)) return;

        final EntityPlayer player = (EntityPlayer) shooter;
        final boolean ammoRequired = isAmmoRequired(bow, player);
        ItemStack ammoStack = AmmoMechanicsMFR.getAmmo(bow);

        charge = ForgeEventFactory.onArrowLoose(bow, world, player, charge, !ammoStack.isEmpty()|| !ammoRequired);
        if (charge < 0) return;

        if (!ammoStack.isEmpty() || !ammoRequired) {
            if (ammoStack.isEmpty()) {
                ammoStack = new ItemStack(Items.ARROW);
            }

            final float arrowVelocity = getArrowVelocity(charge);

            if (arrowVelocity >= 0.1) {
                final boolean isInfinite = player.capabilities.isCreativeMode || ammoStack.getItem() instanceof ItemArrow && ((ItemArrow) ammoStack.getItem()).isInfinite(ammoStack, bow, player);

                if (!world.isRemote) {
                    final ItemArrow itemArrow = (ItemArrow) (ammoStack.getItem() instanceof ItemArrow ? ammoStack.getItem() : Items.ARROW);
                    EntityArrow entityArrow = itemArrow.createArrow(world, ammoStack, player);
                    entityArrow.shoot(player, player.rotationPitch, player.rotationYaw, 0.0F, arrowVelocity * 3.0F, 1.0F);

                    float firepower = arrowVelocity / model.chargeTime;

                    if (firepower < 0.1D) {
                        return;
                    }
                    if (firepower > 1.0F) {
                        firepower = 1.0F;
                    }

                    if (firepower == 1.0f) {
                        entityArrow.setIsCritical(true);
                    }

                    final int powerLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, bow);
                    if (powerLevel > 0) {
                        entityArrow.setDamage(entityArrow.getDamage() + (double) powerLevel * 0.5D + 0.5D);
                    }

                    final int punchLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, bow);
                    if (punchLevel > 0) {
                        entityArrow.setKnockbackStrength(punchLevel);
                    }

                    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, bow) > 0) {
                        entityArrow.setFire(100);
                    }

                    AmmoMechanicsMFR.damageContainer(bow, player, 1);
                    world.playSound(player.posX, player.posY, player.posZ, SoundsMFR.BOW_FIRE, SoundCategory.NEUTRAL, 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + firepower * 0.5F, true);


                    if (isInfinite) {
                        entityArrow.pickupStatus = EntityArrow.PickupStatus.CREATIVE_ONLY;
                    }

                    entityArrow = (EntityArrow) modifyArrow(bow, entityArrow);

                    world.spawnEntity(entityArrow);
                }

                if (!isInfinite && ammoStack.getCount() > 0) {
                    ammoStack.shrink(1);
                }

                player.addStat(StatList.getObjectUseStats(this));
            }
        }
    }

    @Override
    public EnumRarity getRarity(ItemStack item) {
        int lvl = itemRarity;

        EnumRarity[] rarity = new EnumRarity[]{EnumRarity.COMMON, EnumRarity.UNCOMMON, EnumRarity.RARE, EnumRarity.EPIC};
        if (item.isItemEnchanted()) {
            if (lvl == 0) {
                lvl++;
            }
            lvl++;
        }
        if (lvl >= rarity.length) {
            lvl = rarity.length - 1;
        }
        return rarity[lvl];
    }

    @Override
    public Entity modifyArrow(ItemStack bow, Entity arrow) {
        if (this.isCustom) {
            CustomMaterial custom = CustomToolHelper.getCustomPrimaryMaterial(bow);
            if (custom != null) {
                if (custom.name.equalsIgnoreCase("silver")) {
                    arrow.getEntityData().setBoolean("MF_Silverbow", true);
                }
            }
        }

        float dam = getBowDamage(bow);

        arrow.getEntityData().setFloat("MF_Bow_Damage", dam);
        arrow.getEntityData().setString("Design", designType);

        return arrow;
    }

    @Override
    public boolean canAcceptAmmo(ItemStack weapon, String ammo) {
        return ammo.equalsIgnoreCase("arrow");
    }

    @Override
    public int getAmmoCapacity(ItemStack item) {
        return 1;
    }

    public ItemBowMFR setCustom(String designType) {
        canRepair = false;
        isCustom = true;
        this.designType = designType;
        return this;
    }

    public ItemStack construct(String main, String haft) {
        return CustomToolHelper.construct(this, main, haft);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack item) {
        String unlocalName = this.getUnlocalizedNameInefficiently(item) + ".name";
        return CustomToolHelper.getWoodenLocalisedName(item, unlocalName);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return CustomToolHelper.getMaxDamage(stack, super.getMaxDamage(stack));
    }

    @Override
    public void getSubItems( CreativeTabs tab, NonNullList<ItemStack> items) {
        if (!isInCreativeTab(tab)) {
            return;
        }
        if (isCustom) {
            ArrayList<CustomMaterial> wood = CustomMaterial.getList("wood");
            for (CustomMaterial customMat : wood) {
                if (MineFantasyReborn.isDebug() || customMat.getItemStack().isEmpty()) {
                    items.add(this.construct("iron", customMat.name));
                }
            }
        }
    }

    public float getBowDamage(ItemStack item) {
        return CustomToolHelper.getBowDamage(item, baseDamage) * model.damageModifier;
    }

    @Override
    public float getRange(ItemStack item) {
        return model.velocity;
    }

    @Override
    public float getSpread(ItemStack item) {
        return model.spread;
    }

    // ====================================================== CUSTOM END
    // ==============================================================\\
    @Override
    public float getMaxCharge() {
        return model.chargeTime;
    }

    @Override
    public void registerClient() {
        ModelLoaderHelper.registerItem(this);
        ModelResourceLocation modelLocation = new ModelResourceLocation(getRegistryName(), "normal");
        ModelLoader.setCustomModelResourceLocation(this, 0, modelLocation);
        ModelRegistryHelper.register(modelLocation, new RenderBow(() -> modelLocation));

    }




}
