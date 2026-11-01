package com.gempukku.swccgo.cards.set1114.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.FirstBattleDestinyBeingDrawnCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
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
 * Set: Virtual Set 14: Scum and Villainy
 * Type: Character
 * Subtype: Imperial
 * Title: Admiral Ozzel
 */
public class Card1114_017 extends AbstractImperial {
    public Card1114_017() {
        super(Side.DARK, 0, 2, 3, 2, 4, "Admiral Ozzel", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_14, Rarity.V);
        setVirtualSuffix(true);
        setLore("Leader of the Emperor's Death Squadron assigned to hunt down and crush the Rebellion. As clumsy as he is stupid. Has just failed Darth Vader for the next-to-last time.");
        setGameText("Adds 2 to power of anything he pilots. While piloting a capital starship at a battleground system, in each battle a player's first battle destiny draw may not be canceled or reduced and if opponent canceled a just drawn battle destiny, they lose 1 Force.");
        addPersona(Persona.OZZEL);
        addIcons(Icon.PILOT, Icon.HOTH);
        addKeywords(Keyword.ADMIRAL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingCapitalAtBattlegroundSystem = new AndCondition(
                new PilotingCondition(self, Filters.capital_starship),
                new com.gempukku.swccgo.cards.conditions.AtCondition(self, Filters.and(Filters.system, Filters.battleground)));
        Condition firstBattleDestiny = new AndCondition(pilotingCapitalAtBattlegroundSystem, new FirstBattleDestinyBeingDrawnCondition());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
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
