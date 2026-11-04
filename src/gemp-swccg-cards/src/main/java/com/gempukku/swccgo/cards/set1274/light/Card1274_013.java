package com.gempukku.swccgo.cards.set1274.light;

import com.gempukku.swccgo.cards.AbstractAdmiralsOrder;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Filterable;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.evaluators.BaseEvaluator;
import com.gempukku.swccgo.logic.evaluators.Evaluator;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.EachBattleDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ImmunityToAttritionChangeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.4
 * Type: Admiral's Order
 * Title: Combined Fleet Action (V)
 */
public class Card1274_013 extends AbstractAdmiralsOrder {
    public Card1274_013() {
        super(Side.LIGHT, Title.Combined_Fleet_Action, ExpansionSet.VIRTUAL_BLOCK_7_4, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Unique (•) starfighters are immunity to attrition +2 for each non-unique starship of their class present with them. Unique (•) capital starships are defense value +1 for each non-unique starship present at same system. For each of your unique (•)/non-unique starship pairs of the same class present at same system, your battle destiny draws there (and at related sites) are +1.");
        addIcons(Icon.DEATH_STAR_II);
    }

    /**
     * Filter that accepts cards that share a model type ('class') with the specified card.
     */
    private static Filter sameClassAs(PhysicalCard card) {
        List<ModelType> modelTypes = card.getBlueprint().getModelTypes();
        if (modelTypes == null || modelTypes.isEmpty()) {
            return Filters.none;
        }
        List<Filterable> classFilters = new ArrayList<Filterable>();
        for (ModelType modelType : modelTypes) {
            classFilters.add(modelType);
        }
        return Filters.or(classFilters.toArray(new Filterable[0]));
    }

    /**
     * Counts the unique/non-unique starship pairs of the same class the player has present at the system.
     */
    private static int countPairs(SwccgGame game, String playerId, PhysicalCard system) {
        Filter presentAtSystem = Filters.and(Filters.owner(playerId), Filters.starship, Filters.presentAt(Filters.sameCardId(system)));
        Collection<PhysicalCard> uniqueStarships = Filters.filterActive(game, null, Filters.and(presentAtSystem, Filters.unique));
        List<PhysicalCard> nonUniqueStarships = new ArrayList<PhysicalCard>(Filters.filterActive(game, null, Filters.and(presentAtSystem, Filters.non_unique)));
        int pairs = 0;
        for (PhysicalCard uniqueStarship : uniqueStarships) {
            Filter sameClass = sameClassAs(uniqueStarship);
            for (PhysicalCard nonUniqueStarship : nonUniqueStarships) {
                if (sameClass.accepts(game, nonUniqueStarship)) {
                    nonUniqueStarships.remove(nonUniqueStarship);
                    pairs++;
                    break;
                }
            }
        }
        return pairs;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        final String playerId = self.getOwner();
        final String opponent = game.getOpponent(playerId);

        Evaluator nonUniqueOfClassPresentWith = new BaseEvaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard starfighter) {
                return 2 * Filters.filterActive(gameState.getGame(), null, Filters.and(Filters.starship, Filters.non_unique,
                        Filters.presentWith(starfighter), sameClassAs(starfighter))).size();
            }
        };
        Evaluator nonUniquePresentAtSameSystem = new BaseEvaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard capitalStarship) {
                PhysicalCard system = modifiersQuerying.getLocationThatCardIsAt(gameState, capitalStarship);
                if (system == null) {
                    return 0;
                }
                return Filters.filterActive(gameState.getGame(), null, Filters.and(Filters.starship, Filters.non_unique,
                        Filters.presentAt(Filters.sameCardId(system)))).size();
            }
        };

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmunityToAttritionChangeModifier(self, Filters.and(Filters.unique, Filters.starfighter), nonUniqueOfClassPresentWith));
        modifiers.add(new DefenseValueModifier(self, Filters.and(Filters.unique, Filters.capital_starship, Filters.at(Filters.system)), nonUniquePresentAtSameSystem));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.any, pairsEvaluator(playerId), playerId));
        modifiers.add(new EachBattleDestinyModifier(self, Filters.any, pairsEvaluator(opponent), opponent));
        return modifiers;
    }

    /**
     * Evaluates, for the battle location, the number of the player's unique/non-unique starship pairs of the same class
     * present at that system (or at the system a site is related to).
     */
    private static Evaluator pairsEvaluator(final String playerId) {
        return new BaseEvaluator() {
            @Override
            public float evaluateExpression(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard location) {
                if (location == null) {
                    return 0;
                }
                SwccgGame game = gameState.getGame();
                Collection<PhysicalCard> systems = Filters.filterTopLocationsOnTable(game,
                        Filters.and(Filters.system, Filters.or(Filters.sameCardId(location), Filters.relatedLocation(location))));
                int pairs = 0;
                for (PhysicalCard system : systems) {
                    pairs += countPairs(game, playerId, system);
                }
                return pairs;
            }
        };
    }
}
