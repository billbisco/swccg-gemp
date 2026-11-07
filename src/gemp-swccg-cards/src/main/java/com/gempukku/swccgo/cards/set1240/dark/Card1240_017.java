package com.gempukku.swccgo.cards.set1240.dark;

import com.gempukku.swccgo.cards.AbstractCreature;
import com.gempukku.swccgo.cards.conditions.AttachedCondition;
import com.gempukku.swccgo.cards.conditions.PresentCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DefenseValueModifier;
import com.gempukku.swccgo.logic.modifiers.EachWeaponDestinyModifier;
import com.gempukku.swccgo.logic.modifiers.GenerateNoForceModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotAttackModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotInitiateAttacksAtLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.ParasiteTargetModifier;

import java.util.List;

/**
 * Set: Virtual Block 4
 * Type: Creature (Force Eater)
 * Title: Thrawn's Ysalamir
 */
public class Card1240_017 extends AbstractCreature {
    public Card1240_017() {
        super(Side.DARK, 4, 2, 3, 4, 0, "Thrawn's Ysalamir", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_4, Rarity.V);
        setGameText("Habitat: sites. Parasite: Imperials. While attached and present, at same site: all Force generation is canceled, Dark Jedi and Jedi are defense value -1 and their weapon destinies are -1, and no attacks.");
        addIcons(Icon.DAGOBAH);
        addKeyword(Keyword.PARASITE);
    }

    @Override
    protected Filter getGameTextHabitatFilter(String playerId, SwccgGame game, PhysicalCard self) {
        return Filters.site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Condition attachedAndPresent = new AndCondition(new AttachedCondition(self, Filters.Imperial), new PresentCondition(self));
        Filter site = Filters.sameSite(self);
        Filter jedi = Filters.and(Filters.or(Filters.Dark_Jedi, Filters.Jedi), Filters.atSameSite(self));
        return List.of(
                new ParasiteTargetModifier(self, Filters.Imperial),
                new GenerateNoForceModifier(self, site, attachedAndPresent, game.getGameState().getLightPlayer()),
                new GenerateNoForceModifier(self, site, attachedAndPresent, game.getGameState().getDarkPlayer()),
                new DefenseValueModifier(self, jedi, attachedAndPresent, -1),
                new EachWeaponDestinyModifier(self, Filters.any, attachedAndPresent, jedi, -1),
                new MayNotInitiateAttacksAtLocationModifier(self, site, attachedAndPresent),
                new MayNotAttackModifier(self, attachedAndPresent, Filters.present(self)));
    }
}
