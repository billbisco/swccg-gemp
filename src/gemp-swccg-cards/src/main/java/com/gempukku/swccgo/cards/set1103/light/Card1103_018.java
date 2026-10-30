package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelGameTextUntilEndOfBattleEffect;
import com.gempukku.swccgo.logic.effects.RespondableEffect;
import com.gempukku.swccgo.logic.effects.TargetCardOnTableEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardAboardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 3: Revolution
 * Type: Starship
 * Subtype: Starfighter
 * Title: Red 5
 */
public class Card1103_018 extends AbstractStarfighter {
    public Card1103_018() {
        super(Side.LIGHT, 2, 3, 3, null, 4, 5, 6, "Red 5", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Luke's Incom T-65 X-wing at the Battle of Yavin. Instrumentation similarities between Red 5 and the T-16 skyhopper allowed Luke to play a pivotal role in the conflict.");
        setGameText("May add Luke as pilot and [A New Hope] R2-D2 (may deploy here from Reserve Deck; reshuffle). During battle, may cancel a starfighter's game text here (except related to capacity, ability, and identity of permanent pilot). Immune to attrition < 6.");
        addPersona(Persona.RED_5);
        addIcons(Icon.SCOMP_LINK, Icon.A_NEW_HOPE);
        addModelType(ModelType.X_WING);
        addKeywords(Keyword.RED_SQUADRON);
        setPilotCapacity(1);
        setAstromechCapacity(1);
        setMatchingPilotFilter(Filters.Luke);
    }

    @Override
    protected Filter getGameTextValidPilotFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.Luke;
    }

    @Override
    protected Filter getGameTextValidPassengerFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.and(Filters.R2D2, Icon.A_NEW_HOPE);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 6));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActionsEvenIfUnpiloted(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId deployActionId = GameTextActionId.LEGACY__RED_5_V__DEPLOY_ABOARD;
        Filter deployFilter = Filters.or(Filters.Luke, Filters.and(Filters.R2D2, Icon.A_NEW_HOPE));
        if (GameConditions.isDuringYourPhase(game, self, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, deployActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, deployActionId);
            action.setText("Deploy Luke or [A New Hope] R2-D2 aboard");
            action.appendEffect(
                    new DeployCardAboardFromReserveDeckEffect(action, deployFilter, Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();

        Filter starfighterHere = Filters.and(Filters.starfighter, Filters.here(self));
        if (GameConditions.isInBattle(game, self)
                && GameConditions.isOncePerBattle(game, self, playerId, gameTextSourceCardId)
                && GameConditions.canTarget(game, self, starfighterHere)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Cancel a starfighter's game text");
            action.appendUsage(
                    new OncePerBattleEffect(action));
            action.appendTargeting(
                    new TargetCardOnTableEffect(action, playerId, "Choose starfighter", starfighterHere) {
                        @Override
                        protected void cardTargeted(final int targetGroupId, PhysicalCard targetedCard) {
                            action.addAnimationGroup(targetedCard);
                            action.allowResponses("Cancel " + GameUtils.getCardLink(targetedCard) + "'s game text",
                                    new RespondableEffect(action) {
                                        @Override
                                        protected void performActionResults(Action targetingAction) {
                                            PhysicalCard starfighter = action.getPrimaryTargetCard(targetGroupId);
                                            action.appendEffect(
                                                    new CancelGameTextUntilEndOfBattleEffect(action, starfighter));
                                        }
                                    });
                        }
                    });
            actions.add(action);
        }

        return actions.isEmpty() ? null : actions;
    }
}
