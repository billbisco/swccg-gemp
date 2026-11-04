package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.LegacyIKnowThatLaugh;
import com.gempukku.swccgo.cards.conditions.CardsInHandFewerThanCondition;
import com.gempukku.swccgo.cards.evaluators.InBattleEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Effect
 * Title: I Know That Laugh
 */
public class Card1282_021 extends AbstractNormalEffect {
    public Card1282_021() {
        super(Side.LIGHT, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, LegacyIKnowThatLaugh.TITLE, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setLore("Jabba's decadent behavior makes him susceptible to deception. Leia and Lando exploited this weakness, posing as Jabba's kind of scum.");
        setGameText("If Agents In The Court on table, deploy on table. While you have < 13 cards in hand, non-unique aliens are immune to Monnok. For each of your aliens in battle, attrition against opponent is +1. If your alien was just lost from a location, may stack it here face down. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.canSpot(game, self, Filters.Agents_In_The_Court);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.non_unique, Filters.alien),
                new CardsInHandFewerThanCondition(playerId, 13), Title.Monnok));
        modifiers.add(new AttritionModifier(self, new InBattleEvaluator(self, Filters.and(Filters.your(playerId), Filters.alien)), opponent));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.justLostFromLocation(game, effectResult, Filters.and(Filters.your(playerId), Filters.alien), Filters.any)) {
            final PhysicalCard alien = ((LostFromTableResult) effectResult).getCard();
            if (alien.getZone() == Zone.LOST_PILE || alien.getZone() == Zone.TOP_OF_LOST_PILE) {
                OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Stack " + GameUtils.getFullName(alien) + " here");
                action.setActionMsg("Stack " + GameUtils.getCardLink(alien) + " on " + GameUtils.getCardLink(self));
                action.appendEffect(
                        new StackOneCardFromLostPileEffect(action, alien, self, !LegacyIKnowThatLaugh.stacksFaceUp(alien), false, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
