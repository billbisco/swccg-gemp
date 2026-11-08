package com.gempukku.swccgo.cards.set2250.light;

import com.gempukku.swccgo.cards.set1250.light.Card1250_044;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.time.LocalDate;
import java.util.List;

/**
 * Set: Virtual Block 5
 * Type: Character
 * Subtype: Jedi Master
 * Title: Master Qui-Gon (V)
 * Pre-errata version of 1250_44: the Virtual Block 5 release sheet text (Dec 2009), "Immune to Disarmed" (the card
 * Disarmed only; AR 2.0 Appendix A, 2012-08-27, changed it to "May not be Disarmed"). Anything Goes only
 * (docs/legacy-versions.md).
 */
public class Card2250_044 extends Card1250_044 {
    public Card2250_044() {
        setGameText("Deploys -1 to [Episode I] locations. If you just lost Force from an opponent's Effect, may ▲ an [Episode I] Interrupt (except Control), or place a card from hand under Credits Will Do Fine. Immune to Disarmed, You Are Beaten, and attrition.");
        setLegacyVersion(new LegacyVersion("1250_44", "",
                "Virtual Block 5 release sheet (2009-12-07), card #45", LocalDate.of(2009, 12, 7), LocalDate.of(2012, 7, 21)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new ImmuneToTitleModifier(self, Title.Disarmed), new ImmuneToTitleModifier(self, Title.You_Are_Beaten),
                new ImmuneToAttritionModifier(self));
    }
}
