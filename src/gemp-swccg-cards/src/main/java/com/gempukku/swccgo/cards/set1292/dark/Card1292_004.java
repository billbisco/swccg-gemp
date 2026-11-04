package com.gempukku.swccgo.cards.set1292.dark;

import com.gempukku.swccgo.cards.AbstractUniqueStarshipSite;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.ForceLossFromCardMayNotBeReducedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.2
 * Type: Location
 * Subtype: Site
 * Title: Executor: Meditation Chamber (V)
 */
public class Card1292_004 extends AbstractUniqueStarshipSite {
    public Card1292_004() {
        super(Side.DARK, Title.Meditation_Chamber, Persona.EXECUTOR, ExpansionSet.VIRTUAL_BLOCK_9_2, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("If Vader controls a battleground site, Force loss from [Dagobah] holograms may not be reduced.");
        setGameText("Dark: - Light: If Vader controls a battleground site, Force loss from [Dagobah] holograms may not be reduced.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.DAGOBAH, Icon.INTERIOR_SITE, Icon.STARSHIP_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        String darkSidePlayer = game.getOpponent(playerOnLightSideOfLocation);
        Condition vaderControlsBattlegroundSite = new OnTableCondition(self,
                Filters.and(Filters.battleground_site, Filters.controlsWith(darkSidePlayer, self, Filters.Vader)));

        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceLossFromCardMayNotBeReducedModifier(self, Filters.and(Icon.DAGOBAH, Filters.hologram), vaderControlsBattlegroundSite));
        return modifiers;
    }
}
