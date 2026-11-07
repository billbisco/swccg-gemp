package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.cards.conditions.OccupiesCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.CancelForceDrainBonusesFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendModifierEffectsModifier;

import java.util.Arrays;
import java.util.List;

/** Virtual Block 6.1: Renegade-planet clauses follow the original Reflections III modifier pattern. */
public class Card1261_072 extends AbstractDefensiveShield {
    public Card1261_072() {
        super(Side.LIGHT, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Ounee_Ta,
                ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jabba's decadent behavior makes him susceptible to deception. Leia and Lando exploited this weakness, posing as Jabba's kind of scum.");
        setGameText("Plays on table. While you occupy a Renegade planet location, operatives are forfeit = 0, operatives do not add to Force drains and your Force drains may not be reduced. Sleens do not cancel Force icons. Cannot be canceled.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Condition occupyRenegadePlanet = new OccupiesCondition(playerId, Filters.Renegade_planet_location);
        Filter operatives = Filters.and(Filters.operative, Filters.canBeTargetedBy(self));
        return Arrays.asList(
                new MayNotBeCanceledModifier(self, self),
                new ResetForfeitModifier(self, operatives, occupyRenegadePlanet, 0),
                new CancelForceDrainBonusesFromCardModifier(self, operatives, occupyRenegadePlanet),
                new ForceDrainsMayNotBeReducedModifier(self, occupyRenegadePlanet, playerId),
                // Sleen's icon cancellation affects locations; its ferocity modifier affects itself and remains active.
                new SuspendModifierEffectsModifier(self, Filters.location, Filters.title("Sleen")));
    }
}
