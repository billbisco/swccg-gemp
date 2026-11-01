package com.gempukku.swccgo.cards.set1113.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.cards.conditions.CantSpotCondition;
import com.gempukku.swccgo.logic.modifiers.MayNotBeFlippedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 13: Remote Outpost
 * Type: Location
 * Subtype: Site
 * Title: Coruscant: ISB Central Office
 */
public class Card1113_017 extends AbstractSite {
    public Card1113_017() {
        super(Side.DARK, "Coruscant: ISB Central Office", Title.Coruscant, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_13, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("Unless Blount present, Empire's Sinister Agents may not flip. Immune to Revolution.");
        setLocationDarkSideGameText("If your spy here, you may play non-[Virtual Set 8] Interrupts with 'advisor' in title for 1 Force.");
        setGameText("Light: Unless Blount present, Empire's Sinister Agents may not flip. Immune to Revolution. Dark: If your spy here, you may play non-[Virtual Set 8] Interrupts with 'advisor' in title for 1 Force.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.THEED_PALACE, Icon.INTERIOR_SITE, Icon.PLANET);
        addImmuneToCardTitle(Title.Revolution);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotBeFlippedModifier(self,
                new CantSpotCondition(self, Filters.and(Filters.present(self), Filters.titleContains("Blount"))),
                Filters.title(Title.Empires_Sinister_Agents)));
        return modifiers;
    }
}
