package com.gempukku.swccgo.cards.set1104.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.cards.effects.usage.TwicePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardsEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.FerocityModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 4: Battle Of Hoth
 * Type: Effect
 * Title: Krayt Dragon Bones
 */
public class Card1104_041 extends AbstractNormalEffect {
    public Card1104_041() {
        super(Side.DARK, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Krayt_Dragon_Bones, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_4, Rarity.V);
        setVirtualSuffix(true);
        setLore("The bones of a krayt dragon represent both death and life: death for the prey, life for the predator. A symbol of the ebb and flow of the Force through all things.");
        setGameText("Deploy on table. Your ferocious creatures are deploy -2 and ferocity +2. Once per turn, may ▲ a ferocious creature. Twice per game, may deploy a ferocious creature from under your Starting Effect. (Immune to Alter.)");
        addIcons(Icon.HOTH);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourFerociousCreatures = Filters.and(Filters.your(self), Filters.creature);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostModifier(self, yourFerociousCreatures, -2));
        modifiers.add(new FerocityModifier(self, yourFerociousCreatures, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        Filter ferociousCreature = Filters.creature;

        GameTextActionId takeId = GameTextActionId.OVS__KRAYT_DRAGON_BONES__TAKE_FEROCIOUS_CREATURE;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, takeId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, takeId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take a ferocious creature into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, ferociousCreature, true));
            actions.add(action);
        }

        GameTextActionId deployId = GameTextActionId.OVS__KRAYT_DRAGON_BONES__DEPLOY_FROM_STARTING_EFFECT;
        PhysicalCard startingEffect = Filters.findFirstActive(game, self, Filters.and(Filters.your(self), Filters.Starting_Effect));
        if (GameConditions.isTwicePerGame(game, self, deployId)
                && startingEffect != null
                && GameConditions.hasStackedCards(game, startingEffect, ferociousCreature)) {
            final PhysicalCard stackedOn = startingEffect;
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, deployId);
            action.setText("Deploy card from under Starting Effect");
            action.setActionMsg("Deploy a ferocious creature from under Starting Effect");
            action.appendUsage(
                    new TwicePerGameEffect(action));
            action.appendEffect(
                    new DeployStackedCardsEffect(action, stackedOn, ferociousCreature, 1, 1, false));
            actions.add(action);
        }

        return actions;
    }
}
