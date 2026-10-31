package com.gempukku.swccgo.cards.set1109.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 9
 * Type: Character
 * Subtype: Imperial
 * Title: Lieutenant Watts
 */
public class Card1109_042 extends AbstractImperial {
    public Card1109_042() {
        super(Side.DARK, 2, 2, 2, 2, 4, Title.Watts, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_9, Rarity.V);
        setVirtualSuffix(true);
        setLore("Native of Corulag. Watts' gunnery skills produce devastating results. Temporarily assigned to Kuat Drive Yards to work on prototype AT-ST weaponry.");
        setGameText("Adds 2 to power of anything he pilots. Once per turn, if at a battleground site, may take a weapon from Lost Pile into hand to cancel a Force drain at a related site.");
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.ENDOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__LIEUTENANT_WATTS_V__TAKE_WEAPON_FROM_LOST_PILE;

        if (TriggerConditions.forceDrainInitiatedAt(game, effectResult, Filters.and(Filters.site, Filters.relatedSite(self)))
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.isAtLocation(game, self, Filters.battleground_site)
                && GameConditions.canCancelForceDrain(game, self)
                && GameConditions.canTakeCardsIntoHandFromLostPile(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take weapon into hand to cancel Force drain");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromLostPileEffect(action, playerId, Filters.weapon, true));
            action.appendEffect(
                    new CancelForceDrainEffect(action));
            return Collections.singletonList(action);
        }
        return null;
    }
}
