package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.GameTextCanceledCondition;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.CancelsGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierType;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.2
 * Type: Effect
 * Title: Program Trap (V)
 */
public class Card1262_010 extends AbstractNormalEffect {
    public Card1262_010() {
        super(Side.DARK, 4, PlayCardZoneOption.ATTACHED, Title.Program_Trap, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Imperial slicers imbed a secret command in a droid's primary performance banks. A predetermined trigger causes a power overload, destroying the droid and anything nearby.");
        setGameText("Deploy on opponent's capital or [Independent] starship. Starship is power -2, and its gametext (except related to capacity, immunity, and identity and ability of permanent pilots) is canceled.");
        addIcons(Icon.A_NEW_HOPE);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.opponents(self), Filters.or(Filters.capital_starship, Filters.and(Filters.starship, Icon.INDEPENDENT)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter starshipFilter = Filters.hasAttached(self);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, starshipFilter, -2));
        modifiers.add(new CancelsGameTextModifier(self, starshipFilter));

        // Capacity and permanent pilot identity/ability are printed characteristics that game text cancellation keeps.
        // Immunity is kept by re-creating the starship's own immunity modifiers from this Effect while its game text is canceled.
        PhysicalCard starship = self.getAttachedTo();
        if (starship != null) {
            List<Modifier> starshipModifiers = starship.getBlueprint().getWhileInPlayModifiers(game, starship);
            if (starshipModifiers != null) {
                for (final Modifier original : starshipModifiers) {
                    Filter affected = new Filter() {
                        @Override
                        public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
                            return original.affectsCard(gameState, modifiersQuerying, card);
                        }
                    };
                    Condition condition = original.getCondition() != null
                            ? new AndCondition(new GameTextCanceledCondition(starship), original.getCondition())
                            : new GameTextCanceledCondition(starship);
                    if (original.getModifierType() == ModifierType.IMMUNITY_TO_ATTRITION_LESS_THAN) {
                        modifiers.add(new ImmuneToAttritionLessThanModifier(self, affected, condition, new Evaluator() {
                            @Override
                            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                                return original.getImmunityToAttritionLessThanModifier(gameState, modifiersQuerying, cardAffected);
                            }
                            @Override
                            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
                                return evaluateExpression(gameState, modifiersQuerying, cardAffected);
                            }
                        }));
                    } else if (original.getModifierType() == ModifierType.IMMUNITY_TO_ATTRITION_CHANGE) {
                        modifiers.add(new ImmunityToAttritionChangeModifier(self, affected, condition, new Evaluator() {
                            @Override
                            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected) {
                                return original.getImmunityToAttritionChangedModifier(gameState, modifiersQuerying, cardAffected);
                            }
                            @Override
                            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard cardAffected, PhysicalCard otherCard) {
                                return evaluateExpression(gameState, modifiersQuerying, cardAffected);
                            }
                        }));
                    }
                }
            }
        }
        return modifiers;
    }
}
