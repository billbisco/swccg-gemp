package com.gempukku.swccgo.cards.set9.light;

import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.MayBeTargetedByModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Death Star II
 * Title: Luminous
 * Text state: o (as printed, July 2000; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Luminous]] (Decipher archive scan); the current text is
 * [[Luminous (PC Errata)]]. The PC errata removed "Has ship-docking capability" and added "(even while landed)" to Covert Landing; this
 * state keeps ship-docking capability (every capital starship has it in GEMP) and may not use Covert Landing while landed (a landed
 * starship's game text applies only while piloted; the errata grants Covert Landing even while landed). Everything else plays as the current card (Card9_077). The PC errata date is not known; supersededOn is an upper bound (Advanced
 * Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card9_077o extends Card9_077 {
    public Card9_077o() {
        super();
        setGameText("Deploys and moves like a starfighter. May add 1 pilot and 6 passengers. Has ship-docking capability. Permanent pilot provides ability of 2. May use Covert Landing like a shuttle or starfighter.");
        setTextState(new TextState("9_77", "Printed",
                "Death Star II printing (Decipher, July 2000)",
                LocalDate.of(2000, 7, 1), LocalDate.of(2023, 7, 30)));
        addKeyword(Keyword.SHIP_DOCKING_CAPABILITY);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiersEvenIfUnpiloted(SwccgGame game, final PhysicalCard self) {
        return null;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        // Printed: no "(even while landed)": Covert Landing only while this starship's game text applies (not landed).
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayBeTargetedByModifier(self, Title.Covert_Landing));
        return modifiers;
    }
}
