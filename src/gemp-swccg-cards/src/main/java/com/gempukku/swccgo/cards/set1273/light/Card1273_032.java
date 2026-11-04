package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.LegacyVb73Light;
import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeStackedCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Effect
 * Title: Bacta Infirmary
 */
public class Card1273_032 extends AbstractNormalEffect {
    public Card1273_032() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Bacta Infirmary", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setLore("Medical wonder invented centuries ago by the Vratix insectoid species. Gelatinous red bacta is suspended in a synthetic solution to create a healing bacterial medium.");
        setGameText("If Center Of Tyranny on table, deploy on table. Insurrection is lost. Rogue Squadron pilots of ability < 3 are forfeit +1 and, if just lost from table, may be placed here. During your control phase, may use 2 Force to take any character from here into hand. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    private static Filter rogueSquadronPilotsOfAbilityLessThan3() {
        return Filters.and(Filters.Rogue_Squadron_pilot, Filters.abilityLessThan(3));
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, LegacyVb73Light.centerOfTyranny());
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForfeitModifier(self, rogueSquadronPilotsOfAbilityLessThan3(), 1));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Insurrection is lost
        if (TriggerConditions.isTableChanged(game, effectResult)) {
            final PhysicalCard insurrection = Filters.findFirstActive(game, self, Filters.Insurrection);
            if (insurrection != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setSingletonTrigger(true);
                action.setText("Make Insurrection lost");
                action.setActionMsg("Make " + GameUtils.getCardLink(insurrection) + " lost");
                // Perform result(s)
                action.appendEffect(
                        new LoseCardFromTableEffect(action, insurrection));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // Rogue Squadron pilots of ability < 3, if just lost from table, may be placed here
        if (TriggerConditions.justLost(game, effectResult, Filters.and(Filters.your(self), rogueSquadronPilotsOfAbilityLessThan3()))) {
            PhysicalCard cardLost = ((LostFromTableResult) effectResult).getCard();

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place " + GameUtils.getFullName(cardLost) + " here");
            action.setActionMsg("Place " + GameUtils.getCardLink(cardLost) + " on " + GameUtils.getCardLink(self));
            // Perform result(s)
            action.appendEffect(
                    new StackOneCardFromLostPileEffect(action, cardLost, self, false, true, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BACTA_INFIRMARY__TAKE_CHARACTER_INTO_HAND;

        // Check condition(s)
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.CONTROL)
                && GameConditions.hasStackedCards(game, self, Filters.character)
                && GameConditions.canUseForce(game, playerId, 2)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Take character here into hand");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 2));
            // Perform result(s)
            action.appendEffect(
                    new TakeStackedCardIntoHandEffect(action, playerId, self, Filters.character));
            return Collections.singletonList(action);
        }
        return null;
    }
}
