package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
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

import java.util.List;

/** Legacy Block 5 Lieutenant Williams (V), printing 43. */
public class Card1250_043 extends AbstractRepublic {
    public Card1250_043() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, Title.Williams, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Pilot of the Radiant VII. Was personally requested by Captain Madakor to assist in the transportation of the Jedi ambassadors.");
        setGameText("Adds 2 to power of anything he pilots. While piloting Radiant VII, draws one battle destiny if unable to otherwise. During battle at a system, if about to draw a battle destiny here, may instead draw two and choose one; if at opponent's system, may take other destiny into hand.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.PILOT, Icon.WARRIOR);
        setMatchingStarshipFilter(Filters.Radiant_VII);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 2),
                new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, new PilotingCondition(self, Filters.Radiant_VII), 1));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.isAboutToDrawBattleDestiny(game, result, playerId)
                || !GameConditions.isDuringBattleAt(game, Filters.and(Filters.system, Filters.here(self)))
                || !GameConditions.canDrawDestinyAndChoose(game, 1)) return null;

        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
        action.setText("Draw two battle destiny and choose one");
        action.appendEffect(new DrawDestinyAndChooseInsteadEffect(action, 2, 1));
        return List.of(action);
    }
}
