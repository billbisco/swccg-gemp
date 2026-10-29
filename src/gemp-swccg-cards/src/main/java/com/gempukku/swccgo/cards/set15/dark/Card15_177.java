package com.gempukku.swccgo.cards.set15.dark;

import com.gempukku.swccgo.cards.set8.dark.Card8_121;
import com.gempukku.swccgo.cards.conditions.FiredWeaponsInBattleCondition;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendModifierEffectsModifier;
import com.gempukku.swccgo.logic.modifiers.TotalPowerModifier;
import java.util.List;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Crossfire. */
public class Card15_177 extends Card8_121 {
    public Card15_177() {
        setLore("");
        setCredits(2);
        setGameText("Deploy on table. When you fire two weapons (except lightsabers) in a battle, your total power is +5. Also, S-foils and Maneuvering Flaps are suspended where you have a weapon present or a starship with maneuver > 3 present. (Immune to Alter.)");
        setAlternateDestiny(4);
        setAsHorizontal(true);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) {
                addIcon(icon, 0);
            }
        }
        addIcons(Icon.REFLECTIONS_GOLD);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.REFLECTIONS_GOLD;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNKNOWN;
    }

    @Override
    public Float getDestiny() {
        return 4f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String player = self.getOwner();
        Filter locations = Filters.locationAndCardsAtLocation(Filters.wherePresent(self,
                Filters.and(Filters.your(self), Filters.or(Filters.weapon,
                        Filters.and(Filters.starship, Filters.maneuverMoreThan(3))))));
        return List.of(
                new TotalPowerModifier(self, Filters.battleLocation,
                        new FiredWeaponsInBattleCondition(player, 2, Filters.except(Filters.lightsaber)), 5, player),
                new SuspendModifierEffectsModifier(self, locations, Filters.or(Filters.S_foils, Filters.Maneuvering_Flaps)));
    }
}
