package com.gempukku.swccgo.cards.set1.light;

import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;

import java.time.LocalDate;

/**
 * Set: Premiere
 * Type: Effect
 * Title: A Tremor In The Force
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed Premiere (December 1995) text, from the Decipher archive scan on wiki.swccg.com [[A Tremor In The Force]]
 * (the scan reads "'insert' (face down) in opponent's Reserve Deck"). As printed there is no once-per-game limit.
 * Decipher's Glossary Supplement (29 January 2002, [[A Tremor In The Force (Errata)]]) added "Once per game", which
 * is the current card (Card1_042). "Shuffle. Opponent cuts deck without looking." plays as the current "reshuffle".
 */
public class Card1_042o extends Card1_042 {
    public Card1_042o() {
        super();
        setGameText("During your deploy phase, 'insert' (face down) in opponent's Reserve Deck. Shuffle. Opponent cuts deck without looking. When Effect reaches top it is immediately lost, but opponent may not activate any more Force that turn. (Immune to Alter.)");
        setTextState(new TextState("1_42", "Printed", "Premiere (December 1995)",
                LocalDate.of(1995, 12, 1), LocalDate.of(2002, 1, 29)));
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return GameConditions.isDuringYourPhase(game, playerId, Phase.DEPLOY);
    }
}
