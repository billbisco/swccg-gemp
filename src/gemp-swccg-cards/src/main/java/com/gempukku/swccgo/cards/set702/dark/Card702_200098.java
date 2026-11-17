package com.gempukku.swccgo.cards.set702.dark;

import com.gempukku.swccgo.cards.AbstractDefensiveShield;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextModifier;
import com.gempukku.swccgo.logic.modifiers.ModifyGameTextType;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Modern blanked
 * Type: Defensive Shield
 * Title: Imperial Detention
 * Former PC id: 200_98 until 22 July 2024
 */
public class Card702_200098 extends AbstractDefensiveShield {
    public Card702_200098() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Imperial Detention", ExpansionSet.VIRTUAL_MODERN_BLANKED, Rarity.V);
        setGameText("Plays on table. For opponent to deploy a character, starship, or vehicle for free (except by that card's own game text), opponent must first use 2 Force. [Jabba's Palace] I Must Be Allowed To Speak may not target locations except Lars' Moisture Farm and Jabba's Palace sites.");
        addIcons(Icon.VIRTUAL_DEFENSIVE_SHIELD);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ExtraForceCostToDeployCardForFreeExceptByOwnGametextModifier(self, Filters.and(Filters.opponents(self), Filters.or(Filters.character, Filters.starship, Filters.vehicle)), 2));
        modifiers.add(new ModifyGameTextModifier(self, Filters.and(Icon.JABBAS_PALACE, Filters.title(Title.I_Must_Be_Allowed_To_Speak)),
                ModifyGameTextType.I_MUST_BE_ALLOWED_TO_SPEAK__DOES_NOT_TARGET_LOCATIONS_EXCEPT_LARS_MOISTURE_FARM_AND_JABBAS_PALACE_SITES));
        return modifiers;
    }
}
