package dev.screret.mitm.client.model.blockentity;

import dev.screret.mitm.MITMUtil;
import dev.screret.mitm.common.block.entity.SummoningCircleBlockEntity;

import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class SummoningCircleModel extends DefaultedBlockGeoModel<SummoningCircleBlockEntity> {

    public SummoningCircleModel() {
        super(MITMUtil.id("summoning_circle"));
    }
}
