package com.gempukku.swccgo.cards.set1250.dark;

import com.gempukku.swccgo.cards.AbstractTransportVehicle;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelImmunityToAttritionUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.DisembarkEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployWithPilotOrDriverAsReactModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.List;

/** Legacy Block 5: Maul's Sith Speeder. */
public class Card1250_031 extends AbstractTransportVehicle {
    public Card1250_031() {
        super(Side.DARK, 5, 1, 1, 6f, null, 6, 2, "Maul's Sith Speeder", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setLore("Attain speeds of up to 600 kph. 3 meters long. Extremely difficult to control at high speeds. Dengar rode a similar swoop when injured in a race with Han at Agrilat.");
        setGameText("May add Maul as driver. May deploy with Maul as a 'react'. During battle, Maul may disembark (place this vehicle in Used Pile) to cancel the immunity to attrition of a Jedi here. Immune to attrition.");
        addIcons(Icon.SPECIAL_EDITION, Icon.EPISODE_I, Icon.LEGACY_BLOCK_5);
        setAsLegacy(true);
        addKeyword(Keyword.SWOOP);
        setDriverCapacity(1);
    }

    @Override
    protected Filter getGameTextValidDriverFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.Maul;
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new MayDeployWithPilotOrDriverAsReactModifier(self, Filters.Maul));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ImmuneToAttritionModifier(self));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                    PhysicalCard self, int sourceId) {
        if (!GameConditions.isDuringBattleWithParticipant(game, self)) return null;

        var maul = Filters.findFirstActive(game, self, Filters.and(Filters.Maul, Filters.driving(self)));
        var site = self.getAtLocation();
        Filter jediHere = Filters.and(Filters.Jedi, Filters.here(self), Filters.hasAnyImmunityToAttrition);
        if (maul == null || site == null || !GameConditions.canTarget(game, self, jediHere)) return null;

        var action = new TopLevelGameTextAction(self, playerId, sourceId);
        action.setText("Maul disembarks to cancel Jedi attrition immunity");
        action.appendTargeting(new TargetCardOnTableEffect(action, playerId, "Choose Jedi here", jediHere) {
            @Override
            protected void cardTargeted(int targetGroup, PhysicalCard target) {
                action.appendCost(new DisembarkEffect(action, maul, site, false, false));
                action.appendCost(new PlaceCardInUsedPileFromTableEffect(action, self));
                action.allowResponses("Cancel the selected Jedi's immunity to attrition", new RespondableEffect(action) {
                    @Override
                    protected void performActionResults(Action targetingAction) {
                        var jedi = action.getPrimaryTargetCard(targetGroup);
                        action.appendEffect(new CancelImmunityToAttritionUntilEndOfBattleEffect(action,
                                Filters.sameCardId(jedi), "Cancel Jedi's immunity to attrition"));
                    }
                });
            }
        });
        return List.of(action);
    }
}
