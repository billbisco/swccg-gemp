package com.gempukku.swccgo.cards.set1117.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;

/**
 * Set: Virtual Set 17: The Force Unleashed
 * Type: Effect
 * Title: The Force Unleashed
 */
public class Card1117_020 extends AbstractNormalEffect {
    public Card1117_020() {
        super(Side.DARK, 0, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "The Force Unleashed", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_17, Rarity.V);
        setVirtualSuffix(true);
        setLore("");
        setGameText("Deploy on table. During battle, Lightsaber Parry, Tarkin's Orders, and There Is No Conflict gain: \"If Vader or Galen present: USED: Target a vehicle or starship present. Draw destiny. If destiny > 2, target 'thrown' (hit and adds its printed defense value to your total power). LOST: Target opponent's character present and choose (then draw destiny): if destiny > ability, target 'choked' (lost); OR if destiny +1 > ability, target 'drained of life' (hit and opponent loses 2 Force).\"");
        addIcons(Icon.CLOUD_CITY);
    }
}
