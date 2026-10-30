package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.GameTextModificationCondition;
import com.gempukku.swccgo.cards.evaluators.ConditionEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.AttackRunTotalModifier;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextOnSideOfLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Character
 * Subtype: Rebel
 * Title: Rebel Tech
 */
public class Card1103_017 extends AbstractRebel {
    public Card1103_017() {
        super(Side.LIGHT, 2, 2, 1, 1, 3, Title.Rebel_Tech, Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Del Goren, typical expert in communications and scanner jamming, served aboard the Rebel privateer corvette Spiral before joining the Alliance High Command.");
        setGameText("Dark Side game text on your non-docking bay Yavin 4 sites is canceled. Your Attack Run total is cumulatively +1. During your turn, may place this Rebel Tech in Used Pile to take one Advance Preparation or Death Star: Trench into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextOnSideOfLocationModifier(self,
                Filters.and(Filters.your(self), Filters.Yavin_4_site, Filters.not(Filters.docking_bay)),
                game.getDarkPlayer()));
        modifiers.add(new AttackRunTotalModifier(self, null,
                new ConditionEvaluator(1, 2, new GameTextModificationCondition(self, ModifyGameTextType.REBEL_TECH__DOUBLE_BONUS_TO_ATTACK_RUN)), true));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__REBEL_TECH__TAKE_ADVANCE_PREPARATION_OR_TRENCH;

        if (GameConditions.isDuringYourTurn(game, playerId)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Advance Preparation or Death Star: Trench into hand from Reserve Deck");
            action.appendCost(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.or(Filters.Advance_Preparation, Filters.Death_Star_Trench), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
