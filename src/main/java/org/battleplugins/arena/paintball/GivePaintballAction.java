package org.battleplugins.arena.paintball;

import org.battleplugins.arena.ArenaPlayer;
import org.battleplugins.arena.event.action.EventAction;
import org.battleplugins.arena.resolver.Resolvable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

public class GivePaintballAction extends EventAction {
    private static final String PAINTBALL_KEY = "paintball";
    private static final String AMOUNT_KEY = "amount";

    public GivePaintballAction(Map<String, String> params) {
        super(params, PAINTBALL_KEY);
    }

    @Override
    public void call(ArenaPlayer arenaPlayer, Resolvable resolvable) {
        String paintballKey = this.get(PAINTBALL_KEY);
        int amount = Integer.parseInt(this.getOrDefault(AMOUNT_KEY, "1"));
        Paintball paintball = ArenaPaintball.getInstance().getMainConfig().getPaintball(paintballKey);
        if (paintball == null) {
            ArenaPaintball.getInstance().getSLF4JLogger().warn("Invalid paintball " + paintballKey + ". Not giving paintball to player.");
            return;
        }

        ItemStack paintballItem = paintball.getItem().clone();
        paintballItem.editMeta(meta ->
                meta.getPersistentDataContainer().set(ArenaPaintball.getInstance().getPaintballItemKey(), PersistentDataType.STRING, paintballKey)
        );

        int maxStackSize = paintballItem.getMaxStackSize();
        if (amount > maxStackSize) {
            int stacks = amount / maxStackSize;
            int remaining = amount % maxStackSize;
            for (int i = 0; i < stacks; i++) {
                ItemStack stack = paintballItem.clone();
                stack.setAmount(maxStackSize);
                arenaPlayer.getPlayer().getInventory().addItem(stack);
            }

            paintballItem.setAmount(remaining);
        } else {
            paintballItem.setAmount(amount);
            arenaPlayer.getPlayer().getInventory().addItem(paintballItem);
        }
    }
}
