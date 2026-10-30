package com.gempukku.swccgo.cards.set1102.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 2: Our Most Desperate Hour
 * Type: Effect
 * Title: Special Modifications (V)
 */
public class Card1102_023 extends AbstractNormalEffect {
    public Card1102_023() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Special_Modifications, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Han's 'special modifications' for the Millennium Falcon included security mechanisms, deflector shields, hull plating, faster hyperdrive and enhanced weapons.");
        setGameText("Deploy on table. Your starships about to be lost during the weapons segment of battle remain in play instead. May place this Effect in Lost Pile to make your starship immune to attrition for remainder of turn.");
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SPECIAL_MODIFICATIONS_V__PLACE_IN_LOST_PILE;

        if (GameConditions.canTarget(game, self, Filters.and(Filters.your(self), Filters.starship))) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Make a starship immune to attrition");
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starship", Filters.and(Filters.your(self), Filters.starship)) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, final PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.appendCost(
                                    new PlaceCardInLostPileFromTableEffect(action, self));
                            action.allowResponses("Make " + GameUtils.getCardLink(targetedCard) + " immune to attrition for remainder of turn",
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(
                                                    new AddUntilEndOfTurnModifierEffect(action,
                                                            new ImmuneToAttritionModifier(self, targetedCard),
                                                            "Makes " + GameUtils.getCardLink(targetedCard) + " immune to attrition"));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, final EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        if (TriggerConditions.isAboutToBeLost(game, effectResult, Filters.and(Filters.your(self), Filters.starship, Filters.participatingInBattle))
                && GameConditions.isDuringBattle(game)
                && !game.getGameState().getBattleState().isReachedPowerSegment()) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            final PhysicalCard cardToBeLost = result.getCardToBeLost();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Remain in play");
            action.setActionMsg("Make " + GameUtils.getCardLink(cardToBeLost) + " remain in play instead");
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(cardToBeLost);
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
