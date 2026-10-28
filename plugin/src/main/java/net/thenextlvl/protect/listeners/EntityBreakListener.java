package net.thenextlvl.protect.listeners;

import io.papermc.paper.event.entity.EntityBreakByEntityEvent;
import io.papermc.paper.event.entity.EntityBreakEvent;
import net.thenextlvl.protect.ProtectPlugin;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class EntityBreakListener implements Listener {
    private final ProtectPlugin plugin;

    public EntityBreakListener(final ProtectPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityBreak(final EntityBreakEvent event) {
        if (event.getCause().equals(EntityBreakEvent.RemoveCause.EXPLOSION)) {
            final var area = plugin.areaProvider().getArea(event.getEntity());
            event.setCancelled(!area.getFlag(plugin.flags.explosions));
            return;
        }
        if (!(event instanceof final EntityBreakByEntityEvent byEntity)) return;
        if (event.getEntity() instanceof Hanging) return;
        final var remover = byEntity.getRemover() instanceof final Projectile projectile
                && projectile.getShooter() instanceof final Entity shooter
                ? shooter : byEntity.getRemover();
        final var area = plugin.areaProvider().getArea(event.getEntity());
        event.setCancelled(!plugin.protectionService().canAttack(remover, event.getEntity()));
        plugin.failed(remover, event, area, "area.failed.attack");
    }
}
