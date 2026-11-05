package com.gempukku.swccgo.cards.set1283.dark;

import com.gempukku.swccgo.cards.AbstractDevice;
import com.gempukku.swccgo.cards.LegacyOmmniBox;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Device
 * Title: The Modal Nodes' Ommni Box
 */
public class Card1283_006 extends AbstractDevice {
    private static final Filter TECH_MOR = Filters.title("Tech Mo'r");

    public Card1283_006() {
        super(Side.DARK, 2, PlayCardZoneOption.ATTACHED, LegacyOmmniBox.TITLE, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setLore("Difficult-to-play instrument which enhances music. Tech Mo'r plays one for Figrin D'an and the Modal Nodes in the Mos Eisley Cantina.");
        setGameText("Deploy on Tech Mo'r or use 2 Force to deploy on your musician (or a site). Your musicians here not carrying this device are Heroic. 'At same site' on your musicians may be treated as 'at battleground sites.' 'Cover charges' only apply to opponent and are limited to 3.");
    }

    private static Filter deployTargets(PhysicalCard self) {
        return Filters.or(Filters.and(Filters.your(self), Filters.or(TECH_MOR, Filters.musician)), Filters.site);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DefinedByGameTextDeployCostToTargetModifier(self, 2, Filters.not(TECH_MOR)));
        return modifiers;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return deployTargets(self);
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(final SwccgGame game, final PhysicalCard self) {
        return deployTargets(self);
    }

    @Override
    protected Filter getGameTextValidToUseDeviceFilter(final SwccgGame game, final PhysicalCard self) {
        return Filters.musician;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourMusicians = Filters.and(Filters.your(self), Filters.musician);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, Filters.and(yourMusicians, Filters.here(self), Filters.not(Filters.hasAttached(self))), Keyword.HEROIC));
        modifiers.add(new ModifyGameTextModifier(self, yourMusicians, ModifyGameTextType.LEGACY__OMMNI_BOX__AT_SAME_SITE_MAY_BE_TREATED_AS_AT_BATTLEGROUND_SITES));
        modifiers.add(new ModifyGameTextModifier(self, Filters.musician, ModifyGameTextType.LEGACY__OMMNI_BOX__COVER_CHARGES_ONLY_OPPONENT_AND_LIMITED_TO_3));
        return modifiers;
    }
}
