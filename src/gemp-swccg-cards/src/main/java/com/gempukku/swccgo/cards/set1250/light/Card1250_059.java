package com.gempukku.swccgo.cards.set1250.light;

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

import java.util.List;

/** Legacy Block 5 A Jedi's Plans, printing 59. */
public class Card1250_059 extends AbstractNormalEffect {
    public Card1250_059() {
        super(Side.LIGHT, 3, PlayCardZoneOption.ATTACHED, "A Jedi's Plans", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setLore("The guidance of experienced Jedi allowed Luke to confront Vader.");
        setGameText("Deploy on a site. Nightfall is canceled. While you have 8 or fewer cards in hand, non-unique characters (except clones), vehicles, starships, weapons, and devices are immune to Monnok. Once per turn, may use 2 Force to deploy Jedi Archives or a battleground planet location (except a sector) not on table from Reserve Deck; reshuffle. (Immune to Alter.)");
        addIcons(Icon.SPECIAL_EDITION);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self,
            PlayCardOptionId playCardOptionId, boolean asReact) {
        return Filters.site;
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, PhysicalCard self) {
        Filter protectedCards = Filters.and(Filters.non_unique,
                Filters.or(Filters.and(Filters.character, Filters.not(Filters.clone)),
                        Filters.vehicle, Filters.starship, Filters.weapon, Filters.device));
        return List.of(new ImmuneToTitleModifier(self, protectedCards,
                new CardsInHandEqualToOrFewerThanCondition(self.getOwner(), 8), Title.Monnok));
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredBeforeTriggers(SwccgGame game, Effect effect,
            PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.isPlayingCard(game, effect, Filters.Nightfall)
                || !GameConditions.canCancelCardBeingPlayed(game, self, effect)) return null;

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        CancelCardActionBuilder.buildCancelCardBeingPlayedAction(action, effect);
        return List.of(action);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
            PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.isTableChanged(game, result)
                || !GameConditions.canTargetToCancel(game, self, Filters.Nightfall)) return null;

        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        CancelCardActionBuilder.buildCancelCardAction(action, Filters.Nightfall, Title.Nightfall);
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId id = GameTextActionId.LEGACY__A_JEDIS_PLANS__DEPLOY_LOCATION;
        if (!GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, id)
                || !GameConditions.canDeployCardFromReserveDeck(game, playerId, self, id, true, false)
                || !GameConditions.canUseForce(game, playerId, 2)) return null;

        Filter eligible = Filters.and(Filters.not(Filters.sameTitleAs(self, Filters.onTable)),
                Filters.canBeTargetedBy(self), Filters.or(Filters.title("Coruscant: Jedi Archives"),
                        Filters.and(Filters.planet_location, Filters.battleground, Filters.not(Filters.sector))));
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, id);
        action.setText("Deploy Jedi Archives or a battleground planet location");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendCost(new UseForceEffect(action, playerId, 2));
        action.appendEffect(new DeployCardFromReserveDeckEffect(action, eligible, true));
        return List.of(action);
    }
}
