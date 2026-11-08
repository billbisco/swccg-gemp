package com.gempukku.swccgo.cards.set2240.dark;

import com.gempukku.swccgo.cards.set601.dark.Card601_087;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardsFromReserveDeckEffect;

import java.time.LocalDate;

/**
 * Set: Virtual Block 4
 * Type: Objective
 * Title: Hunt Down And Destroy The Jedi / Their Fire Has Gone Out Of The Universe (V)
 * PC-E2 version of 1240_58 (front): the Virtual Cards Master List text (Apr 2013). Same as PC-E1 except that the objective
 * may also deploy "Your Spies Have Located A Jedi?" instead of If The Trace Was Correct (no card of that title exists, so
 * the optional deploy finds nothing), and it names Rogue Shadow. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2240_2058 extends Card601_087 {
    public Card2240_2058() {
        super();
        setGameText("Deploy Coruscant system, Imperial City, and A Sith's Plans. May also deploy Your Spies Have Located A Jedi?.\n" +
                "For remainder of game, you may not deploy [Episode I] Dark Jedi. Opponent loses 2 Force when a character hit by Vader's (or Galen's) Lightsaber leaves table on the same turn.\n" +
                "While this side up, may ▲ Rogue Shadow. Galen's immunity to attrition is +2 for each Jedi present.\n" +
                "Flip this card if Galen or Vader at a battleground site and opponent does not have a unique (•) character of ability > 3 present at a battleground site.");
        setLegacyVersion(new LegacyVersion("1240_58", "PC-E2",
                "Virtual Cards Master List (official text 2013-04-27)", LocalDate.of(2013, 4, 27), LocalDate.of(2014, 2, 4)));
    }

    /** The Master List names this card; no card of this title exists, so the optional deploy finds nothing. */
    static final String YOUR_SPIES_HAVE_LOCATED_A_JEDI = "Your Spies Have Located A Jedi?";

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Coruscant_system, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Coruscant system to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.Imperial_City, true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Imperial City to deploy";
                    }
                });
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.title(Title.A_Siths_Plans), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose A Sith's Plans to deploy";
                    }
                });
        action.appendOptionalEffect(
                new DeployCardsFromReserveDeckEffect(action, Filters.title(YOUR_SPIES_HAVE_LOCATED_A_JEDI), 0, 1, true, false) {
                    public String getChoiceText(int numCardsToChoose) {
                        return "Choose Your Spies Have Located A Jedi? to deploy";
                    }
                });
        return action;
    }
}
