package com.gempukku.swccgo.logic;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.List;

/**
 * Legacy Heroic characteristic helpers. VS12: in battle, weapons that can target an unhit
 * participating Heroic card may not target that owner's non-Heroic cards.
 */
public final class Heroic {
    private Heroic() {
    }

    /** OR of a weapon's per-line target filters. Empty is {@link Filters#none}. */
    public static Filterable orFilters(List<? extends Filterable> lines) {
        Filterable wide = Filters.none;
        for (Filterable line : lines) {
            wide = Filters.or(wide, line);
        }
        return wide;
    }

    /**
     * Restricts one target line so non-Heroic cards drop out while any line of this weapon
     * can still target an unhit participating Heroic card. The candidate is checked with
     * {@code lineFilter}. The Heroic search uses {@code weaponWideFilter} (OR of every line)
     * so a Heroic that matches only another line still blocks this one.
     * No-op outside battle. Heroic cards themselves always pass.
     * All-cards / fires-without-targeting weapons never call this.
     */
    public static Filter restrictWeaponTargets(SwccgGame game, PhysicalCard weapon, Filterable lineFilter, Filterable weaponWideFilter) {
        final Filter line = Filters.and(lineFilter);
        final Filter weaponWide = Filters.and(weaponWideFilter);
        return new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return acceptsWithHeroicRestriction(gameState, modifiersQuerying, physicalCard, false);
            }

            @Override
            public boolean acceptsIgnoringOwner(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return acceptsWithHeroicRestriction(gameState, modifiersQuerying, physicalCard, true);
            }

            private boolean acceptsWithHeroicRestriction(GameState gameState, ModifiersQuerying modifiersQuerying,
                                                        PhysicalCard physicalCard, boolean ignoringOwner) {
                boolean lineAccepts = ignoringOwner
                        ? line.acceptsIgnoringOwner(gameState, modifiersQuerying, physicalCard)
                        : line.accepts(gameState, modifiersQuerying, physicalCard);
                if (!lineAccepts) {
                    return false;
                }
                if (!gameState.isDuringBattle()) {
                    return true;
                }
                if (Filters.and(Keyword.HEROIC).accepts(gameState, modifiersQuerying, physicalCard)) {
                    return true;
                }
                Filter heroicThisWeaponCanTarget = Filters.and(
                        Keyword.HEROIC,
                        Filters.owner(physicalCard.getOwner()),
                        Filters.participatingInBattle,
                        Filters.not(Filters.hit),
                        weaponWide);
                return !Filters.canSpot(game, weapon, heroicThisWeaponCanTarget);
            }
        };
    }
}
