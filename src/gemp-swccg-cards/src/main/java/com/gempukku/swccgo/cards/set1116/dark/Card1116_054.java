package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Location
 * Subtype: Site
 * Title: Execution Arena (Pit)
 */
public class Card1116_054 extends AbstractSite {
    public Card1116_054() {
        super(Side.DARK, "Execution Arena (Pit)", Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setLocationDarkSideGameText("May not deploy to Bespin, Dagobah, Endor, or Hoth. While a creature here, your characters present may not move from here. If a creature here is lost, its owner loses 3 Force. If opponent's character here was just eaten, retrieve 3 Force (6 if Padme).");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.SPECIAL_EDITION, Icon.EXTERIOR_SITE, Icon.PLANET);
        addMayNotBePartOfSystem(Title.Bespin);
        addMayNotBePartOfSystem(Title.Dagobah);
        addMayNotBePartOfSystem(Title.Endor);
        addMayNotBePartOfSystem(Title.Hoth);
    }
}
