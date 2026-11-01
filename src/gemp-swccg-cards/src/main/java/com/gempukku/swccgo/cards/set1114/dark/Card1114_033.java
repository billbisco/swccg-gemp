package com.gempukku.swccgo.cards.set1114.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.DeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 14: Armed & Operational
 * Type: Effect
 * Title: Where Are Those Droidekas?!
 */
public class Card1114_033 extends AbstractNormalEffect {
    public Card1114_033() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Where Are Those Droidekas?!", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_14, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Deploy on table. Scum And Villainy is canceled. Non-unique destroyer droids are deploy -1 and forfeit +1. While all your characters at sites are destroyer droids, opponent's characters are defense value -1 for each destroyer droid present. Immune to Alter.");
        addIcons(Icon.REFLECTIONS_III);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Evaluator perDestroyer = new Evaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                return -Filters.countActive(gameState.getGame(), self, Filters.and(Filters.destroyer_droid, Filters.presentAt(Filters.site)));
            }
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
                return evaluateExpression(gameState, modifiersQuerying, cardAffected);
            }
        };
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new CancelsGameTextModifier(self, Filters.Scum_And_Villainy));
        modifiers.add(new DeployCostModifier(self, Filters.and(Filters.destroyer_droid, Filters.non_unique), -1));
        modifiers.add(new ForfeitModifier(self, Filters.and(Filters.destroyer_droid, Filters.non_unique), 1));
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.opponents(self), Filters.character),
                new CantSpotCondition(self, Filters.and(Filters.your(self), Filters.character, Filters.at(Filters.site), Filters.not(Filters.destroyer_droid))),
                perDestroyer));
        return modifiers;
    }
}
