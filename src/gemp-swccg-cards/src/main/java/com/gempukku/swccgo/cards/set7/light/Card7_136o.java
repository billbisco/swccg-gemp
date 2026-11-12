package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.time.LocalDate;
import java.util.List;

/**
 * Set: Special Edition
 * Type: Objective
 * Title: Hidden Base
 * Text state: o (as printed, 1999; docs/legacy-versions.md "Text states")
 * <p>
 * The print takes the 'Hidden Base' indicator from the Reserve Deck (as Card7_136d) and may flip as soon as you deploy the
 * system matching the indicator; Decipher's Glossary Supplement (2002) added the five battleground systems. Its back side,
 * Card7_136o_BACK, lets opponent 'probe' during the draw phase instead of the deploy phase.
 */
public class Card7_136o extends Card7_136d {
    public Card7_136o() {
        super();
        setGameText("Deploy Rendezvous Point. Place a planet system (with a parsec number from 1 to 8) from Reserve Deck face down on your side of table (not in play); that card indicates the planet where your \"Hidden Base\" is located. While this side up, once during each of your deploy phases, may deploy one system from Reserve Deck; reshuffle. Opponent loses no more than 1 Force from each of your Force drains at systems and sectors. Flip this card any time after the system matching your \"Hidden Base\" indicator has been deployed.");
        setTextState(new TextState("7_136", "Printed",
                "Special Edition printing (Decipher, November 1999)",
                LocalDate.of(1999, 11, 1), LocalDate.of(2002, 1, 29)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        // May flip once you have deployed the system matching the 'Hidden Base' indicator (no battleground count)
        if (self.getWhileInPlayData() != null
                && TriggerConditions.justDeployed(game, effectResult, self.getOwner(), Filters.system)
                && !game.getGameState().getStackedCards(self).isEmpty()) {
            PhysicalCard deployedSystem = ((PlayCardResult) effectResult).getPlayedCard();
            String hiddenBaseTitle = game.getGameState().getStackedCards(self).iterator().next().getBlueprint().getTitle();
            if (deployedSystem.getBlueprint().getTitle().equals(hiddenBaseTitle)) {
                self.setWhileInPlayData(null);
            }
        }
        return null;
    }
}
