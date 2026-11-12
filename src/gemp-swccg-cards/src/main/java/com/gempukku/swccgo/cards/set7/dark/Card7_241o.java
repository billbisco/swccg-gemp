package com.gempukku.swccgo.cards.set7.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PutCardFromLostPileInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.RetrieveForceEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Effect
 * Subtype: Normal
 * Title: Sienar Fleet Systems
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print retrieves 1 Force each time you deploy a TIE. The Players Committee Advanced Rulebook (Appendix A) added
 * "(or 3 if a TIE squadron)", which the bare card (Card7_241) plays; this state retrieves 1 Force for a TIE squadron too.
 * Everything else is the bare card's code.
 */
public class Card7_241o extends Card7_241 {
    public Card7_241o() {
        super();
        setGameText("Deploy on Wakeelmui system. Retrieve 1 Force each time you deploy a TIE. Also, once per turn you may relocate a TIE just lost from table to Used Pile. (Immune to Alter while you occupy Wakeelmui.)");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_241", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2017, 3, 1)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        GameTextActionId gameTextActionId = GameTextActionId.OTHER_CARD_ACTION_1;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, playerId, Filters.TIE)) {
            final PhysicalCard playedCard = ((PlayCardResult) effectResult).getPlayedCard();
            // Printed text: 1 Force for every TIE, including a TIE squadron (no "or 3")
            int numToRetrieve = 1;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Retrieve " + numToRetrieve + " Force");
            action.setActionMsg("Have " + playerId + " retrieve " + numToRetrieve + " Force");
            // Perform result(s)
            action.appendEffect(
                    new RetrieveForceEffect(action, playerId, numToRetrieve) {
                        @Override
                        public Collection<PhysicalCard> getAdditionalCardsInvolvedInForceRetrieval() {
                            return Collections.singletonList(playedCard);
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
