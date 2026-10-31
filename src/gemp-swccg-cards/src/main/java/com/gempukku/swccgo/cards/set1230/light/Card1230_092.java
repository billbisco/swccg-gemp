package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ActivateForceEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.modifiers.DestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Effect
 * Title: Luke's Ultimatum
 */
public class Card1230_092 extends AbstractNormalEffect {
    public Card1230_092() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Lukes_Ultimatum, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setLore("'Jabba! This is your last chance. Free us or die.'");
        setGameText("Deploy on table. Once per turn, during battle at a Jabba's Palace site, may say 'Free us or die!' to make your destiny draws +1 and your Interrupts immune to Sense for remainder of battle. May place this Effect out of play to cause a player to activate 2 Force.");
        addIcons(Icon.JABBAS_PALACE);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        GameTextActionId freeUsId = GameTextActionId.LEGACY__LUKES_ULTIMATUM_V__FREE_US_OR_DIE;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, freeUsId)
                && GameConditions.isDuringBattleAt(game, Filters.Jabbas_Palace_site)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, freeUsId);
            action.setText("Say 'Free us or die!'");
            action.setActionMsg("Make your destiny draws +1 and your Interrupts immune to Sense for remainder of battle");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new DestinyModifier(self, Filters.your(self), 1),
                            "Your destiny draws are +1"));
            action.appendEffect(
                    new AddUntilEndOfBattleModifierEffect(action,
                            new ImmuneToTitleModifier(self, Filters.and(Filters.your(self), Filters.Interrupt), Title.Sense),
                            "Your Interrupts are immune to Sense"));
            actions.add(action);
        }

        GameTextActionId activateId = GameTextActionId.LEGACY__LUKES_ULTIMATUM_V__ACTIVATE;
        final String opponent = game.getOpponent(playerId);
        final TopLevelGameTextAction activateAction = new TopLevelGameTextAction(self, gameTextSourceCardId, activateId);
        activateAction.setText("Place out of play to activate 2 Force");
        activateAction.appendCost(
                new PlaceCardOutOfPlayFromTableEffect(activateAction, self));
        activateAction.appendEffect(
                new PlayoutDecisionEffect(activateAction, playerId,
                        new MultipleChoiceAwaitingDecision("Choose a player to activate 2 Force", new String[]{playerId, opponent}) {
                            @Override
                            protected void validDecisionMade(int index, String result) {
                                activateAction.appendEffect(
                                        new ActivateForceEffect(activateAction, result, 2));
                            }
                        }
                )
        );
        actions.add(activateAction);

        return actions;
    }
}
