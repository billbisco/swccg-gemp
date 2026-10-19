package com.gempukku.swccgo.cards.set1291.light;

import com.gempukku.swccgo.cards.AbstractMission;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HasStackedCondition;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceAndStackFaceDownEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.UnrespondableEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Mission
 * Title: Assault On Muunilinst
 */
public class Card1291_065 extends AbstractMission {
    public Card1291_065() {
        super(Side.LIGHT, 5, PlayCardZoneOption.ATTACHED, "Assault On Muunilinst", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V, false);
        setGameText("Deploy on Begun, The Clone War Has. Unless a card stacked here, if you control three Muunilinst locations (and opponent controls no Muunilinst locations), opponent loses 1 Force (cannot be reduced) and stacks lost card here face down (Mission complete).\n{Reward}: Your Force drains at Muunilinst locations are +1 and, once per turn, may raise your converted Muunilinst site to the top.\n{Turn The Tide}: If opponent controls two Muunilinst locations, place this Mission in Used Pile.");
        addIcons(Icon.EPISODE_I, Icon.CLONE_ARMY);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.title("Begun, The Clone War Has");
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return Filters.title("Begun, The Clone War Has");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, Filters.partOfSystem("Muunilinst"), new HasStackedCondition(self, Filters.any), 1, self.getOwner()));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter muunilinst = Filters.partOfSystem("Muunilinst");
        if (!TriggerConditions.isTableChanged(game, effectResult)) {
            return null;
        }
        if (!GameConditions.hasStackedCards(game, self)
                && GameConditions.controls(game, playerId, 3, muunilinst)
                && !GameConditions.controls(game, opponent, muunilinst)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Complete Mission");
            action.appendEffect(new LoseForceAndStackFaceDownEffect(action, opponent, 1, self) {
                @Override
                public boolean isShownIfLostFromHand() {
                    return true;
                }
            });
            return Collections.singletonList(action);
        }
        if (GameConditions.controls(game, opponent, 2, muunilinst)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setSingletonTrigger(true);
            action.setText("Place in Used Pile");
            action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter converted = Filters.and(Filters.partOfSystem("Muunilinst"), Filters.site, Filters.canBeConvertedByRaisingYourLocationToTop(playerId));
        if (GameConditions.hasStackedCards(game, self)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canTarget(game, self, converted)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Raise converted Muunilinst site");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose converted Muunilinst site", converted) {
                        @Override
                        protected void cardTargeted(int targetGroupId, final PhysicalCard targetedCard) {
                            action.allowResponses("Convert " + GameUtils.getCardLink(targetedCard),
                                    new UnrespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            action.appendEffect(new ConvertLocationByRaisingToTopEffect(action, targetedCard, true));
                                        }
                                    });
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
