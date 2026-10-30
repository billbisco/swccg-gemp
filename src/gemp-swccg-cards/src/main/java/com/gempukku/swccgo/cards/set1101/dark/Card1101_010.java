package com.gempukku.swccgo.cards.set1101.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.NumTimesPerBattleEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 1: Legacy
 * Type: Character
 * Subtype: Imperial
 * Title: Darth Vader
 */
public class Card1101_010 extends AbstractImperial {
    public Card1101_010() {
        super(Side.DARK, 1, 6, 6, 6, 8, "Darth Vader", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Dark Lord of the Sith. Servant of Emperor. Encased in armor with cybernetic life support. Student of Obi-Wan Kenobi. Was the best starpilot in the galaxy. Cunning warrior.");
        setGameText("Adds 4 to power of anything he pilots. While aboard a starship, it is immune to attrition < 5. During battle at same system (twice if with a Black Squadron pilot), may cumulatively subtract 3 from a just drawn destiny. Immune to attrition < 5.");
        addPersona(Persona.VADER);
        addIcons(Icon.PILOT, Icon.WARRIOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 4));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, Filters.and(Filters.starship, Filters.hasAboard(self)), 5));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 5));
        return modifiers;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(final String playerId, SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isDestinyJustDrawn(game, effectResult)
                && GameConditions.isDuringBattleAt(game, Filters.sameSystem(self))) {
            int numTimes = GameConditions.isWith(game, self, Filters.Black_Squadron_pilot) ? 2 : 1;
            if (GameConditions.isNumTimesPerBattle(game, self, playerId, numTimes, gameTextSourceCardId)) {

                final OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
                action.setRepeatableTrigger(true);
                action.setText("Subtract 3 from destiny");
                action.appendUsage(
                        new NumTimesPerBattleEffect(action, numTimes));
                action.appendEffect(
                        new ModifyDestinyEffect(action, -3, true));
                return Collections.singletonList(action);
            }
        }
        return null;
    }
}
