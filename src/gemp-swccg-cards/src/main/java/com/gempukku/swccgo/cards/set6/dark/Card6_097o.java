package com.gempukku.swccgo.cards.set6.dark;

import com.gempukku.swccgo.cards.AbstractAlien;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.DestinyType;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.modifiers.DefinedByGameTextDeployCostModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Jabba's Palace
 * Title: Beedo
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Beedo]] (Decipher archive scan): the power bonus is for all your other Rodians, so
 * Beedo himself does not get it. The Players Committee errata (Advanced Rulebook Appendix A; supersededOn is an upper
 * bound, the exact date is not on the wiki) made it "all your Rodians", which the current card (Card6_097) plays; the
 * printed "any male Rodian" (current: "any other male Rodian") plays the same, as Beedo cannot replace himself.
 */
public class Card6_097o extends Card6_097 {
    public Card6_097o() {
        super();
        setGameText("* Replaces any male Rodian for free (Rodian goes to Used Pile) or deploys for 3 Force. While at Audience Chamber, all your other Rodians are power +2 and whenever Greedo 'threatens' a smuggler you may add 2 to your destiny draw.");
        setTextState(new TextState("6_97", "Printed",
                "Jabba's Palace printing (Decipher, May 1998)",
                LocalDate.of(1998, 5, 1), LocalDate.of(2023, 1, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        // As printed: all your *other* Rodians (the current card's power bonus also applies to Beedo himself).
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.and(Filters.your(self), Filters.other(self), Filters.Rodian), new AtCondition(self, Filters.Audience_Chamber), 2));
        return modifiers;
    }
}
