package com.gempukku.swccgo.cards.set2261.dark;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.set601.dark.Card601_031;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Set: Virtual Block 6.1
 * Type: Defensive Shield
 * Title: A Useless Gesture (V)
 * Pre-errata version of 1261_20: the Virtual Block 6.1 release sheet text (Sep 2009), without "(if possible)" (added by
 * AR 2.0 Appendix A, 2012-08-27): an Interrupt that cannot be stacked here may not be played from Lost Pile at all.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2261_020 extends Card601_031 {
    public Card2261_020() {
        setGameText("Plays on table. In order to play an Interrupt from Lost Pile, opponent must first stack it here and use +1 Force for each card here, even if Interrupt is normally free.");
        setLegacyVersion(new LegacyVersion("1261_20", "",
                "Virtual Block 6.1 release sheet (2009-09-04), card #19", LocalDate.of(2009, 9, 4), LocalDate.of(2012, 8, 27)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());
        Filter mayNotBeStackedHere = new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
                return !GameConditions.canBeGrabbed(gameState.getGame(), self, card);
            }
        };
        List<Modifier> modifiers = new ArrayList<>(super.getGameTextWhileActiveInPlayModifiers(game, self));
        modifiers.add(new MayNotPlayModifier(self, Filters.and(Filters.Interrupt, Filters.inLostPile(opponent), mayNotBeStackedHere), opponent));
        return modifiers;
    }
}
