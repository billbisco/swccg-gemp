package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractImmediateEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.CancelForceDrainEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayCardAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInLostPileFromTableEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotTargetToBePlacedOutOfPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.Collections;
import java.util.List;

/** Return Of The Jedi (new title printed over Our Only Hope), Legacy Block 6.1. */
public class Card1261_085 extends AbstractImmediateEffect {
    private static final Filter CHARACTER_HOST = Filters.or(
            Filters.and(Filters.Sidious, Filters.titleContains("Sidious")),
            Filters.and(Icon.LEGACY_BLOCK_3, Filters.Emperor));

    public Card1261_085() {
        super(Side.LIGHT, 4, PlayCardZoneOption.ATTACHED, Title.Return_Of_The_Jedi, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setLore("'The Emperor knew, as I did, if Anakin were to have any offspring, they would be a threat to him.'");
        setGameText("Deploy on a site to cancel a Force drain there. Skywalkers are power +1 here. At end of your turn If you control this site, place this Effect in Lost Pile. OR At any time, deploy on Sidious or [Virtual Block 3] Emperor. Opponent's Interrupts with “Emperor” in game text are canceled and Sidious may not place Jedi out of play. [Immune to Control]");
        addIcons(Icon.DEATH_STAR_II);
        addImmuneToCardTitle(Title.Control);
    }

    @Override
    protected boolean canPlayCardDuringCurrentPhase(String playerId, SwccgGame game, PhysicalCard self) {
        return game.getGameState().getCurrentPhase() != Phase.PLAY_STARTING_CARDS;
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId option, boolean asReact) {
        if (GameConditions.canCancelForceDrain(game, self)) {
            PhysicalCard drainingAt = game.getGameState().getForceDrainState().getLocation();
            return Filters.or(CHARACTER_HOST, Filters.and(Filters.site, drainingAt));
        }
        return CHARACTER_HOST;
    }

    @Override
    protected Filter getGameTextValidTargetFilterToRemainAttachedTo(SwccgGame game, PhysicalCard self) {
        return Filters.or(Filters.site, CHARACTER_HOST);
    }

    @Override
    protected List<PlayCardAction> getGameTextOptionalAfterActions(String playerId, SwccgGame game, EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.forceDrainInitiatedAt(game, result, Filters.site)
                && GameConditions.canCancelForceDrain(game, playerId, self)) {
            PhysicalCard site = game.getGameState().getForceDrainState().getLocation();
            PlayCardAction action = getPlayCardAction(playerId, game, self, self, false, 0, null, null, null, null, null, false, 0,
                    Filters.and(site), null);
            if (action != null) {
                action.setText("Deploy to cancel the Force drain");
                return Collections.singletonList(action);
            }
        }
        return null;
    }

    private boolean onSite(SwccgGame game, PhysicalCard self) {
        return self.getAttachedTo() != null && Filters.site.accepts(game, self.getAttachedTo());
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        if (onSite(game, self)) {
            return Collections.singletonList(new PowerModifier(self, Filters.and(Filters.Skywalker, Filters.here(self)), 1));
        }
        return Collections.singletonList(new MayNotTargetToBePlacedOutOfPlayModifier(self, Filters.Jedi, Filters.Sidious));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (onSite(game, self) && TriggerConditions.justDeployed(game, result, self)
                && GameConditions.canCancelForceDrain(game, self)
                && self.getAttachedTo().equals(game.getGameState().getForceDrainState().getLocation())) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Cancel the Force drain here");
            action.appendEffect(new CancelForceDrainEffect(action));
            return Collections.singletonList(action);
        }
        if (onSite(game, self) && TriggerConditions.isEndOfYourTurn(game, result, self)
                && isLostAtEndOfTurn(game, self, self.getAttachedTo())) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place Return Of The Jedi in Lost Pile");
            action.appendEffect(new PlaceCardInLostPileFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int gameTextSourceCardId) {
        Filter emperorInterrupt = Filters.and(Filters.opponents(self), Filters.Interrupt, Filters.gameTextContains("Emperor"));
        if (!onSite(game, self) && TriggerConditions.isPlayingCard(game, effect, emperorInterrupt)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    /**
     * "At end of your turn If you control this site, place this Effect in Lost Pile" (Competitive Balance Team errata,
     * PC forum 2014-04-16, which also made the card unique). The original (2261_85) is lost at the end of every turn of yours.
     */
    protected boolean isLostAtEndOfTurn(SwccgGame game, PhysicalCard self, PhysicalCard site) {
        return GameConditions.controls(game, self.getOwner(), site);
    }
}
