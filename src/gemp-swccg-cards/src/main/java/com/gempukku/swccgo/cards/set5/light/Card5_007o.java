package com.gempukku.swccgo.cards.set5.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Species;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.AddsPowerToPilotedBySelfModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Cloud City
 * Title: Princess Leia
 * Text state: o (as printed; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Princess Leia]] (Decipher archive scan); superseded by Decipher Glossary Version 2.0 (November 1998), whose text the current card keeps.
 * As printed, the pilot bonus is "any starship"; the errata (current card, Card5_007) made it "anything", so this state adds power only to starships it pilots and otherwise plays as the current card.
 */
public class Card5_007o extends Card5_007 {
    public Card5_007o() {
        super();
        setGameText("Deploys only on Hoth or Cloud City, but may move elsewhere. Adds 1 to power of any starship she pilots. May deploy (on Hoth or Cloud City) or move as a 'react' to same site as Han or Luke. Immune to attrition < 3.");
        setTextState(new TextState("5_7", "Printed",
                "Cloud City printing (Decipher, November 1997)",
                LocalDate.of(1997, 11, 1), LocalDate.of(1998, 11, 1)));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        // As printed, the pilot bonus applies only to starships (the errata made it "anything").
        List<Modifier> modifiers = new LinkedList<Modifier>(super.getGameTextWhileActiveInPlayModifiers(game, self));
        modifiers.removeIf(modifier -> modifier instanceof AddsPowerToPilotedBySelfModifier);
        modifiers.add(0, new AddsPowerToPilotedBySelfModifier(self, 1, Filters.starship));
        return modifiers;
    }
}
