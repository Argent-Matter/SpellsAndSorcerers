package dev.screret.mitm.client.model.blockentity;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.common.blockentity.SummonSignBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class SummonSignModel extends DefaultedBlockGeoModel<SummonSignBlockEntity> {

    public SummonSignModel() {
        super(MITMUtil.id("summon_sign"));
    }
}
