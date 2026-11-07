package com.gempukku.swccgo.cards.set1271.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.PlayInterruptAction;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.RespondablePlayCardEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.DeployCostToLocationModifier;
import com.gempukku.swccgo.logic.modifiers.LegacyMayPlayInterruptAsModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Action;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Imperial Security Bureau, Virtual Block 7.1. */
public class Card1271_012 extends AbstractNormalEffect {
    public Card1271_012() {
        super(Side.DARK, 1, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Imperial Security Bureau", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_7_1, Rarity.V);
        setGameText("Deploy on table. Sunsdown is canceled. ISB agents who are leaders deploy -2 to sites with 'ISB' in title. Once per game, may take a [Setup] Mission into hand from Reserve Deck; reshuffle. Look Sir, Droids and your Interrupts with 'advisor' in title may be played as follows: 'USED: Use 1 Force to take an ISB agent into hand from Reserve Deck; reshuffle.' (Immune to Alter.)");
        addIcons(Icon.CLOUD_CITY);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter eligibleInterrupt = Filters.and(Filters.Interrupt, Filters.or(Filters.Look_Sir_Droids,
            Filters.and(Filters.your(self), Filters.titleContains("advisor"))));
        return List.of(
            new DeployCostToLocationModifier(self, Filters.and(Filters.ISB_agent, Filters.leader), -2,
                Filters.and(Filters.site, Filters.titleContains("ISB"))),
            new LegacyMayPlayInterruptAsModifier(self, eligibleInterrupt, CardSubtype.USED, new LegacyMayPlayInterruptAsModifier.PlayMode() {
                @Override public PlayInterruptAction createAction(String playerId, SwccgGame game, PhysicalCard source, PhysicalCard interrupt) {
                    return takeAgent(playerId, game, interrupt);
                }
            }));
    }

    private PlayInterruptAction takeAgent(String playerId, SwccgGame game, PhysicalCard interrupt) {
        GameTextActionId id = GameTextActionId.LEGACY__IMPERIAL_SECURITY_BUREAU__TAKE_ISB_AGENT;
        if (!GameConditions.canUseForceToPlayInterrupt(game, playerId, interrupt, 1)
                || !GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, interrupt, id)) return null;

        PlayInterruptAction action = new PlayInterruptAction(game, interrupt, id);
        action.setText("Take an ISB agent with Imperial Security Bureau");
        action.appendCost(new UseForceEffect(action, playerId, 1));
        action.allowResponses(new RespondablePlayCardEffect(action) {
            @Override protected void performActionResults(Action targetingAction) {
                action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.ISB_agent, true));
            }
        });
        return action;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        GameTextActionId id = GameTextActionId.LEGACY__IMPERIAL_SECURITY_BUREAU__TAKE_SETUP_MISSION;
        if (!GameConditions.isOncePerGame(game, self, id)
                || !GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, id)) return null;

        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, sourceId, id);
        action.setText("Take a Setup Mission into hand");
        action.appendUsage(new OncePerGameEffect(action));
        action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, Filters.and(CardCategory.MISSION, Icon.SETUP), true));
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isPlayingCard(game, effect, Filters.Sunsdown)
                || !GameConditions.canCancelCardBeingPlayed(game, self, effect)) return null;
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isTableChanged(game, result)
                || !GameConditions.canTargetToCancel(game, self, Filters.Sunsdown)) return null;
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
        CancelCardActionBuilder.buildCancelCardAction(action, Filters.Sunsdown, Title.Sunsdown);
        return List.of(action);
    }
}
