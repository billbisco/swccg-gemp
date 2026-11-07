package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractEpicEventDeployable;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.game.state.GameState;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.DeployStackedCardEffect;
import com.gempukku.swccgo.logic.effects.choose.StackCardsFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.StackOneCardFromLostPileEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeDestinyCardIntoHandEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotDeployModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.querying.ModifiersQuerying;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;
import com.gempukku.swccgo.logic.timing.results.LostFromTableResult;

import java.util.ArrayList;
import java.util.List;

/** Rise Of The Dark Lord, Virtual Block 7.1. */
public class Card1271_019 extends AbstractEpicEventDeployable {
    private static final Filter VADER_EFFECT = Filters.and(Icon.VADER, Filters.Effect);
    private static final Filter CANCELED = Filters.or(Filters.title(Title.Dark_Jedi_Presence), Filters.title(Title.Jedi_Presence),
        Filters.title(Title.Revenge_Of_The_Sith),
        Filters.and(Side.DARK, Filters.Jedi_Test));

    public Card1271_019() {
        super(Side.DARK, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Rise Of The Dark Lord", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setGameText("Deploy on table (only at start of game) with three different [Vader] Effects from Reserve Deck face down under here. Once per turn, may deploy a [Vader] Effect from here (never from elsewhere). Limit one [Vader] Effect on table and, when lost, stack it under here. You may cancel your [Vader] Effect at any time. Twisted And Evil: Dark Jedi Presence, Jedi Presence, Revenge Of The Sith and Sith Tests are canceled. When using The Empire's Back to deploy Vader, it is a Used Interrupt. If you just drew a [Vader] card for destiny, may take it into hand.");
        addIcons(Icon.SETUP, Icon.CLOUD_CITY);
    }

    @Override
    protected boolean checkGameTextDeployRequirements(String playerId, SwccgGame game, PhysicalCard self, PlayCardOptionId option, boolean asReact) {
        return game.getGameState().getCurrentPhase() == Phase.PLAY_STARTING_CARDS
            && game.getGameState().getReserveDeck(playerId).stream().filter(card -> VADER_EFFECT.accepts(game, card))
                .map(PhysicalCard::getTitle).distinct().limit(3).count() == 3;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        return List.of(new MayNotDeployModifier(self, Filters.and(VADER_EFFECT, Filters.not(Filters.stackedOn(self))), self.getOwner()),
            new MayNotDeployModifier(self, VADER_EFFECT, (state, modifiers) -> GameConditions.canSpot(game, self, Filters.and(VADER_EFFECT, Filters.your(self))), self.getOwner()));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isPlayingCard(game, effect, CANCELED)
                || !GameConditions.canCancelCardBeingPlayed(game, self, effect)) return null;

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        List<RequiredGameTextTriggerAction> actions = new ArrayList<>();
        if (TriggerConditions.justDeployed(game, result, self)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
            action.setText("Stack three different Vader Effects face down");
            Filter notAlreadySelected = new Filter() {
                @Override public boolean accepts(GameState state, ModifiersQuerying modifiers, PhysicalCard card) {
                    return state.getStackedCards(self).stream().noneMatch(stacked -> stacked.getTitle().equals(card.getTitle()));
                }
            };
            for (int count = 0; count < 3; count++) {
                action.appendEffect(new StackCardsFromReserveDeckEffect(action, self.getOwner(), 1, 1, self, true,
                    Filters.and(VADER_EFFECT, notAlreadySelected)));
            }
            actions.add(action);
        }
        if (TriggerConditions.justLost(game, result, Filters.and(VADER_EFFECT, Filters.your(self)))) {
            PhysicalCard lost = ((LostFromTableResult) result).getCard();
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
            action.setText("Stack lost Vader Effect under Rise Of The Dark Lord");
            action.appendEffect(new StackOneCardFromLostPileEffect(action, lost, self, true, false, true));
            actions.add(action);
        }
        if (TriggerConditions.isTableChanged(game, result) && GameConditions.canTargetToCancel(game, self, CANCELED)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
            CancelCardActionBuilder.buildCancelCardAction(action, CANCELED, "card canceled by Rise Of The Dark Lord");
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game,
            EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isDestinyJustDrawnBy(game, result, playerId)
                || !GameConditions.canTakeDestinyCardIntoHand(game, playerId)) return null;
        PhysicalCard drawn = ((DestinyDrawnResult) result).getCard();
        if (drawn == null || !Filters.and(Icon.VADER).accepts(game, drawn)) return null;

        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, playerId, sourceId);
        action.setText("Take Vader destiny card into hand");
        action.appendEffect(new TakeDestinyCardIntoHandEffect(action));
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        List<TopLevelGameTextAction> actions = new ArrayList<>();
        if (GameConditions.isOncePerTurn(game, self, playerId, sourceId)
                && GameConditions.hasStackedCards(game, self, Filters.and(VADER_EFFECT, Filters.deployable(self, null, false, 0)))) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, sourceId);
            action.setText("Deploy a Vader Effect from here");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendEffect(new DeployStackedCardEffect(action, self, VADER_EFFECT, false));
            actions.add(action);
        }
        Filter yourEquipment = Filters.and(VADER_EFFECT, Filters.your(self));
        if (GameConditions.canTargetToCancel(game, self, yourEquipment)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, sourceId);
            CancelCardActionBuilder.buildCancelCardAction(action, yourEquipment, "your Vader Effect");
            actions.add(action);
        }
        return actions;
    }
}
