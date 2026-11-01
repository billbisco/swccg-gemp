package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: Jedi Council Chamber (V)
 */
public class Card1116_029 extends AbstractSite {
    public Card1116_029() {
        super(Side.LIGHT, Title.Jedi_Council_Chamber, Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("May not be deployed if your Senate on table. If you just deployed a Jedi Council Member here, may take any one card into hand from Used Pile; reshuffle.");
        setLocationDarkSideGameText("If your non-battleground site on table with no related locations (or if you do not control a battleground site), [Virtual Set 9] Wipe Them Out, All Of Them is suspended.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self) {
        return !GameConditions.canSpot(game, self, Filters.and(Filters.your(playerId), Filters.titleContains("Senate")));
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        if (!GameConditions.controls(game, playerOnDarkSideOfLocation, Filters.battleground_site)) {
            return Collections.<Modifier>singletonList(new SuspendsCardModifier(self, Filters.title("Wipe Them Out, All Of Them")));
        }
        return null;
    }
}
