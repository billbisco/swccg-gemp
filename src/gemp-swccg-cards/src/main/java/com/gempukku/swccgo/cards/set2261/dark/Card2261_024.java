package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.set1261.dark.Card1261_021;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeCanceledByModifier;
import com.gempukku.swccgo.logic.modifiers.ForceDrainsMayNotBeModifiedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.time.LocalDate;
import java.util.List;

/**
 * Set: Virtual Block 6.1
 * Type: Defensive Shield
 * Title: Leave Them To Me (Death Star II) (V)
 * PC-E1 version of 1261_24 (the Death Star II conversion slip of Leave Them To Me (V), whose current printing shares
 * 1261_21's class): the revised Virtual Block 6.1 sheet (2010-04-20) printed only the Dantooine / Subjugated planet
 * sentence. The PC card list (by 2012-07-21) and the Virtual Cards Master List show the full first-copy text again,
 * which v3 keeps as the latest (not an option (b) case; open question for Bill). No Swamp / Jungle / Forest clauses.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_024 extends Card1261_021 {
    public Card2261_024() {
        setGameText("Plays on table. At opponent's Dantooine and Subjugated Planet sites, your Imperials are deploy -2 and your Force drains may not be modified or canceled by opponent.");
        setLegacyVersion(new LegacyVersion("1261_24", "PC-E1",
                "Virtual Block 6.1 release sheet, revised (2010-04-20), card #23", LocalDate.of(2010, 4, 20), LocalDate.of(2012, 7, 21)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        String playerId = self.getOwner();
        Filter protectedSites = Filters.and(Filters.opponents(self), Filters.site,
                Filters.or(Filters.Dantooine_site, Filters.Subjugated_planet_location));
        return List.of(
                new DeployCostToLocationModifier(self, Filters.and(Filters.your(self), Filters.Imperial), -2, protectedSites),
                new ForceDrainsMayNotBeModifiedByModifier(self, Filters.opponents(self), null, playerId, protectedSites),
                new ForceDrainsMayNotBeCanceledByModifier(self, Filters.opponents(self), playerId, protectedSites));
    }
}
