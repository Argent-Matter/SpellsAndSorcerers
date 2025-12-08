package dev.screret.motm.integration.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;

@EmiEntrypoint
public class MOTMEmiPlugin implements EmiPlugin {

    @Override
    public void register(EmiRegistry registry) {}
}
