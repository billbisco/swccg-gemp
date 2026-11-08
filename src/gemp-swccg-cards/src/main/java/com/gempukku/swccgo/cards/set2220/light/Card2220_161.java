package com.gempukku.swccgo.cards.set2220.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.ControlsCondition;
import com.gempukku.swccgo.cards.conditions.HereCondition;
import com.gempukku.swccgo.logic.conditions.AndCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.LegacyVersion;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Location
 * Subtype: Site
 * Title: Hoth: Echo Med Lab
 *
 * Original version of 1220_161 (Virtual Block 2 release sheet, Dec 2009, card #162): the Dark Side Force drain +1 needs
 * the Dark Side player to control this site with an Imperial present. GEMP's current text ("If your Imperial present")
 * is an undated PC Legacy JSON revision.
 * Anything Goes only (docs/legacy-versions.md).
 */
public class Card2220_161 extends AbstractSite {
    public Card2220_161() {
        super(Side.LIGHT, Title.Echo_Med_Lab, Title.Hoth, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("During your deploy phase, may deploy a medical droid here from Reserve Deck; reshuffle.");
        setLocationDarkSideGameText("If you control, with an Imperial present, Force drain +1 here.");
        setGameText("Light: During your deploy phase, may deploy a medical droid here from Reserve Deck; reshuffle. Dark: If you control, with an Imperial present, Force drain +1 here.");
        setLegacyVersion(new LegacyVersion("1220_161", "",
                "Virtual Block 2 release sheet (2009-12-07), card #162", LocalDate.of(2009, 12, 7), LocalDate.of(2014, 2, 4)));
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.HOTH, Icon.UNDERGROUND, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ForceDrainModifier(self,
                new AndCondition(new ControlsCondition(playerOnDarkSideOfLocation, self),
                        new HereCondition(self, Filters.and(Filters.your(playerOnDarkSideOfLocation), Filters.Imperial))),
                1, playerOnDarkSideOfLocation));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__ECHO_MED_LAB_V__DEPLOY_MEDICAL_DROID;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a medical droid from Reserve Deck");
            action.setActionMsg("Deploy a medical droid here from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.medical_droid, Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
