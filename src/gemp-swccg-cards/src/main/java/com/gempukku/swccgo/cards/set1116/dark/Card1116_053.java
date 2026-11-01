package com.gempukku.swccgo.cards.set1116.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.conditions.Condition;
import com.gempukku.swccgo.logic.modifiers.DestinyDrawForActionSourceModifier;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromCardModifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 16: Revenge Of The Sith
 * Type: Location
 * Subtype: Site
 * Title: Death Star: Conference Room
 */
public class Card1116_053 extends AbstractSite {
    public Card1116_053() {
        super(Side.DARK, Title.Death_Star_Conference_Room, Title.Death_Star, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_16, Rarity.V);
        setVirtualSuffix(true);
        setGameText("Dark: While you occupy two non-Death Star battlegrounds and your total influence here is > 5, you lose no Force to Visage of the Emperor. Light: While opponent has total influence > 5 here, your Sense and Alter destiny draws are +2. [Virtual Block 4] Laser Gate is Immune to Sabotage.");
        setLocationDarkSideGameText("While you occupy two non-Death Star battlegrounds and your total influence here is > 5, you lose no Force to Visage of the Emperor.");
        setLocationLightSideGameText("While opponent has total influence > 5 here, your Sense and Alter destiny draws are +2.");
        addIcon(Icon.DARK_FORCE, 1);
        addIcons(Icon.A_NEW_HOPE, Icon.INTERIOR_SITE, Icon.MOBILE, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(final String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NoForceLossFromCardModifier(self, Filters.Visage_Of_The_Emperor,
                occupiesTwoNonDeathStarBattlegroundsWithInfluenceHereGreaterThan(playerOnDarkSideOfLocation, self, 5),
                playerOnDarkSideOfLocation));
        // Not side text. Persistent so canceling one side of this site does not drop it. Still only while this site is in play.
        Modifier laserGate = new ImmuneToTitleModifier(self, virtualBlock4LaserGate(), Title.Sabotage);
        laserGate.setPersistent(true);
        modifiers.add(laserGate);
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new DestinyDrawForActionSourceModifier(self, Filters.or(Filters.Sense, Filters.Alter),
                opponentTotalInfluenceHereGreaterThan(playerOnLightSideOfLocation, self, 5), 2, playerOnLightSideOfLocation));
        return modifiers;
    }

    private Condition occupiesTwoNonDeathStarBattlegroundsWithInfluenceHereGreaterThan(final String playerId, final PhysicalCard site, final float influence) {
        return new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                if (playerId == null)
                    return false;
                int occupied = Filters.countTopLocationsOnTable(gameState.getGame(),
                        Filters.and(Filters.or(Filters.battleground_site, Filters.battleground_system),
                                Filters.occupies(playerId),
                                Filters.not(Filters.Death_Star_location)));
                return occupied >= 2
                        && modifiersQuerying.getTotalInfluenceAtLocation(gameState, playerId, site) > influence;
            }
        };
    }

    private Condition opponentTotalInfluenceHereGreaterThan(final String playerId, final PhysicalCard site, final float influence) {
        return new Condition() {
            @Override
            public boolean isFulfilled(GameState gameState, ModifiersQuerying modifiersQuerying) {
                if (playerId == null)
                    return false;
                String opponent = gameState.getGame().getOpponent(playerId);
                return modifiersQuerying.getTotalInfluenceAtLocation(gameState, opponent, site) > influence;
            }
        };
    }

    private Filter virtualBlock4LaserGate() {
        return Filters.and(Filters.title(Title.Laser_Gate), new Filter() {
            @Override
            public boolean accepts(GameState gameState, ModifiersQuerying modifiersQuerying, PhysicalCard physicalCard) {
                return physicalCard.getBlueprint().getExpansionSet() == ExpansionSet.VIRTUAL_ORIGINAL_SET_16;
            }
        });
    }
}
