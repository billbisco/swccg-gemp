package com.gempukku.swccgo.cards.set1.light;

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
 * Title: Affect Mind
 * Text state: o (as printed, December 1995; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Affect Mind]] (Decipher archive scan); the current text is [[Affect Mind (PC Errata)]]
 * (Players Committee errata, Advanced Rulebook 2023 Appendix A). 
 * In GEMP the Effect only ever reduced ability where the Jedi is present at a location, which here is always a site, so
 * the PC errata's "While present at a site" plays the same; this state keeps the printed code (without that condition).
 * supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card1_043o extends Card1_043 {
    public Card1_043o() {
        super();
        setGameText("Use 1 Force to deploy on one of your Jedi. Unless a Dark Jedi is also present, opponent's total ability at same location is reduced by 2.");
        setTextState(new TextState("1_43", "Printed",
                "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2023, 7, 30)));
    }

    /** As printed: no "While present at a site" (also applies while the Jedi is aboard a starship or vehicle). */
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        String opponent = game.getOpponent(self.getOwner());

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TotalAbilityModifier(self, Filters.wherePresent(self),
                new UnlessCondition(new PresentCondition(self, Filters.Dark_Jedi)), -2, opponent));
        return modifiers;
    }
}
