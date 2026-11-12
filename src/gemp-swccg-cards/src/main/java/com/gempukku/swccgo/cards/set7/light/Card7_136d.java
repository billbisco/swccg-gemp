package com.gempukku.swccgo.cards.set7.light;

import com.gempukku.swccgo.cards.AbstractObjective;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.ObjectiveDeployedTriggerAction;
import com.gempukku.swccgo.cards.effects.SetWhileInPlayDataEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.WhileInPlayData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.FlipCardEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.PlayCardResult;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Set: Special Edition
 * Type: Objective
 * Title: Hidden Base
 * Text state: d (Decipher's last word; docs/legacy-versions.md "Text states")
 * <p>
 * Decipher Glossary Supplement (29 January 2002) text, wiki [[Hidden Base / Systems Will Slip Through Your Fingers
 * (Errata)]]: the 'Hidden Base' indicator still comes from the Reserve Deck (the Players Committee later made it "from
 * outside your deck", the bare card Card7_136); the flip condition and the back side play as the bare card.
 */
public class Card7_136d extends Card7_136 {
    public Card7_136d() {
        super();
        setGameText("Deploy Rendezvous Point. Place a planet system (with a parsec number from 1 to 8) from Reserve Deck face down on your side of table (not in play); that card indicates the planet where your \"Hidden Base\" is located. While this side up, once during each of your deploy phases, may deploy one system from Reserve Deck; reshuffle. Opponent loses no more than 1 Force from each of your Force drains at systems and sectors. Flip this card any time after you have deployed five battleground systems and your \"Hidden Base\" system.");
        // supersededOn is an upper bound: the wiki does not date the Players Committee rewrite.
        setTextState(new TextState("7_136", "D-E1",
                "Decipher Glossary Supplement, 29 January 2002",
                LocalDate.of(2002, 1, 29), LocalDate.of(2017, 3, 1)));
    }

    @Override
    protected ObjectiveDeployedTriggerAction getGameTextWhenDeployedAction(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        ObjectiveDeployedTriggerAction action = new ObjectiveDeployedTriggerAction(self);
        action.appendRequiredEffect(
                new DeployCardFromReserveDeckEffect(action, Filters.and(Filters.Rendezvous_Point), true, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose Rendezvous Point to deploy";
                    }
                });
        action.appendRequiredEffect(
                new StackCardFromReserveDeckEffect(action, playerId, self, Filters.and(Filters.planet_system, Filters.planetSystemInParsecRange(1, 8)), false, false) {
                    @Override
                    public String getChoiceText() {
                        return "Choose 'Hidden Base' from Reserve Deck";
                    }
                });
        action.appendRequiredEffect(
                new SetWhileInPlayDataEffect(action, self, new WhileInPlayData(new HashMap<String, Boolean>())));
        return action;
    }
}
