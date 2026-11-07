package com.gempukku.swccgo.cards.set1261.dark;

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
import com.gempukku.swccgo.logic.modifiers.ResetForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.SuspendModifierEffectsModifier;

import java.util.Arrays;
import java.util.List;

/** Virtual Block 6.1: the Subjugated-planet clauses reuse Reflections III Leave Them To Me's modifier pattern. */
public class Card1261_022 extends AbstractDefensiveShield {
    public Card1261_022() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.No_Escape,
                ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jabba's influence is not easily ignored. Neither are his voracious and vile appetites. Even Jedi soon learn this lesson.");
        setGameText("Plays on table. While you occupy a Subjugated Planet location, operatives are forfeit = 0, operatives do not add to Force drains, and your Force drains may not be reduced. Nudjs do not cancel Force icons.");
        addIcons(Icon.REFLECTIONS_III);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Condition occupySubjugatedPlanet = new OccupiesCondition(playerId, Filters.Subjugated_planet_location);
        Filter operatives = Filters.and(Filters.operative, Filters.canBeTargetedBy(self));
        return Arrays.asList(
                new ResetForfeitModifier(self, operatives, occupySubjugatedPlanet, 0),
                new CancelForceDrainBonusesFromCardModifier(self, operatives, occupySubjugatedPlanet),
                new ForceDrainsMayNotBeReducedModifier(self, occupySubjugatedPlanet, playerId),
                // Nudj's icon cancellation affects locations; its ferocity modifier affects itself and remains active.
                new SuspendModifierEffectsModifier(self, Filters.location, Filters.title("Nudj")));
    }
}
