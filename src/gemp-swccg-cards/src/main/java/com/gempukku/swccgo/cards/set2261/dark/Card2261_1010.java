package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.set1261.dark.Card1261_010;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.CancelImmunityToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireOnlyOncePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.time.LocalDate;
import java.util.List;

/**
 * Set: Virtual Block 6.1
 * Type: Character
 * Title: Darth Vader, Betrayer Of The Jedi
 * PC-E1 version of 1261_10: the PC website card list text (2012-07-21 .. 2013-01-11), which dropped "here" from
 * "lightsabers here may not be 'swung' twice": while Vader is present with Obi-Wan or a Jedi Council Member, no
 * lightsaber may be 'swung' twice (not only those at his site). The Master List (2013-04-27, PC-E2, the current card)
 * restored "here" and reordered the clauses.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_1010 extends Card1261_010 {
    private static final Filter JEDI = Filters.or(Filters.and(Filters.ObiWan, Filters.icon(Icon.REFLECTIONS_III)),
            Filters.Jedi_Council_member);

    public Card2261_1010() {
        setGameText("[Pilot] 3. The Circle Is Now Complete is canceled. While present with [Reflections III] Obi-Wan or a Jedi Council Member, lightsabers may not be 'swung' twice, their immunity to attrition is canceled and you may add one battle destiny. Immune to attrition < 5.");
        setLegacyVersion(new LegacyVersion("1261_10", "PC-E1",
                "PC website card list (by 2012-07-21)", LocalDate.of(2012, 7, 21), LocalDate.of(2013, 4, 27)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter lightsabers = Filters.or(Filters.lightsaber, Filters.hasPermanentWeapon(Filters.lightsaber));
        return List.of(new AddsPowerToPilotedBySelfModifier(self, 3),
                new MayFireOnlyOncePerBattleModifier(self, lightsabers, new PresentWithCondition(self, JEDI)),
                new CancelImmunityToAttritionModifier(self, Filters.and(JEDI, Filters.presentWith(self))),
                new ImmuneToAttritionLessThanModifier(self, 5));
    }
}
