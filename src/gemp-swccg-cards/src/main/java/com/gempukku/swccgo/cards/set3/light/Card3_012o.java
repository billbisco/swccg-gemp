package com.gempukku.swccgo.cards.set3.light;

import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.cards.evaluators.AtSameSiteEvaluator;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.LeaderModifier;
import com.gempukku.swccgo.logic.modifiers.MayBeBattledModifier;
import com.gempukku.swccgo.logic.modifiers.MayInitiateBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Hoth
 * Title: K-3PO (Kay-Threepio)
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * As printed K-3PO is power +1 for each of your droids at same Hoth or Yavin 4 site, which counts himself; Decipher Glossary 2.0 (November 1998) made it "each of your other droids" (the current card, Card3_012). The leader clause plays the same ("at least one of your other droids").
 */
public class Card3_012o extends Card3_012 {
    public Card3_012o() {
        super();
        setGameText("May initiate battle and be battled. K-3PO is power +1 for each of your droids at same Hoth or Yavin 4 site. Functions as a leader if present with at least one of your other droids.");
        setTextState(new TextState("3_12", "Printed",
                "Hoth printing (Decipher, November 1996)",
                LocalDate.of(1996, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter yourOtherDroids = Filters.and(Filters.your(self), Filters.other(self), Filters.droid);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayInitiateBattleModifier(self));
        modifiers.add(new MayBeBattledModifier(self));
        modifiers.add(new PowerModifier(self, new AtCondition(self, Filters.or(Filters.Hoth_site, Filters.Yavin_4_site)),
                new AtSameSiteEvaluator(self, Filters.and(Filters.your(self), Filters.droid))));
        modifiers.add(new LeaderModifier(self, new PresentWithCondition(self, yourOtherDroids)));
        return modifiers;
    }
}
