package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.set104.dark.Card104_007;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collection;
import java.util.List;

/** Legacy Block 6.3: Walker Garrison (V). */
public class Card1263_013 extends Card104_007 {
    public Card1263_013() {
        setVirtualSuffix(true);
        setAsLegacy(true);
        addIcon(Icon.LEGACY_BLOCK_6);
        setLore("When efficiently deployed, a squadron of AT-ATs can quickly take control of a wide area, making it easy for Imperial forces to dominate a planet.");
        setGameText("If your AT-ATs occupy three related battleground planet sites, your Force drains at same and related battlegrounds this turn are +1 (or +2 if at Hoth). OR If you occupy a site with an AT-AT, cancel a Force drain at a related site.");
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_6_3;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.V;
    }

    private Filter occupiedWalkerSite(String playerId, PhysicalCard self) {
        return Filters.and(Filters.site, Filters.occupies(playerId), Filters.sameSiteAs(self,
                Filters.and(Filters.your(self), Filters.AT_AT, Filters.piloted)));
    }

    private Collection<PhysicalCard> qualifyingSites(String playerId, SwccgGame game, PhysicalCard self) {
        var sites = Filters.filterTopLocationsOnTable(game,
                Filters.and(occupiedWalkerSite(playerId, self), Filters.battleground, Filters.planet_site));
        return sites.stream().filter(site -> {
            Filter related = Filters.relatedSite(site);
            return sites.stream().filter(other -> other != site && related.accepts(game, other))
                    .limit(2).count() == 2;
        }).toList();
    }

    @Override
    protected List<PlayInterruptAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self) {
        if (qualifyingSites(playerId, game, self).isEmpty()) return null;

        var action = new PlayInterruptAction(game, self);
        action.setText("Increase Force drains at related battlegrounds this turn");
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                var sites = qualifyingSites(playerId, game, self);
                if (sites.isEmpty()) return;
                var covered = Filters.or(sites.stream()
                        .map(site -> Filters.or(Filters.sameCardId(site), Filters.relatedLocation(site)))
                        .toArray(Filter[]::new));
                var battlegrounds = Filters.and(Filters.battleground, covered);
                action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                        new ForceDrainModifier(self, Filters.and(battlegrounds, Filters.not(Filters.Hoth_location)),
                                1, playerId), "Force drains +1 at related battlegrounds"));
                action.appendEffect(new AddUntilEndOfTurnModifierEffect(action,
                        new ForceDrainModifier(self, Filters.and(battlegrounds, Filters.Hoth_location),
                                2, playerId), "Force drains +2 at related Hoth battlegrounds"));
            }
        });
        return List.of(action);
    }

    @Override
    protected List<PlayInterruptAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game,
                                                                       EffectResult result, PhysicalCard self) {
        Filter relatedSite = Filters.and(Filters.site,
                Filters.relatedLocationTo(self, occupiedWalkerSite(playerId, self)));
        if (!TriggerConditions.forceDrainInitiatedAt(game, result, relatedSite)
                || !GameConditions.canCancelForceDrain(game, self)) {
            return null;
        }

        var action = new PlayInterruptAction(game, self);
        action.setText("Cancel Force drain at a related site");
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override
            protected void performActionResults(Action targetingAction) {
                action.appendEffect(new CancelForceDrainEffect(action));
            }
        });
        return List.of(action);
    }
}
