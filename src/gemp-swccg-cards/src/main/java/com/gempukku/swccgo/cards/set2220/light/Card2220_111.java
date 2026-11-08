package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Advantage
 *
 * Original version of 1220_111 (Virtual Block 2 release sheet, Dec 2009, card #112): every hit on a different opponent's
 * card during battle makes opponent's total power cumulatively -4 (-2 if by a lightsaber), not once per battle and
 * not optional as on the Virtual Cards Master List (2013-04-27).
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_111 extends AbstractNormalEffect {
    public Card2220_111() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Advantage, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("With only a few Jedi remaining in the galaxy, each confrontation between them has far reaching consequences.");
        setGameText("Deploy on your battleground. During battle, whenever opponent's card is hit by a non-[Permanent Weapon] weapon (once per card targeted), opponent's total power is cumulatively -4 (-2 if hit by a lightsaber).");
        setLegacyVersion(new LegacyVersion("1220_111", "",
                "Virtual Block 2 release sheet (2009-12-07), card #112", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
        addIcons(Icon.CLOUD_CITY);
        addKeywords(Keyword.DEPLOYS_ON_LOCATION);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.battleground, Filters.occupies(self.getOwner()));
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedToAfterCrossingOver(final SwccgGame game, final PhysicalCard self, PlayCardOptionId playCardOptionId) {
        return Filters.battleground;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        return List.of(com.gempukku.swccgo.cards.set2220.dark.Card2220_035.totalPowerModifier(game, self));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        return com.gempukku.swccgo.cards.set2220.dark.Card2220_035.hitTriggers(game, effectResult, self, gameTextSourceCardId);
    }
}
