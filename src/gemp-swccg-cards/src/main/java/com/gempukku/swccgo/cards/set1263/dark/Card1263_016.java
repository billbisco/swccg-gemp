package com.gempukku.swccgo.cards.set1263.dark;

import com.gempukku.swccgo.cards.AbstractSector;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.evaluators.MultiplyEvaluator;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledModifier;
import com.gempukku.swccgo.logic.modifiers.ForceGenerationImmuneToLimitModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalBattleDestinyModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 6.3
 * Type: Location
 * Subtype: Sector
 * Title: Storm Clouds
 */
public class Card1263_016 extends AbstractSector {
    public Card1263_016() {
        super(Side.DARK, "Storm Clouds", Uniqueness.DIAMOND_1, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setLocationDarkSideGameText("If you control a related battleground site, your Force drains here are +1 and may not be canceled. Your Force generation at the related system may not be limited.");
        setLocationLightSideGameText("Unless you control a related site, your Force drains here are -1. For each restricted (•••) TIE present, your total battle destiny here is -1.");
        setGameText("Dark: If you control a related battleground site, your Force drains here are +1 and may not be canceled. Your Force generation at the related system may not be limited. Light: Unless you control a related site, your Force drains here are -1. For each restricted (•••) TIE present, your total battle destiny here is -1.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.PLANET, Icon.CLOUD_CITY);
        addKeywords(Keyword.CLOUD_SECTOR);
        addMayNotBePartOfSystem(Title.Ahch_To);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        ControlsCondition controlsRelatedBattlegroundSite = new ControlsCondition(playerOnDarkSideOfLocation,
                Filters.and(Filters.relatedSite(self), Filters.battleground_site));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, controlsRelatedBattlegroundSite, 1, playerOnDarkSideOfLocation));
        modifiers.add(new ForceDrainsMayNotBeCanceledModifier(self, self, controlsRelatedBattlegroundSite, playerOnDarkSideOfLocation));
        modifiers.add(new ForceGenerationImmuneToLimitModifier(self, Filters.relatedSystem(self), Filters.any, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self, new NotCondition(new ControlsCondition(playerOnLightSideOfLocation, Filters.relatedSite(self))), -1, playerOnLightSideOfLocation));
        modifiers.add(new TotalBattleDestinyModifier(self, Filters.here(self),
                new MultiplyEvaluator(-1, new PresentEvaluator(self, Filters.and(Filters.TIE, Filters.uniqueness(Uniqueness.RESTRICTED_3)))),
                playerOnLightSideOfLocation));
        return modifiers;
    }
}
