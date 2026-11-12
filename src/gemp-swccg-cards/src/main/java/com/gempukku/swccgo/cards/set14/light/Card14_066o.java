package com.gempukku.swccgo.cards.set14.light;

import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: Theed Palace
 * Type: Weapon
 * Subtype: Starship
 * Title: Proton Torpedoes
 * Text state: o (as printed, December 2001; docs/legacy-versions.md "Text states")
 * <p>
 * The PC text only capitalizes "Draw Destiny". The Players Committee's errata (earliest source found: the PC Decipher Errata sheet, "Official text as of June 1, 2017";
 * 2023 Advanced Rulebook Appendix A) gives the current text (bare id); the date the PC first issued it is not established.
 * The wording change does not change how the card plays, so this state reuses the current card's game text code
 * and differs only in its text and text-state information.
 * Text from wiki.swccg.com [[Proton Torpedoes (Theed Palace)]].
 */
public class Card14_066o extends Card14_066 {
    public Card14_066o() {
        super();
        setGameText("Use 1 Force to deploy on your N-1 starfighter. May target a starship using 1 Force. Draw destiny. Target hit if destiny > defense value. While on Bravo Fighter and you are attempting to 'blow away' Blockade Flagship, adds 2 to your total.");
        setTextState(new TextState("14_66", "Printed",
                "Theed Palace printing (Decipher, December 2001)",
                LocalDate.of(2001, 12, 1), LocalDate.of(2017, 6, 1)));
    }
}
