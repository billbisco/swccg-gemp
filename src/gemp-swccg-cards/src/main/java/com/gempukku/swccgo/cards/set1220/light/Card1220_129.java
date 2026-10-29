package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerPhaseEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Persona;
import com.gempukku.swccgo.common.Phase;
import com.gempukku.swccgo.common.PlayCardZoneOption;
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
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.LoseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.modifiers.PowerModifier;
import com.gempukku.swccgo.logic.timing.Effect;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: Nick Of Time
 */
public class Card1220_129 extends AbstractNormalEffect {
    public Card1220_129() {
        super(Side.LIGHT, 2, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Nick_Of_Time, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("Luke barely escaped being crushed by the AT-AT's massive footpad during the Battle of Hoth.");
        setGameText("Deploy on table. Dack Ralter and Wes Janson add 2 to power of anything either one pilots. During your control phase, may take one Attack Pattern Delta, Desperate Tactics, Lucky Shot, One More Pass, Rapid Fire, or a vehicle weapon into hand from Reserve Deck; reshuffle. May lose 2 Force to cancel Trample or Walker Barrage. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter dackOrWes = Filters.or(Filters.persona(Persona.DACK), Filters.persona(Persona.WES));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new PowerModifier(self, Filters.hasPiloting(self, dackOrWes), 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId takeId = GameTextActionId.LEGACY__NICK_OF_TIME_V__TAKE_CARD;
        Filter takeFilter = Filters.or(Filters.title(Title.Attack_Pattern_Delta), Filters.title(Title.Desperate_Tactics),
                Filters.title(Title.Lucky_Shot), Filters.title(Title.One_More_Pass), Filters.title(Title.Rapid_Fire), Filters.vehicle_weapon);
        Filter cancelFilter = Filters.or(Filters.title(Title.Trample), Filters.title(Title.Walker_Barrage));

        if (GameConditions.isOnceDuringYourPhase(game, self, playerId, gameTextSourceCardId, takeId, Phase.CONTROL)
                && GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, takeId)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, takeId);
            action.setText("Take card into hand from Reserve Deck");
            action.setActionMsg("Take Attack Pattern Delta, Desperate Tactics, Lucky Shot, One More Pass, Rapid Fire, or a vehicle weapon into hand from Reserve Deck");
            action.appendUsage(
                    new OncePerPhaseEffect(action));
            action.appendEffect(
                    new TakeCardIntoHandFromReserveDeckEffect(action, playerId, takeFilter, true));
            actions.add(action);
        }

        if (GameConditions.canTargetToCancel(game, self, cancelFilter)) {
            TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId);
            action.setText("Cancel Trample or Walker Barrage");
            action.appendCost(
                    new LoseForceEffect(action, playerId, 2));
            CancelCardActionBuilder.buildCancelCardAction(action, cancelFilter, "Trample or Walker Barrage");
            actions.add(action);
        }
        return actions;
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalBeforeTriggers(final String playerId, SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        Filter cancelFilter = Filters.or(Filters.title(Title.Trample), Filters.title(Title.Walker_Barrage));
        if (TriggerConditions.isPlayingCard(game, effect, cancelFilter)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId);
            action.appendCost(
                    new LoseForceEffect(action, playerId, 2));
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }
}
