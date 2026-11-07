package com.gempukku.swccgo.cards.set1262.light;

import com.gempukku.swccgo.cards.AbstractCreatureVehicle;
import com.gempukku.swccgo.cards.conditions.HasAboardCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/** Kaadu (V), Virtual Block 6.2. */
public class Card1262_026 extends AbstractCreatureVehicle {
    public Card1262_026() {
        super(Side.LIGHT, 4, 1, 1, null, 2, 4, 2, "Kaadu", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Used by many Gungan warriors due to their fearless nature. Kaadu lay large numbers of eggs to compensate for those eaten by predators.");
        setGameText("May add 1 'rider' (passenger). Ability = 1/2. While ridden by a Beast-Rider, power and maneuver +2, cannot be attacked, your Force drains here are +1, and Kaadu and 'rider' are immune to attrition < 4.");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I); addKeywords(Keyword.KAADU); setPassengerCapacity(1);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new DefinedByGameTextAbilityModifier(self, 0.5));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter beastRider = Filters.and(Filters.character, Filters.title("Beast-Rider"), Filters.aboardAsPassenger(self));
        Condition ridden = new HasAboardCondition(self, beastRider);
        Filter riddenKaadu = Filters.and(self, Filters.hasAboard(self, beastRider));
        Filter kaaduAndRider = Filters.or(self, Filters.and(Filters.character, Filters.aboardAsPassenger(self)));
        return Arrays.asList(new PowerModifier(self, ridden, 2), new ManeuverModifier(self, ridden, 2),
                new MayNotBeAttackedModifier(self, riddenKaadu),
                new ForceDrainModifier(self, Filters.here(self), ridden, 1, self.getOwner()),
                new ImmuneToAttritionLessThanModifier(self, kaaduAndRider, ridden, 4));
    }
}
