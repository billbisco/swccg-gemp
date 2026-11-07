package com.gempukku.swccgo.cards.set1261.dark;

import com.gempukku.swccgo.cards.AbstractSite;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.actions.MoveUsingLocationTextAction;
import com.gempukku.swccgo.cards.conditions.OnTableCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.CancelCardActionBuilder;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.IconModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Legacy Block 6.1 Ice Plains, retaining its separately printed fifth-marker designation. */
public class Card1261_052 extends AbstractSite {
    public Card1261_052() {
        super(Side.DARK, Title.Ice_Plains, Title.Hoth, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setVirtualSuffix(true);
        setLocationDarkSideGameText("Once per game, may take a non-[Maintenance] AT-AT into hand from Reserve Deck; reshuffle. During your move phase, may move for free between here and Mountains.");
        setLocationLightSideGameText("If Imperial Control on table, add one [Light Side Force] here and at Mountains. Ice Storm, Rebel Base Occupation, and opponent's Colo Claw Fish are canceled.");
        addIcon(Icon.DARK_FORCE, 2);
        addIcons(Icon.EXTERIOR_SITE, Icon.PLANET, Icon.HOTH);
        addKeywords(Keyword.MARKER_5);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextDarkSideTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int sourceId) {
        List<TopLevelGameTextAction> actions = new ArrayList<>();
        GameTextActionId id = GameTextActionId.LEGACY__HOTH_ICE_PLAINS__TAKE_AT_AT;
        if (GameConditions.isOncePerGame(game, self, id)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, id)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, sourceId, id);
            action.setText("Take non-maintenance AT-AT into hand");
            action.appendUsage(new OncePerGameEffect(action));
            action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId,
                    Filters.and(Filters.AT_AT, Filters.not(Icon.MAINTENANCE)), true));
            actions.add(action);
        }
        if (GameConditions.isDuringYourPhase(game, playerId, Phase.MOVE) && GameConditions.canSpotLocation(game, Filters.Mountains)) {
            if (GameConditions.canPerformMovementUsingLocationText(playerId, game, SpotOverride.INCLUDE_UNDERCOVER, Filters.any, self, Filters.Mountains, true)) {
                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerId, game, self, sourceId,
                        SpotOverride.INCLUDE_UNDERCOVER, Filters.any, self, Filters.Mountains, true);
                action.setText("Move from here to Mountains"); actions.add(action);
            }
            if (GameConditions.canPerformMovementUsingLocationText(playerId, game, SpotOverride.INCLUDE_UNDERCOVER, Filters.any, Filters.Mountains, self, true)) {
                MoveUsingLocationTextAction action = new MoveUsingLocationTextAction(playerId, game, self, sourceId,
                        SpotOverride.INCLUDE_UNDERCOVER, Filters.any, Filters.Mountains, self, true);
                action.setText("Move from Mountains to here"); actions.add(action);
            }
        }
        return actions;
    }

    @Override
    protected List<Modifier> getGameTextLightSideWhileActiveModifiers(String playerId, SwccgGame game, PhysicalCard self) {
        return Collections.singletonList(new IconModifier(self, Filters.or(self, Filters.Mountains),
                new OnTableCondition(self, Filters.title(Title.Imperial_Control)), Icon.LIGHT_FORCE));
    }

    private Filter canceled(String lightPlayer) {
        return Filters.or(Filters.Ice_Storm, Filters.Rebel_Base_Occupation,
                Filters.and(Filters.opponents(lightPlayer), Filters.Colo_Claw_Fish));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredBeforeTriggers(String playerId, SwccgGame game, Effect effect, PhysicalCard self, int sourceId) {
        if (TriggerConditions.isPlayingCard(game, effect, canceled(playerId)) && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextLightSideRequiredAfterTriggers(String playerId, SwccgGame game, EffectResult result, PhysicalCard self, int sourceId) {
        if (TriggerConditions.isTableChanged(game, result) && GameConditions.canTargetToCancel(game, self, canceled(playerId))) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, sourceId);
            CancelCardActionBuilder.buildCancelCardAction(action, canceled(playerId), "Ice Storm, Rebel Base Occupation, or opponent's Colo Claw Fish");
            return Collections.singletonList(action);
        }
        return null;
    }
}
