package com.gempukku.swccgo.cards.set1117.dark;

import com.gempukku.swccgo.cards.AbstractImperial;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.FireWeaponFiredByForFreeModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 17: The Force Unleashed
 * Type: Character
 * Subtype: Imperial
 * Title: Imperial Stormtrooper
 */
public class Card1117_019 extends AbstractImperial {
    public Card1117_019() {
        super(Side.DARK, 3, 2, 2, 2, 3, "Imperial Stormtrooper", Uniqueness.UNRESTRICTED, ExpansionSet.VIRTUAL_ORIGINAL_SET_17, Rarity.V);
        setVirtualSuffix(true);
        setArmor(4);
        setLore("");
        setGameText("Clone. Blaster Rifle deploys (and fires) for free on this trooper. When this trooper fires Blaster Rifle, may draw two weapon destiny and choose one (may take the other into hand). Immune to Rebel Barrier.");
        addIcons(Icon.EPISODE_I, Icon.WARRIOR);
        addKeywords(Keyword.CLONE_TROOPER);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToTargetModifier(self, Filters.title("Blaster Rifle"), self));
        modifiers.add(new FireWeaponFiredByForFreeModifier(self, Filters.title("Blaster Rifle")));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Rebel_Barrier));
        return modifiers;
    }
}
