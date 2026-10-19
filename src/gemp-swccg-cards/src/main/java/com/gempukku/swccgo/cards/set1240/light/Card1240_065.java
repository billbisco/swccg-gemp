package com.gempukku.swccgo.cards.set1240.light;

import com.gempukku.swccgo.cards.AbstractAdmiralsOrder;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.cards.evaluators.AddEvaluator;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotHavePowerReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Admiral's Order
 * Title: No Questions Asked
 */
public class Card1240_065 extends AbstractAdmiralsOrder {
    public Card1240_065() {
        super(Side.LIGHT, Title.No_Questions_Asked, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Players generate +1 Force at systems they occupy. Starships (except Outrider) piloted by Corellians are power +1 (+2 if pilot also a smuggler or gambler, +3 if both), may not have their power modified by opponent, and are immune to attrition < 4. While you occupy Corellia system, your Force drains are +1 at related battleground sites.");
        addIcons(Icon.REFLECTIONS_II);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        Filter systemsOccupiedByPlayer = Filters.and(Filters.system, Filters.occupies(playerId));
        Filter systemsOccupiedByOpponent = Filters.and(Filters.system, Filters.occupies(opponent));
        Filter corellianPiloted = Filters.and(Filters.starship, Filters.except(Filters.Outrider), Filters.hasPiloting(self, Filters.Corellian));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceGenerationModifier(self, systemsOccupiedByPlayer, 1, playerId));
        modifiers.add(new ForceGenerationModifier(self, systemsOccupiedByOpponent, 1, opponent));
        modifiers.add(new PowerModifier(self, corellianPiloted,
                new AddEvaluator(1,
                        new CardMatchesEvaluator(0, 1, Filters.hasPiloting(self, Filters.and(Filters.Corellian, Filters.smuggler))),
                        new CardMatchesEvaluator(0, 1, Filters.hasPiloting(self, Filters.and(Filters.Corellian, Filters.gambler))))));
        modifiers.add(new MayNotHavePowerReducedModifier(self, corellianPiloted, opponent));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, corellianPiloted, 4));
        modifiers.add(new ForceDrainModifier(self, Filters.and(Filters.battleground_site, Filters.relatedSiteTo(self, Filters.Corellia_system)),
                new OccupiesCondition(playerId, Filters.Corellia_system), 1, playerId));
        return modifiers;
    }
}
