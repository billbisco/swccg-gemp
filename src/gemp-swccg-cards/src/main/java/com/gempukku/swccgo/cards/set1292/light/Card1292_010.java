package com.gempukku.swccgo.cards.set1292.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainBonusesMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.2
 * Type: Character
 * Subtype: Rebel
 * Title: Major Haash'n (V)
 */
public class Card1292_010 extends AbstractRebel {
    public Card1292_010() {
        super(Side.LIGHT, 3, 2, 2, 2, 3, "Major Haash'n", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Mon Calamari master engineer. Joined the Alliance to fulfill his personal goal of piloting star cruisers.");
        setGameText("[Pilot] 2. Your Force drains and Force drain bonuses may not be canceled at same system.");
        addIcons(Icon.PILOT, Icon.DEATH_STAR_II);
        setSpecies(Species.MON_CALAMARI);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter sameSystem = Filters.sameSystem(self);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new AddsPowerToPilotedBySelfModifier(self, 2));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, sameSystem, (String) null, playerId));
        modifiers.add(new ForceDrainBonusesMayNotBeCanceledModifier(self, Filters.your(self), sameSystem));
        return modifiers;
    }
}
