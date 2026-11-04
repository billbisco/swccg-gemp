package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.effects.ConvertLocationByRaisingToTopEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.game.AbstractActionProxy;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfGameActionProxyEffect;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.ConvertLocationResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Location
 * Subtype: System
 * Title: Coruscant (V)
 */
public class Card1273_041 extends AbstractSystem {
    public Card1273_041() {
        super(Side.LIGHT, Title.Coruscant, 0, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("If just converted, may raise this system to the top.");
        setLocationDarkSideGameText("If your Coruscant system was converted, Force generation +1 here.");
        setGameText("Light: If just converted, may raise this system to the top. Dark: If your Coruscant system was converted, Force generation +1 here.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.PLANET);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(final String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, final PhysicalCard self, final int gameTextSourceCardId) {
        // A converted location's game text is not active, so when this system reaches the table, register the
        // "if just converted, may raise this system to the top" response for the rest of the game.
        if (TriggerConditions.justDeployed(game, effectResult, self)
                && playerOnLightSideOfLocation.equals(self.getOwner())) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.skipInitialMessageAndAnimation();
            action.setText("Register 'raise this system to the top'");
            action.setActionMsg(null);
            action.appendEffect(
                    new AddUntilEndOfGameActionProxyEffect(action,
                            new AbstractActionProxy() {
                                @Override
                                public List<TriggerAction> getOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult effectResult) {
                                    if (playerId.equals(self.getOwner())
                                            && TriggerConditions.justConvertedLocation(game, effectResult)
                                            && ((ConvertLocationResult) effectResult).getOldLocation() != null
                                            && ((ConvertLocationResult) effectResult).getOldLocation().getCardId() == self.getCardId()
                                            && !game.getModifiersQuerying().isGameTextCanceled(game.getGameState(), self)) {
                                        PhysicalCard newTop = ((ConvertLocationResult) effectResult).getNewLocation();
                                        OptionalGameTextTriggerAction raise = new OptionalGameTextTriggerAction(self, playerId, gameTextSourceCardId);
                                        raise.setText("Raise Coruscant (V) to the top");
                                        raise.setActionMsg("Raise converted Coruscant (V) to the top");
                                        raise.appendEffect(
                                                new ConvertLocationByRaisingToTopEffect(raise, newTop, true));
                                        return Collections.<TriggerAction>singletonList(raise);
                                    }
                                    return null;
                                }
                            }));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(final String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self) {
        Condition yourCoruscantWasConverted = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                for (PhysicalCard converted : gameState.getConvertedLocationsUnderTopLocation(self)) {
                    if (playerOnDarkSideOfLocation.equals(converted.getOwner()))
                        return true;
                }
                return false;
            }
        };

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, self, yourCoruscantWasConverted, 1, playerOnDarkSideOfLocation));
        return modifiers;
    }
}
