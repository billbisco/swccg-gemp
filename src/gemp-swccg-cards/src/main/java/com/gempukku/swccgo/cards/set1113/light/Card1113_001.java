package com.gempukku.swccgo.cards.set1113.light;

import com.gempukku.swccgo.cards.AbstractSystem;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.IgnoresLocationDeploymentRestrictionsInGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 13: Remote Outpost
 * Type: Location
 * Subtype: System
 * Title: Alderaan (Blown Away)
 */
public class Card1113_001 extends AbstractSystem {
    public Card1113_001() {
        super(Side.LIGHT, Title.Alderaan_Blown_Away, 2, ExpansionSet.VIRTUAL_ORIGINAL_SET_13, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("May not be deployed if an Alderaan site (or sector) on table. Star Cruisers ignore location deployment restrictions in their game text.");
        setGameText("May not be deployed if an Alderaan site (or sector) on table. Star Cruisers ignore location deployment restrictions in their game text.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcon(Icon.DARK_FORCE, 0);
        addIcons(Icon.SPACE, Icon.SPECIAL_EDITION, Icon.DAGOBAH);
    }

    @Override
    public List<Modifier> getAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = super.getAlwaysOnModifiers(game, self);
        modifiers.add(new MayNotDeployModifier(self, new OnTableCondition(self, Filters.or(
                Filters.Alderaan_site,
                Filters.and(Filters.sector, Filters.partOfSystem(Title.Alderaan))))));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IgnoresLocationDeploymentRestrictionsInGameTextModifier(self, Filters.Star_Cruiser));
        return modifiers;
    }
}
