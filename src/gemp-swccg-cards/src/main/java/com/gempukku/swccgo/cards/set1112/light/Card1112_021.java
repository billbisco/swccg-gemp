package com.gempukku.swccgo.cards.set1112.light;

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
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.conditions.NotCondition;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToAttritionModifier;
import com.gempukku.swccgo.logic.modifiers.MayDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 12: Martyrs & Magistrates
 * Type: Character
 * Subtype: Jedi Master
 * Title: Yoda, Senior Council Member
 */
public class Card1112_021 extends AbstractJediMaster {
    public Card1112_021() {
        super(Side.LIGHT, 1, 4, 3, 7, 7, "Yoda, Senior Council Member", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_12, Rarity.V);
        setVirtualSuffix(true);
        setLore("Senior Jedi Council member. Responsible for the early training of Obi-Wan Kenobi. When Qui-Gon brought Anakin before the Council, Yoda voted not to train the boy.");
        setGameText("Deploys only to [Episode I] sites. While at Jedi Council Chamber, immune to attrition, Jedi are deploy -2 here, and (unless a Dark Jedi here) other Jedi may deploy to [Episode I] battleground sites.");
        addPersona(Persona.YODA);
        addIcons(Icon.CORUSCANT, Icon.EPISODE_I);
        addKeywords(Keyword.JEDI_COUNCIL_MEMBER);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.and(Filters.site, Icon.EPISODE_I);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        Condition atChamber = new AtCondition(self, Filters.Jedi_Council_Chamber);
        Condition noDarkJediHere = new NotCondition(new AtCondition(self, Filters.and(Filters.Dark_Jedi, Filters.here(self))));
        modifiers.add(new ImmuneToAttritionModifier(self, atChamber));
        modifiers.add(new DeployCostToLocationModifier(self, Filters.Jedi, atChamber, -2, Filters.Jedi_Council_Chamber));
        modifiers.add(new MayDeployToLocationModifier(self, Filters.and(Filters.Jedi, Filters.not(Filters.sameCardId(self))),
                new AndCondition(atChamber, noDarkJediHere), Filters.and(Filters.site, Icon.EPISODE_I, Filters.battleground)));
        return modifiers;
    }
}
