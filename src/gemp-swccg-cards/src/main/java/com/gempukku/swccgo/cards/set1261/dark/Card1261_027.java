package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import com.gempukku.swccgo.cards.conditions.OccupiesMoreThanOpponentCondition;
import com.gempukku.swccgo.logic.modifiers.ForceRetrievalFromCardMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;
import java.util.Arrays;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 6.1
 * Type: Defensive Shield
 * Title: You Cannot Hide Forever
 */
public class Card1261_027 extends AbstractDefensiveShield {
    public Card1261_027() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.You_Cannot_Hide_Forever, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setBasePrintingDisplayTitle("You Cannot Hide Forever (Ref III)");
        setLore("'Give yourself to the dark side. It is the only way you can save your friends.'");
        setGameText("Plays on table. If an 'insert' card was just inserted or revealed, it is canceled. You lose no Force to Boonta Eve Podrace, and opponent's retrieval from Boonta Eve Podrace may not be canceled. While you occupy more battlegrounds than opponent, I Did It! is suspended.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.isPlayingCardAsInsertCard(game, effect, Filters.any)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (TriggerConditions.justRevealedInsertCard(game, effectResult, Filters.any)
                && GameConditions.canCancelRevealedInsertCard(game, self, effectResult)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            // Build action using common utility
            CancelCardActionBuilder.buildCancelRevealedInsertCardAction(action, effectResult);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        return Arrays.asList(
                new NoForceLossFromCardModifier(self, Filters.Boonta_Eve_Podrace, playerId),
                new ForceRetrievalFromCardMayNotBeCanceledModifier(self, Filters.Boonta_Eve_Podrace, game.getOpponent(playerId)),
                new SuspendsCardModifier(self, Filters.I_Did_It, new OccupiesMoreThanOpponentCondition(playerId, Filters.battleground)));
    }
}
