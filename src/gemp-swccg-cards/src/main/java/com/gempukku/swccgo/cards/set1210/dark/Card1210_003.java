package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.cards.conditions.WithCondition;
import com.gempukku.swccgo.cards.evaluators.OnTableEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;
import com.gempukku.swccgo.logic.modifiers.ResetHyperspeedModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Character
 * Subtype: Imperial
 * Title: Admiral Motti, Battlestation Coordinator
 */
public class Card1210_003 extends AbstractImperial {
    public Card1210_003() {
        super(Side.DARK, 1, 3, 3, 3, 5, "Admiral Motti, Battlestation Coordinator", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Considered an overrated leader by many subordinates. Has a disturbing lack of faith. Became a member of the Death Star's command triumvirate despite his failings.");
        setGameText("Adds 2 to power of anything he pilots. Death Star is hyperspeed = 2, and your total power there (and at system it orbits) is +1 for each Death Star site on table. While with Tarkin, Tarkin Doctrine ignores parsec limit and is [Immune to Alter]. Immune to attrition < 3.");
        addPersona(Persona.MOTTI);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.PREMIUM);
        addKeywords(Keyword.LEADER, Keyword.ADMIRAL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Condition withTarkin = new WithCondition(self, Filters.Tarkin);
        Filter deathStarAndOrbited = Filters.or(Filters.Death_Star_system, Filters.isOrbitedBy(Filters.Death_Star_system));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ResetHyperspeedModifier(self, Filters.Death_Star_system, 2));
        modifiers.add(new TotalPowerModifier(self, deathStarAndOrbited, new OnTableEvaluator(self, Filters.Death_Star_site), playerId));
        modifiers.add(new ModifyGameTextModifier(self, Filters.Tarkin_Doctrine, withTarkin, ModifyGameTextType.LEGACY__TARKIN_DOCTRINE__IGNORES_PARSEC_LIMIT));
        modifiers.add(new ImmuneToTitleModifier(self, Filters.Tarkin_Doctrine, withTarkin, Title.Alter));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, 3));
        return modifiers;
    }
}
