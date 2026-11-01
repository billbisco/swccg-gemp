package com.gempukku.swccgo.cards.set1115.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.BattleState;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.effects.AddUntilEndOfBattleModifierEffect;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Character
 * Subtype: Alien
 * Title: Chokk (V)
 */
public class Card1115_040 extends AbstractAlien {
    public Card1115_040() {
        super(Side.DARK, 3, 3, 4, 1, 3, "Chokk", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setArmor(4);
        setLore("Klatooinian bodyguard currently in the paid service of Jabba the Hutt. Chokk takes pride in his work, and he is very good at it.");
        setGameText("Deploys -1 to Tatooine. While with your non-droid character, if a battle was just initiated here, may use 1 Force to place Chokk in Used Pile; Interrupts may not be played until the weapons segment ends.");
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.WARRIOR);
        addKeyword(Keyword.BODYGUARD);
        setSpecies(Species.KLATOOINIAN);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, -1, Filters.Tatooine_location));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.battleInitiatedAt(game, effectResult, Filters.here(self))
                && GameConditions.isWith(game, self, Filters.and(Filters.your(self), Filters.character, Filters.not(Filters.droid)))
                && GameConditions.canUseForce(game, playerId, 1)) {
            final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.appendCost(new UseForceEffect(action, playerId, 1));
            action.appendEffect(new AddUntilEndOfBattleModifierEffect(action,
                    new MayNotPlayModifier(self, Filters.Interrupt, new UntilWeaponsSegmentEnds()),
                    "Interrupts may not be played until the weapons segment ends"));
            action.appendEffect(new PlaceCardInUsedPileFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    private static class UntilWeaponsSegmentEnds implements Condition {
        @Override
        public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
            BattleState battle = gameState.getBattleState();
            return battle != null && battle.isBattleStarted() && !battle.isCanceled() && !battle.isReachedPowerSegment();
        }
    }
}
