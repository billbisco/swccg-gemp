package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.UnlessCondition;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TotalAbilityModifier;
import java.util.LinkedList;
import java.util.List;
import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Title: I Find Your Lack Of Faith Disturbing
 * Text state: o (as printed, December 1995; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[I Find Your Lack Of Faith Disturbing]] (Decipher archive scan); the current text is [[I Find Your Lack Of Faith Disturbing (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). 
 * In GEMP the Effect only ever reduced ability where the Dark Jedi is present at a location (always a site here), and every
 * Jedi is an opponent's, so the PC errata plays the same; this state keeps the printed code (opponent's Jedi, no site condition).
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card1_217o extends Card1_217 {
    public Card1_217o() {
        super();
        setGameText("Use 1 Force to deploy on one of your Dark Jedi. Opponent's total ability at same location is reduced by 2, unless an opponent's Jedi is also present.");
        setTextState(new TextState("1_217", "Printed",
                "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }

    /** As printed: no "While present at a site" (also applies while the Dark Jedi is aboard a starship or vehicle), and only an opponent's Jedi present stops it. */
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalAbilityModifier(self, Filters.wherePresent(self),
                new UnlessCondition(new PresentCondition(self, Filters.and(Filters.opponents(self), Filters.Jedi))), -2, opponent));
        return modifiers;
    }
}
