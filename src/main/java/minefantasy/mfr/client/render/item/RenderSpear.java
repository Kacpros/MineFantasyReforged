package minefantasy.mfr.client.render.item;

import codechicken.lib.model.bakedmodels.WrappedItemModel;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import minefantasy.mfr.api.helpers.CustomToolHelper;
import minefantasy.mfr.item.weapon.ItemHalbeard;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public class RenderSpear extends WrappedItemModel implements IItemRenderer {

	public RenderSpear(Supplier<ModelResourceLocation> wrappedModel) {
		super(wrappedModel);
	}

	@Override
	public void renderItem(ItemStack stack, TransformType transformType) {
		GlStateManager.pushMatrix();
		if (transformType != TransformType.GUI && transformType != TransformType.GROUND){

			if (entity != null){
				if (entity instanceof EntityPlayer && entity.isSwingInProgress && !(stack.getItem() instanceof ItemHalbeard)) {
					GlStateManager.rotate(90, 0, 0, 1);
					GlStateManager.translate(0.5F, -0.5F, 0);
				}
				else{
					GlStateManager.translate(-0.8F, -0.8F, 0);
				}
				if (entity.isSprinting() && transformType == TransformType.FIRST_PERSON_RIGHT_HAND){
					GlStateManager.rotate(90, 0, 0, 1);
				}
				GlStateManager.scale(3, 3, 1);
				renderWrapped(stack);

			}
		}
		else{
			renderWrapped(stack);
		}
		GlStateManager.popMatrix();
	}

	@Override
	public IModelState getTransforms() {
		return TransformUtils.DEFAULT_TOOL;
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return true;
	}
}