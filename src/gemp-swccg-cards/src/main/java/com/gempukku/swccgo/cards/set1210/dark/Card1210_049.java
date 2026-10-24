package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayInterruptFromLostPileModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Spice Mines Of Kessel
 */
public class Card1210_049 extends AbstractNormalEffect {
    public Card1210_049() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Spice_Mines_Of_Kessel, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Captives sent to the Kessel spice mines spend the rest of their lives digging for glitterstim, a spice sold throughout the galaxy by smugglers and crime lords.");
        setGameText("Deploy on Kessel. While your Star Destroyer here, opponent may not play Interrupts from their Lost Pile. If opponent just forfeited a character here, may place this Effect in Lost Pile to retrieve Force equal to that character's forfeit. (Immune to Alter.)");
        addIcons(Icon.A_NEW_HOPE);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.system, Filters.title(Title.Kessel));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.system, Filters.title(Title.Kessel));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayInterruptFromLostPileModifier(self,
                new HereCondition(self, Filters.and(Filters.your(self), Filters.Star_Destroyer)), opponent));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String opponent = game.getOpponent(playerId);
        Filter forfeitedFilter = Filters.and(Filters.opponents(self), Filters.character);

        if (TriggerConditions.justForfeited(game, effectResult, opponent, forfeitedFilter)
                && TriggerConditions.justForfeitedToLostPileFromLocation(game, effectResult, forfeitedFilter, Filters.here(self))) {
            final LostFromTableResult lostResult = (LostFromTableResult) effectResult;
            final PhysicalCard forfeited = lostResult.getCard();
            final int forfeit = Math.max(0, (int) game.getModifiersQuerying().getForfeit(game.getGameState(), forfeited));
            if (forfeit <= 0) {
                return null;
            }

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId, GameTextActionId.LEGACY__SPICE_MINES_OF_KESSEL_V__RETRIEVE_FORCE);
            action.setText("Retrieve " + forfeit + " Force");
            action.setActionMsg("Place " + GameUtils.getCardLink(self) + " in Lost Pile to retrieve " + forfeit + " Force");
            action.appendCost(
                    new PlaceCardInLostPileFromTableEffect(action, self));
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, forfeit));
            return Collections.singletonList(action);
        }
        return null;
    }
}
