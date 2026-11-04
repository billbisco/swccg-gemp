package com.gempukku.swccgo.cards.set1281.light;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
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
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.MultipleChoiceAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ExchangeCardInHandWithTopCardOfForcePileEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.LimitForceLossFromForceDrainModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 8.1
 * Type: Location
 * Subtype: Site
 * Title: Yavin 4: Massassi War Room (V)
 */
public class Card1281_043 extends AbstractSite {
    private static final String DEPLOY = "Deploy Rebel Aces from Reserve Deck";
    private static final String EXCHANGE = "Exchange a card in hand with top card of Force Pile";
    private static final String BOTH = "Both";

    public Card1281_043() {
        super(Side.LIGHT, Title.Yavin_4_War_Room, Title.Yavin_4, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_8_1, Rarity.V);
        setVirtualSuffix(true);
        setLocationLightSideGameText("Once per game, may deploy Rebel Aces from Reserve Deck (reshuffle) and/or exchange a card in hand with top card of Force Pile.");
        setLocationDarkSideGameText("You lose no more than 2 Force from Force drains at non-liberated systems.");
        setGameText("Light: Once per game, may deploy Rebel Aces from Reserve Deck (reshuffle) and/or exchange a card in hand with top card of Force Pile. Dark: You lose no more than 2 Force from Force drains at non-liberated systems.");
        addIcon(Icon.LIGHT_FORCE, 2);
        addIcons(Icon.INTERIOR_SITE, Icon.PLANET, Icon.SCOMP_LINK);
        addKeywords(Keyword.WAR_ROOM);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextLightSideTopLevelActions(final String playerOnLightSideOfLocation, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        final String playerId = playerOnLightSideOfLocation;
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__MASSASSI_WAR_ROOM_V__DEPLOY_REBEL_ACES_OR_EXCHANGE;
        final Filter rebelAces = Filters.title("Rebel Aces");

        // Check condition(s)
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)) {
            final boolean canDeploy = GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId);
            final boolean canExchange = GameConditions.hasHand(game, playerId) && GameConditions.hasForcePile(game, playerId);
            if (canDeploy || canExchange) {
                final List<String> choices = new ArrayList<String>();
                if (canDeploy)
                    choices.add(DEPLOY);
                if (canExchange)
                    choices.add(EXCHANGE);
                if (canDeploy && canExchange)
                    choices.add(BOTH);

                final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
                action.setText("Deploy Rebel Aces and/or exchange card");
                // Update usage limit(s)
                action.appendUsage(
                        new OncePerGameEffect(action));
                // Perform result(s)
                action.appendEffect(
                        new PlayoutDecisionEffect(action, playerId,
                                new MultipleChoiceAwaitingDecision("Choose", choices.toArray(new String[0])) {
                                    @Override
                                    protected void validDecisionMade(int index, String result) {
                                        if (DEPLOY.equals(result) || BOTH.equals(result)) {
                                            action.appendEffect(
                                                    new DeployCardFromReserveDeckEffect(action, rebelAces, true));
                                        }
                                        if (EXCHANGE.equals(result) || BOTH.equals(result)) {
                                            action.appendEffect(
                                                    new ExchangeCardInHandWithTopCardOfForcePileEffect(action, playerId));
                                        }
                                    }
                                }));
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    @Override
    protected List<Modifier> getGameTextDarkSideWhileActiveModifiers(String playerOnDarkSideOfLocation, SwccgGame game, PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new LimitForceLossFromForceDrainModifier(self, Filters.and(Filters.system, Filters.not(Filters.liberated_system)), 2, playerOnDarkSideOfLocation));
        return modifiers;
    }
}
