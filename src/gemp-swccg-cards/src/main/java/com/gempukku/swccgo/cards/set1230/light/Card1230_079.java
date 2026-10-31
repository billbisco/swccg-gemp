package com.gempukku.swccgo.cards.set1230.light;

import com.gempukku.swccgo.cards.AbstractDroid;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.effects.PlaceHandInUsedPileEffect;
import com.gempukku.swccgo.logic.effects.choose.DrawCardsIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToPlayInterruptModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 3
 * Type: Character
 * Subtype: Droid
 * Title: See-Threepio
 */
public class Card1230_079 extends AbstractDroid {
    public Card1230_079() {
        super(Side.LIGHT, 2, 2, 1, 5, Title.See_Threepio, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("C-3PO was Jabba's 'khan chita,' or translator. Survived more battles than most members of the Alliance. Wasn't informed of R2-D2's role in the rescue of Han.");
        setGameText("Unless Jabba on table: you must first use 3 Force to play How Did We Get Into This Mess?; and, once per turn, if with R2-D2 at a Jabba's Palace site, may place your hand in Used Pile to draw the same number of cards from Reserve Deck.");
        addIcons(Icon.PREMIUM);
        addModelType(ModelType.PROTOCOL);
        addPersona(Persona.C3PO);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ExtraForceCostToPlayInterruptModifier(self, Filters.How_Did_We_Get_Into_This_Mess,
                new NotCondition(new OnTableCondition(self, Filters.Jabba)), 3));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SEE_THREEPIO_V__PLACE_HAND_DRAW;

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && !GameConditions.canSpot(game, self, Filters.Jabba)
                && GameConditions.isPresentWith(game, self, Filters.R2D2)
                && GameConditions.isAtLocation(game, self, Filters.Jabbas_Palace_site)
                && GameConditions.hasHand(game, playerId)
                && GameConditions.hasReserveDeck(game, playerId)) {

            final int numCards = game.getGameState().getHand(playerId).size();
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Place hand in Used Pile to draw");
            action.setActionMsg("Place hand in Used Pile to draw " + numCards + " cards from Reserve Deck");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendCost(
                    new PlaceHandInUsedPileEffect(action, playerId));
            action.appendEffect(
                    new DrawCardsIntoHandFromReserveDeckEffect(action, playerId, numCards));
            return Collections.singletonList(action);
        }
        return null;
    }
}
