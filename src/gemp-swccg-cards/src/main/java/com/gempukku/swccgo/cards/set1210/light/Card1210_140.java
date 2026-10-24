package com.gempukku.swccgo.cards.set1210.light;

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
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.ForRemainderOfGameData;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfTurnModifierEffect;
import com.gempukku.swccgo.logic.effects.LoseCardFromTableEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardOnTableEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitValueToUseWhenForfeitedModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Special Modifications
 */
public class Card1210_140 extends AbstractNormalEffect {
    public Card1210_140() {
        super(Side.LIGHT, 3, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Special_Modifications, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Han's 'special modifications' for the Millennium Falcon included security mechanisms, deflector shields, hull plating, faster hyperdrive and enhanced weapons.");
        setGameText("Deploy on table. If your starship in battle is about to be lost before the damage segment, it is instead lost at end of battle (if forfeited, forfeit for 0). May place this Effect in Lost Pile to make your starship immune to attrition for remainder of turn. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
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
                && !GameConditions.isDamageSegmentOfBattle(game)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            final PhysicalCard cardToBeLost = result.getCardToBeLost();

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Make " + GameUtils.getFullName(cardToBeLost) + " lost at end of battle");
            action.setActionMsg("Make " + GameUtils.getCardLink(cardToBeLost) + " lost at end of battle instead");
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(cardToBeLost);
                            action.appendEffect(
                                    new AddUntilEndOfBattleModifierEffect(action, new ForfeitValueToUseWhenForfeitedModifier(self, cardToBeLost, 0), null));
                            List<PhysicalCard> cardsToMakeLost = self.getForRemainderOfGameData().get(gameTextSourceCardId) != null ? self.getForRemainderOfGameData().get(gameTextSourceCardId).getPhysicalCards() : null;
                            if (cardsToMakeLost == null) {
                                self.getForRemainderOfGameData().put(gameTextSourceCardId, new ForRemainderOfGameData(new ArrayList<PhysicalCard>()));
                                cardsToMakeLost = self.getForRemainderOfGameData().get(gameTextSourceCardId).getPhysicalCards();
                            }
                            cardsToMakeLost.add(cardToBeLost);
                            final int permCardId = self.getPermanentCardId();
                            action.appendEffect(
                                    new AddUntilEndOfGameActionProxyEffect(action,
                                            new AbstractActionProxy() {
                                                @Override
                                                public List<TriggerAction> getRequiredAfterTriggers(final SwccgGame game, EffectResult effectResult) {
                                                    List<TriggerAction> actions = new LinkedList<TriggerAction>();
                                                    PhysicalCard self = game.findCardByPermanentId(permCardId);

                                                    GameTextActionId gameTextActionId2 = GameTextActionId.OTHER_CARD_ACTION_2;

                                                    if (TriggerConditions.battleEnded(game, effectResult)
                                                            || TriggerConditions.battleCanceled(game, effectResult)) {
                                                        List<PhysicalCard> cardsToMakeLost2 = self.getForRemainderOfGameData().get(gameTextSourceCardId) != null ? self.getForRemainderOfGameData().get(gameTextSourceCardId).getPhysicalCards() : null;
                                                        if (cardsToMakeLost2 != null) {
                                                            for (Iterator<PhysicalCard> iterator = cardsToMakeLost2.iterator(); iterator.hasNext(); ) {
                                                                PhysicalCard cardToMakeLost2 = iterator.next();
                                                                if (!Filters.onTable.accepts(game, cardToMakeLost2)) {
                                                                    iterator.remove();
                                                                }
                                                            }

                                                            if (!cardsToMakeLost2.isEmpty()) {
                                                                final RequiredGameTextTriggerAction action2 = new RequiredGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId2);
                                                                action2.setRepeatableTrigger(true);
                                                                action2.setText("Make starship lost");
                                                                action2.appendTargeting(
                                                                        new ChooseCardOnTableEffect(action, game.getGameState().getCurrentPlayerId(), "Choose starship", cardsToMakeLost2) {
                                                                            @Override
                                                                            protected void cardSelected(final PhysicalCard cardToMakeLost3) {
                                                                                action2.addAnimationGroup(cardToMakeLost3);
                                                                                action2.setText("Make " + GameUtils.getCardLink(cardToMakeLost3) + " lost");
                                                                                cardsToMakeLost2.remove(cardToMakeLost3);
                                                                                action2.appendEffect(
                                                                                        new LoseCardFromTableEffect(action2, cardToMakeLost3, true));
                                                                            }
                                                                            @Override
                                                                            protected boolean getUseShortcut() {
                                                                                return true;
                                                                            }
                                                                        }
                                                                );
                                                                actions.add(action2);
                                                            }
                                                        }
                                                    }
                                                    return actions;
                                                }
                                            }
                                    ));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
