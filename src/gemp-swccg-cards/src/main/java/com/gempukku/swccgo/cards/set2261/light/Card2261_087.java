package com.gempukku.swccgo.cards.set2261.light;

import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.set1261.light.Card1261_087;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.decisions.ArbitraryCardsSelectionDecision;
import com.gempukku.swccgo.logic.decisions.DecisionResultInvalidException;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.modifiers.ForceRetrievalFromCardMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Set: Virtual Block 6.1
 * Type: Epic Event
 * Title: Strong Is Vader
 * Pre-errata version of 1261_87: the first Virtual Block 6.1 release sheet text (Sep 2009): up to 6 <i>different</i>
 * Jedi Tests, and the Jedi Test retrieval cannot be canceled while you occupy a battleground. Both went with the
 * 2010-04-20 sheet revision. Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_087 extends Card1261_087 {
    public Card2261_087() {
        setGameText("If [Virtual] Mind What You Have Learned on table, deploy on table with up to 6 different Jedi Tests under here from outside your deck. You may not Force drain at Dagobah locations or deploy asteroid sectors, [Maintenance] cards, or [Permanent Weapon] cards. Whenever you draw training destiny, draw two and choose one. You may retrieve 3 Force whenever you complete a Jedi Test (cannot be canceled while you occupy a battleground). Completed Jedi Tests are placed on table and are suspended (not lost) whenever character who completed them not on table.");
        setLegacyVersion(new LegacyVersion("1261_87", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #89", LocalDate.of(2009, 9, 4), LocalDate.of(2009, 9, 21)));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(
            SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.justDeployed(game, result, self))
            return super.getGameTextRequiredAfterTriggers(game, result, self, sourceId);
        String player = self.getOwner();
        var state = game.getGameState();
        var tests = new ArrayList<>(Filters.filter(state.getOutsideOfDeck(player), game, Filters.Jedi_Test));
        if (tests.isEmpty()) return List.of();
        var action = new RequiredGameTextTriggerAction(self, sourceId);
        action.setPerformingPlayer(player);
        action.setText("Stack up to six different Jedi Tests from outside your deck");
        action.appendEffect(new PlayoutDecisionEffect(action, player,
                new ArbitraryCardsSelectionDecision("Choose up to six different Jedi Tests to stack", tests, 0, Math.min(6, tests.size())) {
                    @Override
                    public void decisionMade(String result) throws DecisionResultInvalidException {
                        var selected = getSelectedCardsByResponse(result);
                        var titles = new HashSet<String>();
                        for (var test : selected) {
                            if (!titles.add(test.getTitle()))
                                throw new DecisionResultInvalidException("Choose different Jedi Tests");
                        }
                        for (var test : selected) {
                            state.removeCardFromZone(test);
                            state.stackCard(test, self, true, false, false);
                        }
                    }
                }));
        return List.of(action);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String player = self.getOwner();
        var modifiers = new ArrayList<>(super.getGameTextWhileActiveInPlayModifiers(game, self));
        modifiers.add(new ForceRetrievalFromCardMayNotBeCanceledModifier(self, self,
                new OccupiesCondition(player, Filters.battleground), player));
        return modifiers;
    }
}
