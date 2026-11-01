package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.choose.ChooseAndLoseCardFromHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.ResetDestinyEffect;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.modifiers.AddsBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextPowerModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Character
 * Subtype: Alien
 * Title: Brainiac
 */
public class Card1115_008 extends AbstractAlien {
    public Card1115_008() {
        super(Side.LIGHT, Math.PI, 3f, null, (float) Math.PI, 4f, "Brainiac", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("BoShek nicknamed this male Siniteen 'Brainiac' due to his ability to calculate hyperspace coordinates in his head. 'He's the brains, sweetheart!'");
        setGameText("X = Light Side cards in hand. Y = Dark Side cards in hand. If X < Y, your Force generation is +1. If X = Y, adds one battle destiny. Once per turn, if X > Y, may lose a card from hand to make a just drawn weapon or battle destiny = π.");
        addIcons(Icon.A_NEW_HOPE);
        setSpecies(Species.SINITEEN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextPowerModifier(self,
                new BaseEvaluator() {
                    @Override
                    public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                        int x = gameState.getHand(gameState.getLightPlayer()).size();
                        int y = gameState.getHand(gameState.getDarkPlayer()).size();
                        return x - y;
                    }
                }));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition xLessThanY = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return gameState.getHand(gameState.getLightPlayer()).size() < gameState.getHand(gameState.getDarkPlayer()).size();
            }
        };
        Condition xEqualsY = new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                return gameState.getHand(gameState.getLightPlayer()).size() == gameState.getHand(gameState.getDarkPlayer()).size();
            }
        };
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalForceGenerationModifier(self, xLessThanY, 1, playerId));
        modifiers.add(new AddsBattleDestinyModifier(self, xEqualsY, 1));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__BRAINIAC_V__RESET_DESTINY;
        int x = GameConditions.numCardsInHand(game, game.getLightPlayer());
        int y = GameConditions.numCardsInHand(game, game.getDarkPlayer());
        if ((TriggerConditions.isBattleDestinyJustDrawn(game, effectResult) || TriggerConditions.isWeaponDestinyJustDrawn(game, effectResult))
                && x > y
                && GameConditions.hasHand(game, playerId)
                && GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reset destiny to π");
            action.appendUsage(
                    new OncePerTurnEffect(action));
            action.appendEffect(
                    new ChooseAndLoseCardFromHandEffect(action, playerId, Filters.any));
            action.appendEffect(
                    new ResetDestinyEffect(action, (float) Math.PI));
            return Collections.singletonList(action);
        }
        return null;
    }
}
