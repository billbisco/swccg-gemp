package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceAtLocationFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.DuelTotalModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Character (Rebel)
 * Title: Ben Kenobi
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print says "if a battle just ended, may 'revive' ... one character forfeited from same site this turn"; the Players
 * Committee Advanced Rulebook (Appendix A) added "here" and "your" ("if a battle just ended here ... your character"). This
 * state may revive after any battle ends; the character still comes from your Lost Pile.
 */
public class Card7_004o extends Card7_004 {
    public Card7_004o() {
        super();
        setGameText("Deploys only on Tatooine. When in a duel, adds 2 to your total. Once per turn, if a battle just ended, may 'revive' (place here from Lost Pile) one character forfeited from same site this turn. Immune to attrition < 5.");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_4", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.BEN_KENOBI__REVIVE_CHARACTER;

        // Check condition(s): printed text, any battle just ended (not only one here)
        if (TriggerConditions.battleEnded(game, effectResult)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canSearchLostPile(game, playerId, self, gameTextActionId)
                && GameConditions.canReviveCharacters(game)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("'Revive' a forfeited character");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerTurnEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new PlaceAtLocationFromLostPileEffect(action, playerId, Filters.and(Filters.character,
                            Filters.forfeitedFromLocationThisTurn(Filters.sameSite(self))), Filters.sameSite(self), false, false));
            return Collections.singletonList(action);
        }
        return null;
    }
}
