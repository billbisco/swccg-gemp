package com.gempukku.swccgo.cards.set1117.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.conditions.CardsInHandEqualToOrFewerThanCondition;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
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
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ImmuneToTitleModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.Effect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Set 17: The Force Unleashed
 * Type: Effect
 * Title: A Jedi's Plans
 */
public class Card1117_002 extends AbstractNormalEffect {
    public Card1117_002() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "A Jedi's Plans", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_ORIGINAL_SET_17, Rarity.V);
        setVirtualSuffix(true);
        setLore("");
        setGameText("Deploy on a site. Nightfall is canceled. While you have 8 or fewer cards in hand, non-unique characters (except clones), vehicles, starships, weapons, and devices are immune to Monnok. Once per turn, may use 2 Force to deploy Jedi Archives or a battleground planet location (except a sector) not on table from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new ImmuneToTitleModifier(self, Filters.and(Filters.non_unique, Filters.or(Filters.and(Filters.character, Filters.except(Filters.clone)), Filters.vehicle, Filters.starship, Filters.weapon, Filters.device)),
                new CardsInHandEqualToOrFewerThanCondition(self.getOwner(), 8), Title.Monnok));
        return modifiers;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(final SwccgGame game, Effect effect, final PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isPlayingCard(game, effect, Filters.Nightfall)
                && GameConditions.canCancelCardBeingPlayed(game, self, effect)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, PhysicalCard self, int gameTextSourceCardId) {
        if (TriggerConditions.isTableChanged(game, effectResult)
                && GameConditions.canTargetToCancel(game, self, Filters.Nightfall)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            CancelCardActionBuilder.buildCancelCardAction(action, Filters.Nightfall, Title.Nightfall);
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, final SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.OVS__A_JEDIS_PLANS__DEPLOY_LOCATION;
        Filter notOnTable = Filters.not(Filters.sameTitleAs(self, Filters.onTable));
        Filter location = Filters.and(notOnTable, Filters.or(
                Filters.title("Coruscant: Jedi Archives"),
                Filters.and(Filters.not(Filters.sector), Filters.planet_location, Filters.battleground)));

        if (GameConditions.isOncePerTurn(game, self, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 2)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy a location");
            action.appendUsage(new OncePerTurnEffect(action));
            action.appendCost(new UseForceEffect(action, playerId, 2));
            action.appendEffect(new DeployCardFromReserveDeckEffect(action, location, true));
            return Collections.singletonList(action);
        }
        return null;
    }
}
