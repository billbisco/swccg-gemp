package com.gempukku.swccgo.cards.set2262.dark;

import com.gempukku.swccgo.cards.set1262.dark.Card1262_007;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.time.LocalDate;
import java.util.List;

/**
 * Set: Virtual Block 6.2
 * Type: Effect
 * Subtype: Immediate
 * Title: Power Of The Sith
 * Pre-errata version of 1262_7: the Virtual Block 6.2 release sheet text (Dec 2009), character "immune to ... Disarmed"
 * (the card Disarmed only; the Master List of 2013-04-27 changed it to "may not be Disarmed").
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2262_007 extends Card1262_007 {
    public Card2262_007() {
        setGameText("If your character just won an epic duel, deploy on that character. Character is immune to Clash Of Sabers, Disarmed, and Sorry About The Mess. During your control phase, if at a battleground, opponent loses 1 Force. If this card just lost, place it in Used Pile. (Immune to Control.)");
        setLegacyVersion(new LegacyVersion("1262_7", "",
                "Virtual Block 6.2 release sheet (2009-12-07), card #2-09", LocalDate.of(2009, 12, 7), LocalDate.of(2013, 4, 27)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter bearer = Filters.hasAttached(self);
        return List.of(new ImmuneToTitleModifier(self, bearer, Title.Clash_Of_Sabers),
                new ImmuneToTitleModifier(self, bearer, Title.Disarmed),
                new ImmuneToTitleModifier(self, bearer, Title.Sorry_About_The_Mess));
    }
}
