package com.gempukku.swccgo.cards.set1262.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.PlayStarshipOrVehicleSimultaneouslyWithPilotOrPassengerAction;
import com.gempukku.swccgo.cards.effects.RevealCardFromOwnHandEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.StackActionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.LandedResult;

import java.util.List;

/** Legacy Block 6.2: Deployment Orders. */
public class Card1262_006 extends AbstractNormalEffect {
    private static final Filter PRESENCE_DROID = Filters.and(Filters.droid, Icon.PRESENCE);
    private static final Filter DROID_PILOT = Filters.and(PRESENCE_DROID, Filters.pilot);
    private static final Filter TRANSPORT = Filters.or(Filters.and(Icon.THEED_PALACE, Filters.vehicle), Filters.Landing_Craft);

    public Card1262_006() {
        super(Side.DARK, 4, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Deployment Orders", Uniqueness.UNIQUE,
                ExpansionSet.VIRTUAL_BLOCK_6_2, Rarity.V);
        setLore("The amount of time it takes for a sub-command to transfer to an officer battle droid, and then to the rest of his regiment, is .396 of a second.");
        setGameText("Deploy on table. If your Landing Craft just landed, you may use 1 Force to deploy a [Presence] droid or Neimoidian aboard from Reserve Deck; reshuffle. Once per turn, may use 1 Force: reveal a [Theed Palace] vehicle (or Landing Craft) from hand to take a [Presence] droid pilot into hand from Reserve Deck (or vice versa), and deploy both simultaneously; reshuffle. (Immune to Alter.)");
        addIcons(Icon.THEED_PALACE, Icon.EPISODE_I, Icon.LEGACY_BLOCK_6);
        addImmuneToCardTitle(Title.Alter);
        setAsLegacy(true);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
                                                                                  EffectResult result, PhysicalCard self,
                                                                                  int sourceId) {
        var id = GameTextActionId.LEGACY__DEPLOYMENT_ORDERS__DEPLOY_ABOARD_LANDING_CRAFT;
        Filter craft = Filters.and(Filters.your(self), Filters.Landing_Craft, Filters.hasAnyAvailablePilotOrPassengerCapacity);
        if (!TriggerConditions.justLandedAt(game, result, playerId, craft, Filters.site)
                || !GameConditions.canUseForce(game, playerId, 1)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id)) return null;

        var landed = Filters.filter(((LandedResult) result).getMovedCards(), game, craft);
        var action = new OptionalGameTextTriggerAction(self, sourceId, id);
        action.setText("Deploy a Presence droid or Neimoidian aboard the Landing Craft");
        action.appendCost(new UseForceEffect(action, playerId, 1));
        action.appendEffect(new DeployCardToTargetFromReserveDeckEffect(action,
                Filters.or(PRESENCE_DROID, Filters.Neimoidian), Filters.in(landed), true));
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
                                                                    PhysicalCard self, int sourceId) {
        var id = GameTextActionId.LEGACY__DEPLOYMENT_ORDERS__REVEAL_AND_DEPLOY_PAIR;
        Filter reveal = Filters.or(TRANSPORT, DROID_PILOT);
        if (!GameConditions.isOncePerTurn(game, self, playerId, sourceId, id)
                || !GameConditions.canUseForce(game, playerId, 1)
                || !GameConditions.hasInHand(game, playerId, reveal)
                || !GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, id)) return null;

        var action = new TopLevelGameTextAction(self, sourceId, id);
        action.setText("Reveal a transport or droid pilot and deploy the pair");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendCost(new UseForceEffect(action, playerId, 1));
        action.appendEffect(new ChooseCardFromHandEffect(action, playerId, reveal) {
            @Override
            protected void cardSelected(SwccgGame game, PhysicalCard selected) {
                action.appendEffect(new RevealCardFromOwnHandEffect(action, playerId, selected) {
                    @Override
                    protected void cardRevealed(PhysicalCard revealed) {
                        takePartner(action, playerId, game, self, revealed);
                    }
                });
            }
        });
        return List.of(action);
    }

    private void takePartner(Action action, String playerId, SwccgGame game, PhysicalCard self, PhysicalCard revealed) {
        boolean revealedTransport = TRANSPORT.accepts(game, revealed);
        Filter category = revealedTransport ? DROID_PILOT : TRANSPORT;
        Filter compatible = new Filter() {
            @Override
            public boolean accepts(GameState state, ModifiersQuerying querying, PhysicalCard partner) {
                var transport = revealedTransport ? revealed : partner;
                var droid = revealedTransport ? partner : revealed;
                var destinations = transport.getBlueprint().getValidDeployTargetWithPilotOrPassengerFilter(
                        playerId, state.getGame(), transport, self, false, 0, droid, false, 0, null, null);
                return GameConditions.canSpot(state.getGame(), self, destinations);
            }
        };
        action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(category, compatible), true) {
            @Override
            protected void cardTakenIntoHand(PhysicalCard partner) {
                var transport = revealedTransport ? revealed : partner;
                var droid = revealedTransport ? partner : revealed;
                action.appendEffect(new StackActionEffect(action, new PlayStarshipOrVehicleSimultaneouslyWithPilotOrPassengerAction(
                        game, self, transport, false, 0, Filters.any, droid, false, 0, null)));
            }
        });
    }
}
