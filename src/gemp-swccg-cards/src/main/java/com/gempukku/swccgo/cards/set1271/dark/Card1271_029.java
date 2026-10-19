package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractStarfighter;
import com.gempukku.swccgo.cards.conditions.DuringBattleCondition;
import com.gempukku.swccgo.cards.conditions.HasPilotingCondition;
import com.gempukku.swccgo.cards.conditions.PresentWithCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.ModelType;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionLessThanModifier;
import com.gempukku.swccgo.logic.modifiers.MayFireOneWeaponTwicePerBattleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 7.1
 * Type: Starship
 * Subtype: Starfighter
 * Title: Saber Squadron TIE
 */
public class Card1271_029 extends AbstractStarfighter {
    public Card1271_029() {
        super(Side.DARK, 2, 2, 3, null, 4, null, 4, "Saber Squadron TIE", Uniqueness.RESTRICTED_3, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setLore("-");
        setGameText("May add 1 pilot. Power +1 while present with an X-wing. During battle, may fire one starship weapon aboard twice. Immune to attrition < 4 when matching pilot aboard.");
        addIcons(Icon.DEATH_STAR_II);
        addKeywords(Keyword.NO_HYPERDRIVE);
        addModelType(ModelType.TIE_INTERCEPTOR);
        setPilotCapacity(1);
        setMatchingPilotFilter(Filters.title("Saber Squadron Pilot"));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, new PresentWithCondition(self, Filters.X_wing), 1));
        modifiers.add(new MayFireOneWeaponTwicePerBattleModifier(self, new DuringBattleCondition(), Filters.starship_weapon));
        modifiers.add(new ImmuneToAttritionLessThanModifier(self, new HasPilotingCondition(self, Filters.matchingPilot(self)), 4));
        return modifiers;
    }
}
