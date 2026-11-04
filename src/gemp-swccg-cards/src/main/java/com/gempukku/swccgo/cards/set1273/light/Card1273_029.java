package com.gempukku.swccgo.cards.set1273.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.evaluators.CardMatchesEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.3
 * Type: Character
 * Subtype: Rebel
 * Title: Tycho Celchu (V)
 */
public class Card1273_029 extends AbstractRebel {
    public Card1273_029() {
        super(Side.LIGHT, 2, 2, 2, 2, 4, "Tycho Celchu", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Spy fighting for the memory of his homeworld of Alderaan. Rogue Squadron pilot. Volunteered to fly an A-wing at the Battle of Endor. Former TIE fighter pilot.");
        setGameText("Adds 3 to the power of anything he pilots. Tycho is a matching pilot for any starfighter. While piloting a starship, draws one battle destiny if unable to otherwise and it is immune to attrition < 4 (< 7 if stolen).");
        addPersona(Persona.TYCHO);
        addIcons(Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.SPY, Keyword.ROGUE_SQUADRON, Keyword.GREEN_SQUADRON);
        setSpecies(Species.ALDERAANIAN);
        setMatchingStarshipFilter(Filters.starfighter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingStarship = new PilotingCondition(self, Filters.starship);
        Filter starshipHePilots = Filters.and(Filters.starship, Filters.hasPiloting(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingStarship, 1));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, starshipHePilots, pilotingStarship,
                new CardMatchesEvaluator(4, 7, Filters.stolen)));
        return modifiers;
    }
}
