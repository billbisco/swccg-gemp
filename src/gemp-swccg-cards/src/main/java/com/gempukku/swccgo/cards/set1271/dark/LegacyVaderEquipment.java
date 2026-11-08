package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

/** Common attachment and immunity printed on the Block 7 Vader Effects. */
public abstract class LegacyVaderEquipment extends AbstractNormalEffect {
    protected LegacyVaderEquipment(float destiny, String title) {
        super(Side.DARK, destiny, PlayCardZoneOption.ATTACHED, title, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        addIcons(Icon.VADER);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected final Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId option, boolean asReact) {
        return Filters.and(Icon.VADER, Filters.Vader);
    }
}
