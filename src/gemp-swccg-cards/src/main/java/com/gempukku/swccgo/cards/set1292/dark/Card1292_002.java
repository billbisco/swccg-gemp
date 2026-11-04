package com.gempukku.swccgo.cards.set1292.dark;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.conditions.AtCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.SuspendsCardModifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.2
 * Type: Character
 * Subtype: Republic
 * Title: Toonbuck Toora (V)
 */
public class Card1292_002 extends AbstractRepublic {
    public Card1292_002() {
        super(Side.DARK, 3, 2, 2, 3, 3, "Toonbuck Toora", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_2, Rarity.V);
        setVirtualSuffix(true);
        setPolitics(2);
        setLore("Female senator and member of her planet's ruling class. Her original optimism in the political process has been eroded by witnessing bribery, corruption and petty bickering.");
        setGameText("While at Separatist Council Room, Jar Jar may not deploy here and opponent's non-[Immune to Alter] Effects deployed on that site are suspended.");
        addIcons(Icon.EPISODE_I, Icon.SEPARATIST, Icon.CORUSCANT);
        addKeywords(Keyword.FEMALE, Keyword.SENATOR);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Condition atCouncilRoom = new AtCondition(self, Filters.title(Title.Geonosis_Separatist_Council_Room));
        Filter opponentsEffectsOnThatSite = Filters.and(Filters.opponents(self), Filters.Effect, Filters.not(Filters.immune_to_Alter),
                Filters.attachedTo(Filters.and(Filters.site, Filters.here(self))));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.Jar_Jar, atCouncilRoom, Filters.here(self)));
        modifiers.add(new SuspendsCardModifier(self, opponentsEffectsOnThatSite, atCouncilRoom));
        return modifiers;
    }
}
