package com.gempukku.swccgo.cards.set1100.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationWithoutPresenceOrForceIconsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set: Defensive Shields
 * Type: Defensive Shield
 * Title: No Escape
 */
public class Card1100_014 extends AbstractDefensiveShield {
    public Card1100_014() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.No_Escape, ExpansionSet.VIRTUAL_ORIGINAL_SHIELDS, Rarity.V);
        setVirtualSuffix(true);
        setLore("Jabba's influence is not easily ignored. Neither are his voracious and vile appetites. Even Jedi soon learn this lesson.");
        setGameText("Plays on table. At opponent's ◇ site where opponent's creature present, you may deploy without presence or Force icons, and your Force generation there is +1.");
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        Filter opponentsGenericSiteWithCreature = Filters.and(Filters.opponents(self), Filters.generic_site,
                Filters.wherePresent(self, Filters.and(Filters.opponents(self), Filters.creature)), Filters.canBeTargetedBy(self));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToLocationWithoutPresenceOrForceIconsModifier(self, Filters.your(self), opponentsGenericSiteWithCreature));
        modifiers.add(new ForceGenerationModifier(self, opponentsGenericSiteWithCreature, 1, playerId));
        return modifiers;
    }
}
