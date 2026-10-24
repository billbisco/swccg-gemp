package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardsInHandEqualToOrFewerThanCondition;
import com.gempukku.swccgo.cards.effects.SetTargetedCardEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetId;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.TargetCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotHaveCardsPlacedOnItModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotLookAtOpponentsHandModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotRemoveCardsFromOpponentsHandModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Effect
 * Title: Organa's Ceremonial Necklace
 */
public class Card1210_047 extends AbstractNormalEffect {
    public Card1210_047() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Organas_Ceremonial_Necklace, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Necklace worn by Princess Leia during the awards ceremony after the Battle of Yavin. A powerful artifact in the Alderaanian royal family for dozens of generations.");
        setGameText("Deploy on table. While you have < 13 cards in hand, opponent may not peek at or remove those cards (except with Grimtaash). May lose 2 Force to target two [Coruscant] Political Effects; opponent may not place cards on either until the next time you use this text. (Immune to Alter.)");
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter targetedPoliticalEffects = Filters.or(
                Filters.targetedByCardOnTableAsTargetId(self, TargetId.EFFECT_TARGET_1),
                Filters.targetedByCardOnTableAsTargetId(self, TargetId.EFFECT_TARGET_2));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotRemoveCardsFromOpponentsHandModifier(self, opponent, new CardsInHandEqualToOrFewerThanCondition(playerId, 12), Filters.except(Filters.Grimtaash)));
        modifiers.add(new MayNotLookAtOpponentsHandModifier(self, opponent, new CardsInHandEqualToOrFewerThanCondition(playerId, 12), Filters.except(Filters.Grimtaash)));
        modifiers.add(new MayNotHaveCardsPlacedOnItModifier(self, targetedPoliticalEffects, opponent));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ORGANAS_CEREMONIAL_NECKLACE_V__TARGET_POLITICAL_EFFECTS;
        Filter coruscantPoliticalEffects = Filters.and(Filters.Political_Effect, Icon.CORUSCANT);

        if (GameConditions.canSpot(game, self, 2, coruscantPoliticalEffects)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Target two Political Effects");
            action.appendCost(
                    new LoseForceEffect(action, playerId, 2, true));
            action.appendTargeting(
                    new TargetCardsOnTableEffect(action, playerId, "Choose two [Coruscant] Political Effects", 2, 2, coruscantPoliticalEffects) {
                        @Override
                        protected void cardsTargeted(final int targetGroupId, Collection<PhysicalCard> targetedCards) {
                            action.addAnimationGroup(targetedCards);
                            action.allowResponses("Target " + GameUtils.getAppendedNames(targetedCards),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            List<PhysicalCard> finalTargets = new ArrayList<PhysicalCard>(targetingAction.getPrimaryTargetCards(targetGroupId));
                                            if (finalTargets.size() > 0) {
                                                action.appendEffect(
                                                        new SetTargetedCardEffect(action, self, TargetId.EFFECT_TARGET_1, targetGroupId, finalTargets.get(0), Filters.samePermanentCardId(finalTargets.get(0))));
                                            }
                                            if (finalTargets.size() > 1) {
                                                action.appendEffect(
                                                        new SetTargetedCardEffect(action, self, TargetId.EFFECT_TARGET_2, targetGroupId, finalTargets.get(1), Filters.samePermanentCardId(finalTargets.get(1))));
                                            }
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
