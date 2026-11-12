package com.gempukku.swccgo.cards.set2.dark;

import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.RemainsInPlayWhenForfeitedFromPlayModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import java.util.List;
import com.gempukku.swccgo.common.TextState;

import java.time.LocalDate;

/**
 * Set: A New Hope
 * Title: WED15-l7 'Septoid' Droid
 * Text state: o (as printed, 1997; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[WED15-l7 'Septoid' Droid]] (Decipher archive scan); superseded by the Players Committee errata, wiki
 * [[WED15-l7 'Septoid' Droid (PC Errata)]] (date unknown; upper bound March 2017).
 * The bare card (Card2_109) plays the errata's "(unless 'hit')" (the engine's remains-in-play modifier does not apply to
 * a 'hit' card); this state keeps the printed text, which has no such exception.
 */
public class Card2_109o extends Card2_109 {
    public Card2_109o() {
        super();
        setGameText("* Forfeit value begins at 7. When 'forfeited,' droid remains in play, but forfeit value is reduced by the amount of attrition or battle damage absorbed. Droid lost when forfeit value reaches zero.");
        setTextState(new TextState("2_109", "Printed",
                "A New Hope printing (Decipher, July 1997)",
                LocalDate.of(1997, 7, 1), LocalDate.of(2017, 3, 1)));
    }

    /**
     * As printed: "droid remains in play" when forfeited, with no "(unless 'hit')". The engine's
     * RemainsInPlayWhenForfeitedFromPlayModifier plays the PC errata (it does not apply to a 'hit' card), so this state
     * uses one without that check.
     */
    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = super.getGameTextWhileActiveInPlayModifiers(game, self);
        modifiers.removeIf(modifier -> modifier instanceof RemainsInPlayWhenForfeitedFromPlayModifier);
        modifiers.add(new RemainsInPlayWhenForfeitedFromPlayModifier(self) {
            @Override
            public Condition getAdditionalCondition(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard card) {
                final int permCardId = card.getPermanentCardId();
                return new Condition() {
                    @Override
                    public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                        PhysicalCard droid = gameState.findCardByPermanentId(permCardId);
                        return !modifiersQuerying.isProhibitedFromHavingForfeitReduced(gameState, droid);
                    }
                };
            }
        });
        return modifiers;
    }
}
