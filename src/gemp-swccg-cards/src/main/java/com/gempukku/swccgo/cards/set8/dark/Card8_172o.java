package com.gempukku.swccgo.cards.set8.dark;

import com.gempukku.swccgo.cards.AbstractCombatVehicle;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TextState;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.AttritionModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayMoveAsReactForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;


/**
 * Set: Endor
 * Type: Vehicle
 * Subtype: Combat
 * Title: Tempest Scout 1
 * Text state: o (as printed, June 1998; docs/legacy-versions.md "Text states")
 * <p>
 * Printed text from wiki.swccg.com [[Tempest Scout 1]] (Decipher archive scan): "adds 1 to attrition against
 * opponent" with no "here", so while Arnet pilots it, attrition against opponent is +1 in every battle. The PC errata
 * ([[Tempest Scout 1 (PC Errata)]], current card Card8_172) limits it to battles here. The PC errata date is not
 * known; supersededOn is an upper bound (Advanced Rulebook 2023 Appendix A, PDF dated 30 July 2023).
 */
public class Card8_172o extends AbstractCombatVehicle {
    public Card8_172o() {
        super(Side.DARK, 3, 2, 3, 4, null, 3, 4, Title.Tempest_Scout_1, Uniqueness.UNIQUE, ExpansionSet.ENDOR, Rarity.R);
        setLore("Enclosed AT-ST assigned as first response to incidents at Endor control bunker. Piloted by Lieutenant Arnet.");
        setGameText("May add 2 pilots or passengers. May move as a 'react' for free. When Arnet piloting, immune to attrition < 4 and adds 1 to attrition against opponent.");
        setTextState(new TextState("8_172", "Printed",
                "Endor printing (Decipher, June 1998)",
                LocalDate.of(1998, 6, 1), LocalDate.of(2023, 7, 30)));
        addModelType(ModelType.AT_ST);
        addIcons(Icon.ENDOR, Icon.SCOMP_LINK);
        addKeywords(Keyword.ENCLOSED);
        setPilotOrPassengerCapacity(2);
        setMatchingPilotFilter(Filters.Arnet);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition arnetPiloting = new HasPilotingCondition(self, Filters.Arnet);

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayMoveAsReactForFreeModifier(self));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, arnetPiloting, 4));
        modifiers.add(new AttritionModifier(self, arnetPiloting, 1, game.getOpponent(self.getOwner())));
        return modifiers;
    }
}
