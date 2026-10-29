package com.gempukku.swccgo.cards.set15.light;

import com.gempukku.swccgo.cards.set5.light.Card5_059;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Uniqueness;

/** Reflections Gold printing of Off The Edge. */
public class Card15_114 extends Card5_059 {
    public Card15_114() {
        setLore("");
        setCredits(3);
        setGameText("Target one of your characters at a Cloud City site. Draw destiny. If destiny > character's destiny number, retrieve Force equal to the difference. If destiny < character's destiny number, lose difference. If destiny = character's destiny number, character is lost.");
        setAlternateDestiny(2);
        setAsHorizontal(true);
        for (Icon icon : Icon.values()) {
            if (icon.isExpansionIcon() || icon == Icon.EPISODE_I) {
                addIcon(icon, 0);
            }
        }
        addIcons(Icon.REFLECTIONS_GOLD);
    }

    @Override
    public ExpansionSet getExpansionSet() {
        return ExpansionSet.REFLECTIONS_GOLD;
    }

    @Override
    public Rarity getRarity() {
        return Rarity.UNKNOWN;
    }

    @Override
    public Float getDestiny() {
        return 2f;
    }

    @Override
    public Uniqueness getUniqueness() {
        return Uniqueness.UNIQUE;
    }
}
