package com.gempukku.swccgo.cards.set1282.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.LegacyDeployTargets;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToLocationFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayDeployToTargetModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.2
 * Type: Location
 * Subtype: Site
 * Title: Hoth: Echo Corridor (V)
 */
public class Card1282_028 extends AbstractSite {
    public Card1282_028() {
        super(Side.LIGHT, "Hoth: Echo Corridor", Title.Hoth, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_2, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("Once per game, may deploy a [Hoth] general here from Reserve Deck; reshuffle. Non-[Defensive Shield] Chasm is canceled.");
        setLocationDarkSideGameText("While 1st Marker on table, Effects may deploy here instead of on 1st Marker.");
        setGameText("Light: Once per game, may deploy a [Hoth] general here from Reserve Deck; reshuffle. Non-[Defensive Shield] Chasm is canceled. Dark: While 1st Marker on table, Effects may deploy here instead of on 1st Marker.");
        addIcon(Icon.LIGHT_FORCE, 1);
        addIcons(Icon.UNDERGROUND, Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
    }

    private static Filter nonDefensiveShieldChasm() {
        return Filters.and(Filters.title("Chasm"), Filters.not(Filters.Defensive_Shield));
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__HOTH_ECHO_CORRIDOR_V__DEPLOY_HOTH_GENERAL;

        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerOnLightSideOfLocation, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerOnLightSideOfLocation, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a [Hoth] general here from Reserve Deck");
            action.setActionMsg("Deploy a [Hoth] general here from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendEffect(
                    new DeployCardToLocationFromReserveDeckEffect(action, Filters.and(Icon.HOTH, Filters.general), Filters.here(self), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredBeforeTriggers(String playerOnLightSideOfLocation, SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, nonDefensiveShieldChasm())
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerOnLightSideOfLocation, SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, nonDefensiveShieldChasm())) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, nonDefensiveShieldChasm(), "Chasm");
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayDeployToTargetModifier(self,
                Filters.and(Filters.owner(playerOnDarkSideOfLocation), Filters.Effect, LegacyDeployTargets.gameTextAllowsDeployingTo(self, Filters.First_Marker)),
                self));
        return modifiers;
    }
}
