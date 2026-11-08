package com.gempukku.swccgo.cards.set1261.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.TitleComponent;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.GameUtils;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.effects.choose.DrawCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.NotHitWhenThrownModifier;
import com.gempukku.swccgo.logic.modifiers.MayNotBeSuspendedModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.ShowCardOnScreenEffect;
import com.gempukku.swccgo.logic.effects.choose.ChooseCardFromHandEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckSimultaneouslyWithCardEffect;

import java.util.Collections;
import java.util.List;

/** Maneuvering Flaps & Nick Of Time, final Legacy Light master p26. */
public class Card1261_082 extends AbstractNormalEffect {
    private static final Filter UNPILOTED_COMBAT_VEHICLE = Filters.and(Filters.unpiloted, Filters.combat_vehicle);
    private static final Filter PILOT_CHARACTER = Filters.and(Filters.character, Filters.pilot);
    private static final Filter DRAW_INTERRUPTS = Filters.or(Filters.title(Title.Desperate_Tactics), Filters.title(Title.Lucky_Shot),
            Filters.title(Title.On_Target), Filters.title(Title.Rapid_Fire), Filters.title("Steady Aim"));

    public Card1261_082() {
        super(Side.LIGHT, 1, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, "Maneuvering Flaps & Nick Of Time",
                Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_1, Rarity.V);
        setTitleComponents(new TitleComponent(Title.Maneuvering_Flaps, Uniqueness.UNIQUE),
                new TitleComponent("Nick Of Time", Uniqueness.UNIQUE));
        setGameText("Deploy on table. Your vehicles with a pilot character or gunner aboard are not hit when 'thrown.' During your deploy phase, may reveal an unpiloted combat vehicle from hand to take its matching pilot character (or vice versa) from Reserve Deck and deploy both simultaneously; reshuffle. Whenever you play Desperate Tactics, Lucky Shot, On Target, Rapid Fire, or Steady Aim, may draw top card from Reserve Deck. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        Filter filter = Filters.and(Filters.or(PILOT_CHARACTER, UNPILOTED_COMBAT_VEHICLE), Filters.isUniquenessOnTableNotReached);

        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__FLAPS_NICK_OF_TIME__DEPLOY_MATCHING;

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, gameTextActionId, Phase.DEPLOY)
                && GameConditions.hasInHand(game, playerId, filter)
                && GameConditions.canSearchReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Reveal pilot or unpiloted combat vehicle from hand");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendTargeting(
                    new ChooseCardFromHandEffect(action, playerId, filter) {
                        @Override
                        protected void cardSelected(SwccgGame game, final PhysicalCard selectedCard) {
                            final Filter searchFilter;
                            if (Filters.character.accepts(game, selectedCard)) {
                                action.setActionMsg("Take " + GameUtils.getCardLink(selectedCard) + "'s matching unpiloted combat vehicle from Reserve Deck and deploy both simultaneously");
                                searchFilter = Filters.and(UNPILOTED_COMBAT_VEHICLE, Filters.matchingVehicle(selectedCard));
                            }
                            else {
                                action.setActionMsg("Take " + GameUtils.getCardLink(selectedCard) + "'s matching pilot from Reserve Deck and deploy both simultaneously");
                                searchFilter = Filters.and(PILOT_CHARACTER, Filters.matchingPilot(selectedCard));
                            }
                            action.appendEffect(
                                    new ShowCardOnScreenEffect(action, selectedCard));
                            action.appendEffect(
                                    new DeployCardFromReserveDeckSimultaneouslyWithCardEffect(action, selectedCard, searchFilter, true));
                        }
                    });
            return Collections.singletonList(action);
        }
        return null;
    }

    /** The PC card list text (PC-E1, the latest) has no "May not be suspended." (2261_82, the sheet text, has it). */
    protected boolean mayNotBeSuspended() {
        return false;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter yourVehiclesWithPilotOrGunnerAboard = Filters.and(Filters.your(self), Filters.vehicle,
                Filters.hasAboard(self, Filters.or(PILOT_CHARACTER, Filters.gunner)));
        if (!mayNotBeSuspended())
            return List.of(new NotHitWhenThrownModifier(self, yourVehiclesWithPilotOrGunnerAboard));
        return List.of(new NotHitWhenThrownModifier(self, yourVehiclesWithPilotOrGunnerAboard),
                new MayNotBeSuspendedModifier(self));
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(
            String playerId, SwccgGame game, Effect effect, PhysicalCard self, int sourceId) {
        if (!TriggerConditions.isPlayingCard(game, effect, playerId, DRAW_INTERRUPTS)
                || !GameConditions.hasReserveDeck(game, playerId)) return List.of();
        var action = new OptionalGameTextTriggerAction(self, sourceId);
        action.setText("Draw top card of Reserve Deck");
        action.appendEffect(new DrawCardIntoHandFromReserveDeckEffect(action, playerId));
        return List.of(action);
    }
}
