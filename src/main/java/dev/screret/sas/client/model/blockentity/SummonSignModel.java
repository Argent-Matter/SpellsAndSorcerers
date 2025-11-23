package dev.screret.sas.client.model.blockentity;

import dev.screret.sas.Util;
import dev.screret.sas.blockentity.blockentity.SummonSignBlockEntity;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;

public class SummonSignModel extends DefaultedBlockGeoModel<SummonSignBlockEntity> {

    public SummonSignModel() {
        super(Util.id("summon_sign"));
    }
}
