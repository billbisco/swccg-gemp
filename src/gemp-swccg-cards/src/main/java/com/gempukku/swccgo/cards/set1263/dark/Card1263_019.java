package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBeLostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 6.3
 * Type: Starship
 * Subtype: Starfighter
 * Title: TIE Avenger (V)
 */
public class Card1263_019 extends AbstractStarfighter {
    private static final String TIE_FIGHTER_CONSTRUCTION_FACILITY = "TIE Fighter Construction Facility";

    public Card1263_019() {
        super(Side.DARK, 3, 2, 3, null, 4, 3, 4, "TIE Avenger", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Design based on Lord Vader's prototype Advanced x1. Manufactured until replaced by the more economical TIE Interceptor. Equipped with deflector shields and a hyperdrive.");
        setGameText("May add 1 pilot and 1 astromech. While TIE Fighter Construction Facility on table, pilot is forfeit +2 and this starship may not be lost before the damage segment of battle. May deploy an astromech aboard from Reserve Deck; reshuffle.");
        addIcons(Icon.DAGOBAH, Icon.NAV_COMPUTER, Icon.SCOMP_LINK);
        addModelType(ModelType.TIE_AD);
        setPilotCapacity(1);
        setAstromechCapacity(1);
    }

    private boolean protectedBeforeDamage(SwccgGame game, PhysicalCard self) {
        return GameConditions.isDuringBattle(game) && !GameConditions.isDamageSegmentOfBattle(game)
                && GameConditions.canSpot(game, self, Filters.title(TIE_FIGHTER_CONSTRUCTION_FACILITY));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition protection = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return protectedBeforeDamage(gameState.getGame(), self);
            }
        };
        return Arrays.asList(new ForfeitModifier(self, Filters.piloting(self),
                        new OnTableCondition(self, Filters.title(TIE_FIGHTER_CONSTRUCTION_FACILITY)), 2),
                new MayNotTargetToBeLostModifier(self, self, protection));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult,
            final PhysicalCard self, int gameTextSourceCardId) {
        if (protectedBeforeDamage(game, self) && TriggerConditions.isAboutToBeLost(game, effectResult, self)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;
            if (result.getPreventableCardEffect() != null) {
                RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
                action.setText("Prevent loss before damage segment");
                action.appendEffect(new PassthruEffect(action) {
                    @Override
                    protected void doPlayEffect(SwccgGame game) {
                        result.getPreventableCardEffect().preventEffectOnCard(self);
                    }
                });
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__TIE_AVENGER_V__DEPLOY_ASTROMECH_ABOARD;
        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            Filter astromech = Filters.astromech_droid;
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy astromech aboard from Reserve Deck");
            action.setActionMsg("Deploy an astromech aboard " + com.gempukku.swccgo.logic.GameUtils.getCardLink(self) + " from Reserve Deck");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployCardToTargetFromReserveDeckEffect(action, astromech, Filters.sameCardId(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
