package dev.screret.motm.client.model.blockentity;

import dev.screret.motm.MOTMUtil;
import dev.screret.motm.common.block.entity.SummoningCircleBlockEntity;

import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class SummoningCircleModel extends DefaultedBlockGeoModel<SummoningCircleBlockEntity> {

    public SummoningCircleModel() {
        super(MOTMUtil.id("summoning_circle"));
    }
}
