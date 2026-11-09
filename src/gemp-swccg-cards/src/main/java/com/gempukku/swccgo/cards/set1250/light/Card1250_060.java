package com.gempukku.swccgo.cards.set1250.light;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.PlayCardOptionId;
import com.gempukku.swccgo.common.PlayCardZoneOption;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.TargetingReason;
import com.gempukku.swccgo.common.Title;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.CancelCardsOnTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.TakeCardIntoHandFromReserveDeckEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.List;

/** Legacy Block 5 An Unusual Amount Of Fear (V), printing 60. */
public class Card1250_060 extends AbstractNormalEffect {
    public Card1250_060() {
        super(Side.LIGHT, 0, PlayCardZoneOption.ATTACHED, "An Unusual Amount Of Fear", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_5, Rarity.V);
        setVirtualSuffix(true);
        setLore("The peacekeepers of the galaxy are not to be taken lightly.");
        setGameText("Deploy on Jedi Council Chamber or Yoda's Hut. Once per turn, may use 1 Force to take a card with 'A Jedi's' in title (except A Jedi's Resilience) into hand from Reserve Deck; reshuffle. [Virtual Block 5] Wipe Them Out, All Of Them and opponent's [Sidious] Effects here are cancelled. (Immune to Alter.)");
        addIcons(Icon.REFLECTIONS_III, Icon.EPISODE_I);
        addImmuneToCardTitle(Title.Alter);
    }

    @Override
    protected Filter getGameTextValidDeployTargetFilter(SwccgGame game, PhysicalCard self, PlayCardOptionId option, boolean asReact) {
        return Filters.or(Filters.Jedi_Council_Chamber, Filters.Yodas_Hut);
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult result,
            PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.isTableChanged(game, result)) return null;

        Filter wipe = Filters.and(Icon.LEGACY_BLOCK_5, Filters.title("Wipe Them Out, All Of Them"));
        Filter sidiousHere = Filters.and(Filters.opponents(self), Filters.Effect, Icon.SIDIOUS, Filters.here(self));
        var targets = Filters.filterActive(game, self,
                Filters.and(Filters.or(wipe, sidiousHere), Filters.canBeTargetedBy(self, TargetingReason.TO_BE_CANCELED)));
        if (targets.isEmpty()) return null;
        RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
        action.setSingletonTrigger(true);
        action.setText("Cancel Wipe Them Out or opposing Sidious Effects here");
        action.appendEffect(new CancelCardsOnTableEffect(action, targets));
        return List.of(action);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game,
            PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId id = GameTextActionId.OVS__UNUSUAL_FEAR__TAKE_A_JEDIS_CARD;
        if (!GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, id)
                || !GameConditions.canTakeCardsIntoHandFromReserveDeck(game, playerId, self, id)
                || !GameConditions.canUseForce(game, playerId, 1)) return null;

        Filter eligible = Filters.and(Filters.titleContains("A Jedi's"), Filters.not(Filters.title("A Jedi's Resilience")));
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, playerId, gameTextSourceCardId, id);
        action.setText("Take an A Jedi's card into hand");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendCost(new UseForceEffect(action, playerId, 1));
        action.appendEffect(new TakeCardIntoHandFromReserveDeckEffect(action, playerId, eligible, true));
        return List.of(action);
    }
}
