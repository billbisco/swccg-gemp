package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.DeploysFreeToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Rebel
 * Title: Tigran Jamiro
 */
public class Card1220_104 extends AbstractRebel {
    public Card1220_104() {
        super(Side.LIGHT, 3, 2, 2, 2, 4, "Tigran Jamiro", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Senior logistics officer from Onderon. Left Dantooine to serve on Yavin 4 before coming to Hoth. All personnel entering Echo Base must report to him.");
        setGameText("Deploys free to Dantooine, Hoth, or Yavin 4. At same and related sites, opponent's aliens and Imperials are deploy +1.");
        addIcons(Icon.WARRIOR, Icon.HOTH);
        addPersona(Persona.TIGRAN);
    }

    @Override
    protected List<Modifier> getGameTextAlwaysOnModifiers(SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeploysFreeToLocationModifier(self, Filters.or(Filters.Dantooine_location, Filters.Hoth_location, Filters.Yavin_4_location)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DeployCostToLocationModifier(self, Filters.and(Filters.opponents(self), Filters.or(Filters.alien, Filters.Imperial)),
                1, Filters.sameOrRelatedSite(self)));
        return modifiers;
    }
}
