package com.gempukku.swccgo.cards.set1115.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PilotingCondition;
import com.gempukku.swccgo.cards.conditions.WithCondition;
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
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.OrCondition;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.DrawsBattleDestinyIfUnableToOtherwiseModifier;
import com.gempukku.swccgo.logic.modifiers.ManeuverModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 15: Galactic Frontier
 * Type: Character
 * Subtype: Rebel
 * Title: Wedge Antilles
 */
public class Card1115_034 extends AbstractRebel {
    public Card1115_034() {
        super(Side.LIGHT, 4, 2, 2, 2, 6, "Wedge Antilles", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_15, Rarity.V);
        setVirtualSuffix(true);
        setLore("Highly decorated Corellian. Piloted Red 2 at the Battle of Yavin. A wealthy orphan, he bought a freighter with his inheritance. First joined the Alliance as a weapons smuggler.");
        setGameText("Adds 3 to power of anything he pilots. While piloting Pulsar Skate, it is maneuver +2 and Wedge draws one battle destiny if unable to otherwise. While at Tatooine (or with Booster, Luke, or Mirax), Set For Stun may not target characters here and Overwhelmed may not be played here.");
        addPersona(Persona.WEDGE);
        addIcons(Icon.PILOT, Icon.WARRIOR, Icon.A_NEW_HOPE);
        addKeywords(Keyword.SMUGGLER);
        setSpecies(Species.CORELLIAN);
        setMatchingStarshipFilter(Filters.Pulsar_Skate);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition pilotingPulsarSkate = new PilotingCondition(self, Filters.Pulsar_Skate);
        Condition atTatooineOrWithNamed = new OrCondition(
                new AtCondition(self, Filters.or(Filters.Tatooine_system, Filters.Tatooine_site)),
                new WithCondition(self, Filters.or(Filters.Booster, Filters.Luke, Filters.Mirax)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 3));
        modifiers.add(new ManeuverModifier(self, Filters.and(Filters.Pulsar_Skate, Filters.hasPiloting(self)), pilotingPulsarSkate, 2));
        modifiers.add(new DrawsBattleDestinyIfUnableToOtherwiseModifier(self, pilotingPulsarSkate, 1));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.and(Filters.character, Filters.here(self)), atTatooineOrWithNamed, Filters.Set_For_Stun));
        modifiers.add(new MayNotBeTargetedByModifier(self, Filters.sameLocation(self), atTatooineOrWithNamed, Filters.Overwhelmed));
        return modifiers;
    }
}
