package com.gempukku.swccgo.logic;

import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

/**
 * Legacy Heroic characteristic helpers. VS12: in battle, weapons that can target an unhit
 * participating Heroic card may not target that owner's non-Heroic cards.
 */
public final class Heroic {
    private Heroic() {
    }

    /**
     * Restricts a weapon's already-built target filter so non-Heroic cards drop out while a
     * legal Heroic target remains. No-op outside battle. Heroic cards themselves always pass.
     * All-cards / fires-without-targeting weapons never call this.
     */
    public static Filter restrictWeaponTargets(SwccgGame game, PhysicalCard weapon, Filterable baseFilter) {
        final Filter base = Filters.and(baseFilter);
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
                boolean baseAccepts = ignoringOwner
                        ? base.acceptsIgnoringOwner(gameState, modifiersQuerying, physicalCard)
                        : base.accepts(gameState, modifiersQuerying, physicalCard);
                if (!baseAccepts) {
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
                        base);
                return !Filters.canSpot(game, weapon, heroicThisWeaponCanTarget);
            }
        };
    }
}
