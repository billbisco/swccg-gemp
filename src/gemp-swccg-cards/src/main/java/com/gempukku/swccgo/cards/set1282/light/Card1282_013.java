package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.PresentAtCondition;
import com.gempukku.swccgo.cards.conditions.StackedOnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.common.Zone;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.AboutToLeaveTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Character
 * Subtype: Alien
 * Title: Vilmarh 'Villie' Grahrk
 */
public class Card1282_013 extends AbstractAlien {
    public Card1282_013() {
        super(Side.LIGHT, 3, 3, 3, 2, 3, "Vilmarh 'Villie' Grahrk", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setLore("Devaronian gambler. Smuggler.");
        setGameText("Adds 2 to the power of anything he pilots. While present at Audience Chamber or stacked on your Objective: non-unique Devaronians are deploy -1, power and forfeit +1, and if Yerka Mig is about to be lost, may place it in Used Pile instead.");
        addIcons(Icon.PILOT, Icon.EPISODE_I);
        addKeywords(Keyword.GAMBLER, Keyword.SMUGGLER);
        setSpecies(Species.DEVARONIAN);
    }

    private static Filter nonUniqueDevaronians() {
        return Filters.and(Filters.non_unique, Filters.species(Species.DEVARONIAN));
    }

    private static List<Modifier> devaronianModifiers(PhysicalCard self, Condition condition) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, nonUniqueDevaronians(), condition, -1));
        modifiers.add(new PowerModifier(self, nonUniqueDevaronians(), condition, 1));
        modifiers.add(new ForfeitModifier(self, nonUniqueDevaronians(), condition, 1));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.addAll(devaronianModifiers(self, new PresentAtCondition(self, Filters.Audience_Chamber)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileStackedModifiers(SwccgGame game, final PhysicalCard self) {
        return devaronianModifiers(self, new StackedOnCondition(self, Filters.and(Filters.your(self), Filters.Objective)));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (new PresentAtCondition(self, Filters.Audience_Chamber).isFulfilled(game.getGameState(), game.getModifiersQuerying())) {
            return yerkaMigToUsedPile(playerId, game, effectResult, self, gameTextSourceCardId);
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggersWhenStacked(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (playerId.equals(self.getOwner())
                && Filters.stackedOn(self, Filters.and(Filters.your(self), Filters.Objective)).accepts(game, self)) {
            return yerkaMigToUsedPile(playerId, game, effectResult, self, gameTextSourceCardId);
        }
        return null;
    }

    private static List<OptionalGameTextTriggerAction> yerkaMigToUsedPile(String playerId, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isAboutToBeLost(game, effectResult, Filters.title(Title.Yerka_Mig))) {
            final AboutToLeaveTableResult result = (AboutToLeaveTableResult) effectResult;
            final PhysicalCard yerkaMig = result.getCardAboutToLeaveTable();
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
            action.setText("Place Yerka Mig in Used Pile instead");
            action.setActionMsg("Place " + GameUtils.getCardLink(yerkaMig) + " in Used Pile instead");
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(yerkaMig);
                            action.appendEffect(
                                    new PlaceCardInUsedPileFromTableEffect(action, yerkaMig, false, Zone.USED_PILE));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
