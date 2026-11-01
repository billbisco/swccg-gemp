package com.gempukku.swccgo.cards.set1116.light;

import com.gempukku.swccgo.cards.AbstractPermanentAboard;
import com.gempukku.swccgo.cards.AbstractPermanentPilot;
import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Starship
 * Subtype: Starfighter
 * Title: Blue Squadron 5
 */
public class Card1116_024 extends AbstractStarfighter {
    public Card1116_024() {
        super(Side.LIGHT, 2, 2, 4, null, 2, 4, 4, "Blue Squadron 5", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setGameText("Heroic. Permanent pilot provides ability of 2. When deployed, may play a Defensive Shield (and/or deploy a weapon on this starship) from under your Starting Effect (as if from hand).");
        addIcons(Icon.DEATH_STAR_II, Icon.NAV_COMPUTER, Icon.PILOT);
        addKeywords(Keyword.HEROIC, Keyword.BLUE_SQUADRON);
        addModelType(ModelType.X_WING);
    }

    @Override
    protected List<? extends AbstractPermanentAboard> getGameTextPermanentsAboard() {
        return Collections.singletonList(new AbstractPermanentPilot(2) {});
    }
}
