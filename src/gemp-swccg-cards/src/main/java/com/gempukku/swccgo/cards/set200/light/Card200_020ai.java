package com.gempukku.swccgo.cards.set200.light;

/**
 * Set: Set 0
 * Type: Character
 * Subtype: Rebel
 * Title: Luke Skywalker (V) (ALT)
 * The alternate-image printing (deck id 200_20^). Its current PC card image (VirtualAlternateImage-Light
 * lukeskywalker_ai, 20 January 2026) words the game text differently from the regular image; the wording on the card
 * image takes precedence (Bill, 12 Nov 2026), so the ALT printing shows its own words. It plays as the regular card:
 * the same Attack Run bonus, the same control-phase choice of retrieving 1 Force or taking Darklighter Spin into hand.
 * An ALT printing is the same card as its base printing (deck counts, bans; docs/legacy-versions.md "AI printings").
 */
public class Card200_020ai extends Card200_020 {
    public Card200_020ai() {
        super();
        setGameText("Adds 3 to power of anything he pilots (and to Attack Run total if piloting lead starfighter). If piloting at a battleground during your control phase, may retrieve 1 Force or take Darklighter Spin into hand from Reserve Deck; reshuffle.");
    }
}
