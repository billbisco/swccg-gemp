package com.gempukku.swccgo.cards.set1111.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ModifierFlag;
import com.gempukku.swccgo.logic.modifiers.SpecialFlagModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 11: Gathering Storm
 * Type: Effect
 * Title: Forced Landing
 */
public class Card1111_021 extends AbstractNormalEffect {
    public Card1111_021() {
        super(Side.DARK, 5, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Forced_Landing, Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_11, Rarity.V);
        setVirtualSuffix(true);
        setLore("'You will not deviate from your present course. . . Permission granted to land on platform three-two-seven.'");
        setGameText("Deploy on table. After starting hands are drawn, players may only take into hand or deploy on table from Reserve Deck (except as a 'react' or response) during their deploy phase (if another phase specified, it becomes 'during your deploy phase'). (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new SpecialFlagModifier(self, ModifierFlag.MAY_ONLY_TAKE_OR_DEPLOY_FROM_RESERVE_DURING_DEPLOY_PHASE));
        return modifiers;
    }
}
