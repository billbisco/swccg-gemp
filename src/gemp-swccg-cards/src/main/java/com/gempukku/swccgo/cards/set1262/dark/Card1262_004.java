package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Legacy Block 6.2: Comlink (V). */
public class Card1262_004 extends AbstractCharacterDevice {
    private static final Filter IMPERIAL_WARRIOR = Filters.and(Filters.Imperial, Filters.warrior);

    public Card1262_004() {
        super(Side.DARK, 3, Title.Comlink, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Hand-held communication device. Military comlinks have scrambling and variable frequency programming for secure communication during battles or emergencies.");
        setGameText("Deploy on an Imperial warrior. Your characters may deploy as a 'react' to same or adjacent site. If at an opponent's site, when opponent is losing Force from Force drains here, lost Force must come from Life Force (if possible).");
        addIcons(Icon.LEGACY_BLOCK_6);
        setAsLegacy(true);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId option,
                                                         boolean asReact) {
        return Filters.and(Filters.your(self), IMPERIAL_WARRIOR);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(SwccgGame game, PhysicalCard self) {
        return IMPERIAL_WARRIOR;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy a character as a 'react'",
                self.getOwner(), Filters.character, Filters.or(Filters.sameSite(self), Filters.adjacentSite(self))));
    }
}
