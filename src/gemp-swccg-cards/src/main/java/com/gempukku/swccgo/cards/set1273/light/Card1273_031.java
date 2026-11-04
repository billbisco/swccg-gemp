package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.LegacyVb73Light;
import com.gempukku.swccgo.cards.AbstractLocationDevice;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
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
import com.gempukku.swccgo.logic.effects.DrawDestinyEffect;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardOutOfPlayFromTableEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.GuiUtils;
import com.gempukku.swccgo.logic.timing.PassthruEffect;
import com.gempukku.swccgo.logic.timing.results.AboutToLoseCardFromTableResult;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Device
 * Title: Planetary Shield
 */
public class Card1273_031 extends AbstractLocationDevice {
    public static final String TITLE = "Planetary Shield";

    public Card1273_031() {
        super(Side.LIGHT, 4, TITLE, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setLore("Key installations are protected from bombardment by a complex network of early warning sensors, emergency shields and fast response fighters.");
        setGameText("If Center Of Tyranny on table, deploy on Coruscant. Once during your control phase, if you occupy Main Power Plant, may draw destiny. If destiny > 5, Planetary Shield 'disabled' (place this device out of play; opponent loses 3 Force). May not be lost.");
    }

    /**
     * (Virtual Block 7) Planetary Shield.
     */
    public static Filter planetaryShield() {
        return Filters.and(Filters.title(TITLE), Icon.LEGACY_BLOCK_7);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.canSpot(game, self, LegacyVb73Light.centerOfTyranny());
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.Coruscant_system;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        // Check condition(s)
        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, Phase.CONTROL)
                && GameConditions.occupies(game, playerId, Filters.title(Card1273_040.TITLE))
                && GameConditions.canDrawDestiny(game, playerId)) {
            final String opponent = game.getOpponent(playerId);

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId);
            action.setText("Attempt to 'disable' Planetary Shield");
            // Update usage limit(s)
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            // Perform result(s)
            action.appendEffect(
                    new DrawDestinyEffect(action, playerId) {
                        @Override
                        protected void destinyDraws(SwccgGame game, List<PhysicalCard> destinyCardDraws, List<Float> destinyDrawValues, Float totalDestiny) {
                            if (totalDestiny == null) {
                                game.getGameState().sendMessage("Result: Failed due to failed destiny draw");
                                return;
                            }
                            game.getGameState().sendMessage("Destiny: " + GuiUtils.formatAsString(totalDestiny));
                            if (totalDestiny > 5) {
                                game.getGameState().sendMessage("Result: " + GameUtils.getCardLink(self) + " 'disabled'");
                                action.appendEffect(
                                        new PlaceCardOutOfPlayFromTableEffect(action, self));
                                action.appendEffect(
                                        new LoseForceEffect(action, opponent, 3));
                            }
                            else {
                                game.getGameState().sendMessage("Result: Failed");
                            }
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        // May not be lost
        if (TriggerConditions.isAboutToBeLost(game, effectResult, self)) {
            final AboutToLoseCardFromTableResult result = (AboutToLoseCardFromTableResult) effectResult;

            final RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Prevent " + GameUtils.getFullName(self) + " from being lost");
            action.setActionMsg(GameUtils.getCardLink(self) + " may not be lost");
            // Perform result(s)
            action.appendEffect(
                    new PassthruEffect(action) {
                        @Override
                        protected void doPlayEffect(SwccgGame game) {
                            result.getPreventableCardEffect().preventEffectOnCard(self);
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }
}
