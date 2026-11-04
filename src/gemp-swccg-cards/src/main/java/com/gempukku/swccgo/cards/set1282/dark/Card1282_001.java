package com.gempukku.swccgo.cards.set1282.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.effects.SatisfyAllAttritionEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Character
 * Subtype: Alien
 * Title: Chevin (V)
 */
public class Card1282_001 extends AbstractAlien {
    public Card1282_001() {
        super(Side.DARK, 3, 3, 2, 1, 2, "Chevin", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Most Chevin are mercenaries, gun runners and slavers. Have strong communities on their homeworld. Wear clothing only because others do.");
        setGameText("When deployed, may use 1 Force to take a non-unique Trandoshan into hand from Reserve Deck; reshuffle. Power +1 while your other slaver present. While in battle at a site with two other slavers, may place this character out of play to satisfy all remaining attrition against you.");
        addIcons(Icon.JABBAS_PALACE, Icon.WARRIOR);
        addKeyword(Keyword.MERCENARY);
        setSpecies(Species.CHEVIN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new PresentWithCondition(self, Filters.and(Filters.your(self), Filters.other(self), Filters.slaver)), 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, final EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        List<OptionalGameTextTriggerAction> actions = new LinkedList<OptionalGameTextTriggerAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__CHEVIN_V__TAKE_TRANDOSHAN;

        // Check condition(s)
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take a non-unique Trandoshan into hand from Reserve Deck");
            // Pay cost(s)
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            // Perform result(s)
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(Filters.non_unique, Filters.Trandoshan), true));
            actions.add(action);
        }

        // Check condition(s)
        Filter otherSlaversAtSameSite = Filters.and(Filters.other(self), Filters.slaver, Filters.atSameSite(self));
        if (TriggerConditions.isResolvingBattleDamageAndAttrition(game, effectResult, playerId)
                && GameConditions.isInBattle(game, self)
                && GameConditions.isDuringBattleAt(game, Filters.site)
                && GameConditions.isAttritionRemaining(game, playerId)
                && GameConditions.canSpot(game, self, 2, otherSlaversAtSameSite)) {

            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place out of play to satisfy all attrition");
            // Pay cost(s)
            action.appendCost(
                    new PlaceCardOutOfPlayFromTableEffect(action, self));
            // Perform result(s)
            action.appendEffect(
                    new SatisfyAllAttritionEffect(action, playerId));
            actions.add(action);
        }
        return actions;
    }
}
