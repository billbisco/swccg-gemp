package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.CancelForceDrainBonusesFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.EpicEventCalculationTotalModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Imperial
 * Title: Death Star Gunner
 */
public class Card1210_008 extends AbstractImperial {
    public Card1210_008() {
        super(Side.DARK, 2, 2, 1, 1, 2, Title.Death_Star_Gunner, Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Most gunners in the Imperial Navy once aspired to be TIE pilots, but lacked sufficient skills to fly starfighters. A few were assigned to the Death Star to man the main artillery.");
        setGameText("While on Death Star: cancels Force drain bonuses from Death Star sites' game text, your [A New Hope] Epic Event total is cumulatively +2, and during your deploy phase, may place this character in Used Pile to take a Superlaser into hand from Reserve Deck; reshuffle.");
        addIcons(Icon.A_NEW_HOPE);
        addKeywords(Keyword.GUNNER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition onDeathStar = new OnCondition(self, Title.Death_Star);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelForceDrainBonusesFromCardModifier(self, Filters.Death_Star_site, onDeathStar));
        modifiers.add(new EpicEventCalculationTotalModifier(self, Filters.and(Filters.Epic_Event, Icon.A_NEW_HOPE), onDeathStar, 2, true));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__DEATH_STAR_GUNNER_V__TAKE_SUPERLASER;

        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.isOnSystem(game, self, Title.Death_Star)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Take Superlaser into hand");
            action.setActionMsg("Take Superlaser into hand from Reserve Deck");
            action.appendCost(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.Superlaser, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
