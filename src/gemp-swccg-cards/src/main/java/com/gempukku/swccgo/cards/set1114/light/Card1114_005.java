package com.gempukku.swccgo.cards.set1114.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.FirstBattleDestinyBeingDrawnCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.CancelDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotCancelBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotModifyBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 14: Armed & Operational
 * Type: Character
 * Subtype: Rebel
 * Title: General Walex Blissex
 */
public class Card1114_005 extends AbstractRebel {
    public Card1114_005() {
        super(Side.LIGHT, 3, 2, 1, 2, 3, Title.General_Walex_Blissex, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_14, Rarity.V);
        setVirtualSuffix(true);
        setLore("Engineer who worked with Jan Dodonna to design the A-wing starfighter. Given honorary rank due to his service to the Rebellion.");
        setGameText("Adds 1 to power of anything he pilots. While piloting a capital starship at a battleground system, in each battle a player's first battle destiny draw may not be canceled or reduced and if opponent canceled a just drawn battle destiny, they lose 1 Force.");
        addIcons(Icon.PILOT, Icon.DEATH_STAR_II);
        addKeywords(Keyword.GENERAL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingCapitalAtBattlegroundSystem = new AndCondition(
                new PilotingCondition(self, Filters.capital_starship),
                new com.gempukku.swccgo.cards.conditions.AtCondition(self, Filters.and(Filters.system, Filters.battleground)));
        Condition firstBattleDestiny = new AndCondition(pilotingCapitalAtBattlegroundSystem, new FirstBattleDestinyBeingDrawnCondition());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 1));
        modifiers.add(new MayNotCancelBattleDestinyModifier(self, Filters.here(self), firstBattleDestiny, null));
        modifiers.add(new MayNotModifyBattleDestinyModifier(self, Filters.here(self), firstBattleDestiny, null));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        if (effect instanceof CancelDestinyEffect
                && opponent.equals(effect.getAction().getPerformingPlayer())
                && game.getGameState().isDuringBattle()
                && Filters.and(Filters.piloting(Filters.capital_starship), Filters.at(Filters.and(Filters.system, Filters.battleground))).accepts(game, self)) {

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Opponent loses 1 Force");
            action.setActionMsg("Make opponent lose 1 Force for canceling a just drawn battle destiny");
            action.appendEffect(
                    new LoseForceEffect(action, opponent, 1));
            return Collections.singletonList(action);
        }
        return null;
    }
}
