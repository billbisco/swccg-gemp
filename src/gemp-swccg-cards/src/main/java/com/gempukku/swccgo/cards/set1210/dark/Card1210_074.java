package com.gempukku.swccgo.cards.set1210.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 1
 * Type: Starship
 * Subtype: Starfighter
 * Title: Black 3
 */
public class Card1210_074 extends AbstractStarfighter {
    public Card1210_074() {
        super(Side.DARK, 3, 1, 1, null, 3, null, 3, "Black 3", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_1, Rarity.V);
        setVirtualSuffix(true);
        setLore("TIE/ln fighter of pilot DS-61-3. Stylized image of Corellian slice-hound painted on inner hatch.");
        setGameText("Heroic. May add 1 pilot. When this starship is targeted by Tallon Roll, 'Rebel' in its game text may be treated as '[Independent Starship]' or '[Republic]'. While DS-61-3 piloting, immune to attrition < 5.");
        addPersona(Persona.BLACK_3);
        addModelType(ModelType.TIE_LN);
        addKeywords(Keyword.NO_HYPERDRIVE, Keyword.BLACK_SQUADRON, Keyword.HEROIC);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.DS_61_3);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ModifyGameTextModifier(self, self, ModifyGameTextType.LEGACY__TALLON_ROLL__REBEL_MAY_BE_INDEPENDENT_OR_REPUBLIC));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.DS_61_3), 5));
        return modifiers;
    }
}
