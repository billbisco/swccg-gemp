package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set601.dark.Card601_001;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.StackCardsFromOutsideDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Effect
 * Subtype: Starting
 * Title: Knowledge And Defense (V)
 * Original version of 1240_31: the Virtual Block 4 release sheet text (Dec 2009): deploys with up to 12 cards from outside
 * your deck under it, where the Virtual Cards Master List (Apr 2013) says 15. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_031 extends Card601_001 {
    /** Printed number of cards from outside your deck. */
    private static final int OUTSIDE_DECK_CARDS = 12;

    public Card2240_031() {
        super();
        setGameText("Deploy on table with up to 12 cards from outside your deck face-down under here. Four times per game, may play a Defensive Shield from here (as if from hand). Unless canceling your Interrupt, opponent may not play Recoil In Fear until the end of your first turn.");
        setLegacyVersion(new LegacyVersion("1240_31", "",
                "Virtual Block 4 release sheet (2009-12-07), card #29E", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)) {

            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.skipInitialMessageAndAnimation();
            // Perform result(s)
            action.appendEffect(
                    new StackCardsFromOutsideDeckEffect(action, playerId, 1, GameConditions.startingEffectShieldCap(game, OUTSIDE_DECK_CARDS), self, Filters.Defensive_Shield));
            return Collections.singletonList(action);
        }
        return null;
    }
}
