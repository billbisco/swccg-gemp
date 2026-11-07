package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MovesForFreeModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.List;

/** Legacy Block 6: Veteran Rogue. */
public class Card1263_024 extends AbstractRebel {
    public Card1263_024() {
        super(Side.LIGHT, 3, 1, 0, 2, 2, "Veteran Rogue", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setGameText("Adds 2 to power of anything he pilots. While piloting a T-47 or X-wing: it is maneuver +2, it moves for free, and if this character is about to be lost, may use 1 Force to place him in Used Pile instead.");
        addIcons(Icon.PILOT, Icon.DEATH_STAR_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var pilotedSpeederOrXWing = Filters.and(Filters.or(Filters.T_47, Filters.X_wing), Filters.hasPiloting(self));
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 2),
                new ManeuverModifier(self, pilotedSpeederOrXWing, 2),
                new MovesForFreeModifier(self, pilotedSpeederOrXWing));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(
            String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        var qualifyingPilot = Filters.and(Filters.sameCardId(self), Filters.piloting(Filters.or(Filters.T_47, Filters.X_wing)));
        if (!TriggerConditions.isAboutToBeLost(game, effectResult, qualifyingPilot)
                || !GameConditions.canUseForce(game, playerId, 1)) {
            return null;
        }
        var loss = (AboutToLoseCardFromTableResult) effectResult;
        if (loss.getPreventableCardEffect() == null) {
            return null;
        }
        var action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Place Veteran Rogue in Used Pile instead");
        action.appendCost(new UseForceEffect(action, playerId, 1));
        action.appendEffect(new PassthruEffect(action) {
            @Override
            protected void doPlayEffect(SwccgGame game) {
                loss.getPreventableCardEffect().preventEffectOnCard(self);
            }
        });
        action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, self, false, Zone.USED_PILE));
        return List.of(action);
    }
}
