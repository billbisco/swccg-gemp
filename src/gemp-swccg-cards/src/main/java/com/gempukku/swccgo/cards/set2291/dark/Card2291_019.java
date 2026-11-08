package com.gempukku.swccgo.cards.set2291.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.TreatedAsInSenateMajorityModifier;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 9.1
 * Type: Location
 * Subtype: Site
 * Title: Geonosis: Separatist Council Room
 * Original version of 1291_19 (Virtual Block 9.1 release sheet, Jan 2014). Anything Goes only (docs/legacy-versions.md).
 */
public class Card2291_019 extends AbstractSite {
    public Card2291_019() {
        super(Side.DARK, Title.Geonosis_Separatist_Council_Room, Title.Geonosis, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_9_1, Rarity.V);
        setLocationDarkSideGameText("May deploy Gunray from Reserve Deck; reshuffle. While two senators here, they may use their game text as if in a senate majority.");
        setLocationLightSideGameText("While Gunray or opponent's senator here, your spies may not deploy here.");
        setGameText("Dark: May deploy Gunray from Reserve Deck; reshuffle. While two senators here, they may use their game text as if in a senate majority. Light: While Gunray or opponent's senator here, your spies may not deploy here.");
        setLegacyVersion(new LegacyVersion("1291_19", "", "Virtual Block 9.1 release sheet (2014-01-10)", LocalDate.of(2014, 1, 10), LocalDate.of(2014, 2, 4)));
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.EPISODE_I, Icon.INTERIOR_SITE, Icon.UNDERGROUND, Icon.PLANET, Icon.SCOMP_LINK);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(final String playerOnDarkSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__SEPARATIST_COUNCIL_ROOM__DOWNLOAD_GUNRAY;

        // Check condition(s)
        if (GameConditions.canDeployCardFromReserveDeck(game, playerOnDarkSideOfLocation, self, gameTextActionId, Persona.GUNRAY)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnDarkSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Gunray from Reserve Deck");
            // Perform result(s)
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Gunray, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new TreatedAsInSenateMajorityModifier(self, Filters.and(Filters.senator, Filters.here(self)),
                new HereCondition(self, 2, Filters.senator)));
        return modifiers;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotDeployToLocationModifier(self, Filters.and(Filters.your(playerOnLightSideOfLocation), Filters.spy),
                new HereCondition(self, Filters.or(Filters.Gunray, Filters.and(Filters.opponents(playerOnLightSideOfLocation), Filters.senator))),
                self));
        return modifiers;
    }
}
