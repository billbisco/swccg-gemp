package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.conditions.HitCondition;
import com.gempukku.swccgo.cards.conditions.StackedOnCondition;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.*;
import java.util.List;

/** Legacy Block 5 Chewbacca Of Kashyyyk, final printed36E. */
public class Card1250_037 extends AbstractRebel {
    public Card1250_037() {
        super(Side.LIGHT, 1, 6, 6, 2, 7, "Chewbacca Of Kashyyyk", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setArmor(4); setSpecies(Species.WOOKIEE);
        setLore("Wookiee scout. Volunteered for Han's Endor strike team. Keeps his distance, but doesn't look like he's keeping his distance. Always thinks with his stomach.");
        setGameText("Adds 2 to power of anything he pilots. Deploys -2 to Kashyyyk. Wookiees are destiny +1 when drawn for weapon or battle destiny. While stacked on Grrrghrrrgh!, Wookiees are power and forfeit +1. While hit, Chewie's power is doubled.");
        addIcons(Icon.ENDOR, Icon.PILOT, Icon.WARRIOR, Icon.EPISODE_I);
        addKeywords(Keyword.SCOUT); addPersona(Persona.CHEWIE);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new DeployCostToLocationModifier(self, -2, Filters.Kashyyyk_location));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 2),
                new DestinyWhenDrawnForWeaponDestinyModifier(self, Filters.Wookiee, 1),
                new DestinyWhenDrawnForBattleDestinyModifier(self, Filters.Wookiee, 1),
                new PowerMultiplierModifier(self, self, new HitCondition(self), 2));
    }

    @Override
    protected List<Modifier> getGameTextWhileStackedModifiers(SwccgGame game, PhysicalCard self) {
        StackedOnCondition stacked = new StackedOnCondition(self, Filters.title("Grrrghrrrgh!"));
        return List.of(new PowerModifier(self, Filters.Wookiee, stacked, 1),
                new ForfeitModifier(self, Filters.Wookiee, stacked, 1));
    }
}
