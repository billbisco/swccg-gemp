package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.*;

import java.util.List;

/** Vader's Cape (V), Virtual Block 7.1. */
public class Card1271_014 extends LegacyVaderEquipment {
    public Card1271_014() {
        super(3, "Vader's Cape");
        setVirtualSuffix(true);
        setLore("A symbol of the Dark Lord of the Sith, and of the seductive power of the dark side.");
        setGameText("Deploy on [Vader] Vader. Vader moves (using landspeed) for free. While with a Jedi or Skywalker, Vader is defense value and immunity to attrition +2, and his weapon destiny draws are +1. During a duel (or lightsaber combat) opponent initiates, Vader's destiny draws are +1. (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter wearer = Filters.hasAttached(self);
        Condition withJediOrSkywalker = (state, modifiers) -> {
            PhysicalCard vader = self.getAttachedTo();
            return vader != null && GameConditions.canSpot(game, self,
                Filters.and(Filters.or(Filters.Jedi, Filters.Skywalker), Filters.presentWith(vader), Filters.except(vader)));
        };
        String opponent = game.getOpponent(self.getOwner());
        Condition duel = (state, modifiers) -> GameConditions.isDuringDuelInitiatedBy(game, opponent)
            && GameConditions.isDuringDuelWithParticipant(game, wearer);
        Condition combat = (state, modifiers) -> state.getLightsaberCombatState() != null
            && opponent.equals(state.getLightsaberCombatState().getPlayerInitiatedLightsaberCombat())
            && GameConditions.isDuringLightsaberCombatWithParticipant(game, wearer);
        return List.of(
            new MovesForFreeUsingLandspeedModifier(self, wearer),
            new DefenseValueModifier(self, wearer, withJediOrSkywalker, 2),
            new ImmunityToAttritionChangeModifier(self, wearer, withJediOrSkywalker, 2),
            new EachWeaponDestinyModifier(self, Filters.any, withJediOrSkywalker, wearer, 1),
            new EachDuelDestinyModifier(self, duel, 1, self.getOwner()),
            new EachLightsaberCombatDestinyModifier(self, combat, 1, self.getOwner()));
    }
}
