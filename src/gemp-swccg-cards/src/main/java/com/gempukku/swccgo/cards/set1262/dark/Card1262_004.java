package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.AbstractCharacterDevice;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.DuringForceDrainAtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.modifiers.MayDeployOtherCardsAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.2
 * Type: Device
 * Title: Comlink (V)
 */
public class Card1262_004 extends AbstractCharacterDevice {
    public Card1262_004() {
        super(Side.DARK, 3, Title.Comlink, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Hand-held communication device. Military comlinks have scrambling and variable frequency programming for secure communication during battles or emergencies.");
        setGameText("Deploy on an Imperial warrior. Your characters may deploy as a 'react' to same or adjacent site. If at an opponent's site, when opponent is losing Force from Force drains here, lost Force must come from Life Force (if possible).");
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.your(self), Filters.Imperial, Filters.warrior);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.and(Filters.Imperial, Filters.warrior);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String playerId = self.getOwner();
        String opponent = game.getOpponent(playerId);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployOtherCardsAsReactToLocationModifier(self, "Deploy a character as a 'react'", playerId,
                Filters.character, Filters.or(Filters.sameSite(self), Filters.adjacentSite(self))));
        modifiers.add(new SpecialFlagModifier(self, new AndCondition(new AtCondition(self, Filters.and(Filters.opponents(self), Filters.site)),
                new DuringForceDrainAtCondition(Filters.sameSite(self))), ModifierFlag.FORCE_DRAIN_LOST_FROM_LIFE_FORCE, opponent));
        return modifiers;
    }
}
