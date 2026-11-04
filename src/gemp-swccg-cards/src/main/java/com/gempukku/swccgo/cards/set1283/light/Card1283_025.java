package com.gempukku.swccgo.cards.set1283.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.KeywordModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeConvertedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.NoForceLossFromForceDrainModifier;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.3
 * Type: Location
 * Subtype: Site
 * Title: Tatooine: Cantina (V)
 */
public class Card1283_025 extends AbstractSite {
    public Card1283_025() {
        super(Side.LIGHT, Title.Cantina, Title.Tatooine, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_3, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("Scoundrels are smugglers. May use 1 Force to [download] Mos Eisley. Immune to Revolution.");
        setLocationDarkSideGameText("You lose no Force to Force drains at opponent's Kessel system. May not be converted.");
        setGameText("Light: Scoundrels are smugglers. May use 1 Force to [download] Mos Eisley. Immune to Revolution. Dark: You lose no Force to Force drains at opponent's Kessel system. May not be converted.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET);
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerOnLightSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new KeywordModifier(self, Filters.scoundrel, Keyword.SMUGGLER));
        modifiers.add(new ImmuneToTitleModifier(self, Title.Revolution));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__TATOOINE_CANTINA_V__DEPLOY_MOS_EISLEY;

        if (GameConditions.canUseForce(game, playerOnLightSideOfLocation, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId, Title.Mos_Eisley)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Mos Eisley from Reserve Deck");
            action.setActionMsg("Deploy Tatooine: Mos Eisley from Reserve Deck");
            action.appendCost(
                    new UseForceEffect(action, playerOnLightSideOfLocation, 1));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, Filters.Mos_Eisley, true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        String opponent = game.getOpponent(playerOnDarkSideOfLocation);
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new NoForceLossFromForceDrainModifier(self, Filters.and(Filters.Kessel_system, Filters.owner(opponent)), playerOnDarkSideOfLocation));
        modifiers.add(new MayNotBeConvertedModifier(self));
        return modifiers;
    }
}
