package com.gempukku.swccgo.cards.set1.dark;

import com.gempukku.swccgo.cards.evaluators.MaxLimitEvaluator;
import com.gempukku.swccgo.cards.evaluators.MultiplyEvaluator;
import com.gempukku.swccgo.cards.evaluators.PresentEvaluator;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Premiere
 * Type: Character
 * Subtype: Alien
 * Title: Tusken Raider
 * Text state: d (Decipher's last word; docs/legacy-versions.md "Text states")
 * <p>
 * Decipher Glossary Supplement (29 January 2002) text, from wiki.swccg.com [[Tusken Raider (Errata)]] and the
 * Supplement's own entry (Glossary Version 2.0, November 1998, had the same rule without "that may add to power"):
 * power 1, +1/2 for each other non-unique Tusken Raider present, at most three others counted. That reproduces the
 * printed group totals (1, 3, 6, 10). The Players Committee's errata (SWCCG 2023 Advanced Rulebook Appendix A,
 * [[Tusken Raider (PC Errata)]]) is the current card (Card1_196). The glossary prints a stray symbol ("W") before
 * "Power"; it is left out here.
 */
public class Card1_196d extends Card1_196 {
    public Card1_196d() {
        super();
        setGameText("Deploys only on Tatooine. Power = 1, +1/2 for each other non-unique Tusken Raider present (limit three other Tusken Raiders that may add to power).");
        setTextState(new TextState("1_196", "D-E1",
                "Decipher Glossary Version 2.0, November 1998; Glossary Supplement, 29 January 2002",
                LocalDate.of(1998, 11, 1), LocalDate.of(2023, 7, 30)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(groupPowerModifier(self));
        return modifiers;
    }

    /** +1/2 power for each other non-unique Tusken Raider present, counting at most three. */
    static Modifier groupPowerModifier(PhysicalCard self) {
        return new PowerModifier(self, new MultiplyEvaluator(0.5f, new MaxLimitEvaluator(
                new PresentEvaluator(self, Filters.and(Filters.other(self), Filters.non_unique, Filters.Tusken_Raider)), 3)));
    }
}
