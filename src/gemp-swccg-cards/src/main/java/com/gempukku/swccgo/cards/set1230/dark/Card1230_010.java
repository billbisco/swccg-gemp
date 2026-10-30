package com.gempukku.swccgo.cards.set1230.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.DrivingCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Grond
 */
public class Card1230_010 extends AbstractImperial {
    public Card1230_010() {
        super(Side.DARK, 2, 2, 2, 2, 3, Title.Grond, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Colonel Dyer's aide. Coordinates scout actions. As a youth, raced swoops on homeworld of Corellia. Formerly served with Emperor's Demonstration Team.");
        setGameText("Adds 2 to power of anything he pilots. Adds one battle destiny if with Colonel Dyer or if driving a swoop. May place Lieutenant Grond in Used Pile to cancel a Force drain at a related location.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.ENDOR);
        addKeywords(Keyword.SCOUT);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new AddsBattleDestinyModifier(self, new OrCondition(
                new WithCondition(self, Filters.title(Title.Dyer)),
                new DrivingCondition(self, Filters.swoop)), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LIEUTENANT_GROND_V__CANCEL_FORCE_DRAIN;

        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.relatedLocation(self))
                && GameConditions.canCancelForceDrain(game, self)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Cancel Force drain");
            action.appendCost(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
