package com.gempukku.swccgo.cards.set1103.light;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.conditions.ArmedWithCondition;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.evaluators.PowerEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.ForceRetrievalImmuneToSecretPlansModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;


/**
 * Set: Virtual Set 3: Revolution
 * Type: Character
 * Subtype: Alien
 * Title: Han (V)
 */
public class Card1103_011 extends AbstractAlien {
    public Card1103_011() {
        super(Side.LIGHT, 1, 3, 4, 3, 6, "Han", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_3, Rarity.V);
        setVirtualSuffix(true);
        setLore("Corellian. Graduated with honors from the Imperial Academy. Dishonorably discharged. Wanders the galaxy building a reputation as a gambler and a hot-shot pilot.");
        setGameText("[Pilot] 3. 4: Falcon. While armed with a blaster, Han's defense value and weapon destiny draws are +2. Force retrieval with Or Be Destroyed is immune to Secret Plans. While Or Be Destroyed on table, immune to attrition < Han's power.");
        addPersona(Persona.HAN);
        addIcons(Icon.PREMIUM, Icon.PILOT, Icon.WARRIOR);
        addKeywords(Keyword.GAMBLER);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Falcon);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ForceRetrievalImmuneToSecretPlansModifier(self, Filters.Or_Be_Destroyed));
        modifiers.add(new DefenseValueModifier(self, new ArmedWithCondition(self, Filters.blaster), 2));
        modifiers.add(new EachWeaponDestinyModifier(self, Filters.blaster, new ArmedWithCondition(self, Filters.blaster), self, 2));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new OnTableCondition(self, Filters.Or_Be_Destroyed), new PowerEvaluator(self)));
        return modifiers;
    }
}
