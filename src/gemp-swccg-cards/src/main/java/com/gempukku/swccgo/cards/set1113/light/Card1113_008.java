package com.gempukku.swccgo.cards.set1113.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.DrawDestinyAndChooseInsteadEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 13: Remote Outpost
 * Type: Character
 * Subtype: Republic
 * Title: Lieutenant Williams
 */
public class Card1113_008 extends AbstractRepublic {
    public Card1113_008() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, Title.Williams, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_13, Rarity.V);
        setVirtualSuffix(true);
        setLore("Pilot of the Radiant VII. Was personally requested by Captain Madakor to assist in the transportation of the Jedi ambassadors.");
        setGameText("Adds 2 to power of anything he pilots. While piloting Radiant VII, draws one battle destiny if unable to otherwise. During battle at a system, if about to draw a battle destiny here, may instead draw two and choose one; if at opponent's system, may take the other destiny into hand.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR);
        setMatchingStarshipFilter(Filters.Radiant_VII);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Radiant_VII), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isAboutToDrawBattleDestiny(game, effectResult, playerId)
                && GameConditions.isDuringBattleAt(game, Filters.system)
                && GameConditions.canDrawDestinyAndChoose(game, 1)
                && new AtCondition(self, Filters.here(self)).isFulfilled(game.getGameState(), game.getModifiersQuerying())) {
            boolean opponentsSystem = GameConditions.canSpot(game, self, Filters.and(Filters.system, Filters.here(self), Filters.occupies(game.getOpponent(playerId))));
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Draw two battle destiny and choose one");
            action.appendEffect(new DrawDestinyAndChooseInsteadEffect(action, 2, 1, opponentsSystem));
            return Collections.singletonList(action);
        }
        return null;
    }
}
