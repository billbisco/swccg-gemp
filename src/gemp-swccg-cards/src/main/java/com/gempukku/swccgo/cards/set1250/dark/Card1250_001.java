package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.effects.AddToForceDrainEffect;
import com.gempukku.swccgo.cards.set1114.dark.Card1114_029;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfForceDrainModifierEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ForceDrainInitiatedResult;

import java.util.List;

/** Legacy Block 5: Nothing Can Get Through Our Shield (V). */
public class Card1250_001 extends Card1114_029 {
    public Card1250_001() {
        setGameText("At each system a player controls with a capital starship, his non-[Presence] characters and vehicles deploy -1 to related sites and sectors. If you occupy a system with a capital starship (or three unique starships) and have no Objective on table, may add 1 to each of your Force drains at related battleground sites (opponent loses no more than 2 Force from those drains).");
        addIcons(Icon.EPISODE_I, Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.VIRTUAL_BLOCK_5;
    }

    private Filter systemWithFleet(PhysicalCard self, String playerId, boolean requiresControl) {
        Filter systemState = requiresControl ? Filters.controls(playerId) : Filters.occupies(playerId);
        return Filters.and(Filters.system, systemState, new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying querying, PhysicalCard system) {
                Filter ownShips = Filters.and(Filters.owner(playerId), Filters.starship, Filters.presentAt(system));
                if (Filters.canSpot(state.getGame(), self, Filters.and(ownShips, Filters.capital_starship))) {
                    return true;
                }
                return !requiresControl && Filters.countActive(state.getGame(), self,
                        Filters.and(ownShips, Filters.unique)) >= 3;
            }
        });
    }

    private Modifier discount(PhysicalCard self, String playerId) {
        Filter eligible = Filters.and(Filters.owner(playerId), Filters.or(Filters.character, Filters.vehicle),
                Filters.not(Icon.PRESENCE));
        Filter relatedPlaces = Filters.and(Filters.or(Filters.site, Filters.sector),
                Filters.relatedSystemTo(self, systemWithFleet(self, playerId, true)));
        return new DeployCostToLocationModifier(self, eligible, -1, relatedPlaces);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(discount(self, game.getDarkPlayer()), discount(self, game.getLightPlayer()));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
                                                                                  EffectResult result, PhysicalCard self,
                                                                                  int sourceId) {
        if (!(result instanceof ForceDrainInitiatedResult drain)
                || !playerId.equals(drain.getPerformingPlayerId())
                || !Filters.filterAllOnTable(game, Filters.and(Filters.owner(playerId), Filters.Objective)).isEmpty()) {
            return null;
        }
        var location = drain.getLocation();
        Filter eligibleSite = Filters.and(Filters.battleground, Filters.site,
                Filters.relatedSystemTo(self, systemWithFleet(self, playerId, false)));
        if (!eligibleSite.accepts(game, location)) return null;

        var action = new OptionalGameTextTriggerAction(self, sourceId);
        action.setText("Add 1 to this Force drain (opponent loses at most 2)");
        action.appendEffect(new AddToForceDrainEffect(action, 1));
        action.appendEffect(new AddUntilEndOfForceDrainModifierEffect(action,
                new LimitForceLossFromForceDrainModifier(self, location, 2, game.getOpponent(playerId)),
                "Limit Force loss from this drain to 2"));
        return List.of(action);
    }
}
