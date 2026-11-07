package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractJediMaster;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.List;

/** Legacy Block 5 Yoda, Senior Council Member (V), printing 57. */
public class Card1250_057 extends AbstractJediMaster {
    public Card1250_057() {
        super(Side.LIGHT, 1, 4, 3, 7, 7, "Yoda, Senior Council Member", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("Senior Jedi Council member. Responsible for the early training of Obi-Wan Kenobi. When Qui-Gon brought Anakin before the Council, Yoda voted not to train the boy.");
        setGameText("Deploys only to [Episode I] sites. While at Jedi Council Chamber, immune to attrition, Jedi are deploy -2 here, and (unless a Dark Jedi here) other Jedi may treat \"Jedi Council Chamber\" in their game text as \"[Episode I] battleground sites.\"");
        addPersona(Persona.YODA);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeyword(Keyword.JEDI_COUNCIL_MEMBER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId option, boolean asReact) {
        return Filters.locationAndCardsAtLocation(Filters.and(Filters.site, Icon.EPISODE_I));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        var atChamber = new AtCondition(self, Filters.Jedi_Council_Chamber);
        return List.of(new ImmuneToAttritionModifier(self, atChamber),
                new DeployCostToLocationModifier(self, Filters.Jedi, atChamber, -2, Filters.Jedi_Council_Chamber));
    }
}
