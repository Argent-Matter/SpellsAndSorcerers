package screret.sas.entity.entity.boss.cthulhu.part;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.neoforged.neoforge.entity.PartEntity;
import screret.sas.entity.entity.boss.cthulhu.CthulhuEntity;

public class CthulhuPart extends PartEntity<CthulhuEntity> {
    public final CthulhuEntity parentMob;
    public final String name;
    private final EntityDimensions size;

    public CthulhuPart(CthulhuEntity pParentMob, String pName, float pWidth, float pHeight) {
        super(pParentMob);
        this.size = EntityDimensions.scalable(pWidth, pHeight);
        this.refreshDimensions();
        this.parentMob = pParentMob;
        this.name = pName;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public boolean isPickable() {
        return true;
    }

    public boolean hurt(DamageSource pSource, float pAmount) {
        return !this.isInvulnerableTo(pSource) && this.parentMob.hurt(this, pSource, pAmount);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {

    }
}
