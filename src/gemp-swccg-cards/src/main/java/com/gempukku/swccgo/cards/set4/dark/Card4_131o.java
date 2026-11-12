package com.gempukku.swccgo.cards.set4.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.YesNoDecision;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Set: Dagobah
 * Title: Shot In The Dark
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * As printed it may be used "at any time"; Decipher's Glossary Supplement (29 January 2002) limited it to once per turn (the current card, Card4_131), so this state has no once-per-turn limit.
 */
public class Card4_131o extends Card4_131 {
    public Card4_131o() {
        super();
        setGameText("Deploy on your side of table. At any time, you may lose 1 Force to draw the top card of your Reserve Deck into your hand. If that card is a space creature, you may immediately deploy it for free.");
        setTextState(new TextState("4_131", "Printed",
                "Dagobah printing (Decipher, April 1997)",
                LocalDate.of(1997, 4, 1), LocalDate.of(2002, 1, 29)));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.hasReserveDeck(game, playerId)) {
            int forceToLoseAsCost = GameConditions.hasGameTextModification(game, self, ModifyGameTextType.SHOT_IN_THE_DARK__LOSE_ADDITIONAL_FORCE_TO_DRAW) ? 2 : 1;

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Draw top card of Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new LoseForceEffect(action, playerId, forceToLoseAsCost, true));
            // Perform result(s)
            action.appendEffect(
                    new DrawCardIntoHandFromReserveDeckEffect(action, playerId) {
                        @Override
                        protected void cardDrawnIntoHand(final PhysicalCard cardDrawn) {
                            if (Filters.and(Filters.space_creature, Filters.deployable(self, null, true, 0)).accepts(game, cardDrawn)) {
                                action.appendEffect(
                                        new PlayoutDecisionEffect(action, playerId,
                                                new YesNoDecision("Do you want to deploy " + GameUtils.getCardLink(cardDrawn) + " for free?") {
                                                    @Override
                                                    protected void yes() {
                                                        action.appendEffect(
                                                                new DeployCardFromHandEffect(action, cardDrawn, true));
                                                    }
                                                }));
                            }
                        }
                    }
            );
            return Collections.singletonList(action);
        }
        return null;
    }
}
