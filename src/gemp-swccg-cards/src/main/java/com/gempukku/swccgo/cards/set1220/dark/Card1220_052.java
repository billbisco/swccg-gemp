package com.gempukku.swccgo.cards.set1220.dark;

import com.gempukku.swccgo.cards.AbstractNormalEffect;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
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
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.RequiredGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.PlaceCardInUsedPileFromTableEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.MayNotPlayModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;
import com.gempukku.swccgo.logic.timing.EffectResult;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Effect
 * Title: We're The Bait
 */
public class Card1220_052 extends AbstractNormalEffect {
    public Card1220_052() {
        super(Side.DARK, 7, PlayCardZoneOption.YOUR_SIDE_OF_TABLE, Title.Were_The_Bait, Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("'He doesn't want you at all, he's after somebody called, uh...Skywalker.' 'Luke?' 'Lord Vader has set a trap for him.' 'And we're that bait!' 'Yeah, well, he's on his way.'");
        setGameText("Deploy on table. It's A Trap! may not be played. Once per game, may use 1 Force to deploy an Effect with 'Bespin' or 'Cloud City' in lore or game text from Reserve Deck; reshuffle. If Chewie, Han, Leia, or Luke wins a battle on Cloud City, place this Effect in Used Pile.");
        addIcons(Icon.CLOUD_CITY);
    }

    private Filter baitEffectFilter() {
        return Filters.and(Filters.Effect, Filters.or(
                Filters.loreContains("Bespin"), Filters.loreContains("Cloud City"),
                Filters.gameTextContains("Bespin"), Filters.gameTextContains("Cloud City")));
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new MayNotPlayModifier(self, Filters.Its_A_Trap));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__WERE_THE_BAIT_V__DEPLOY_EFFECT;
        if (GameConditions.isOncePerGame(game, self, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {
            final TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            action.setText("Deploy Effect from Reserve Deck");
            action.appendUsage(
                    new OncePerGameEffect(action));
            action.appendCost(
                    new UseForceEffect(action, playerId, 1));
            action.appendEffect(
                    new DeployCardFromReserveDeckEffect(action, baitEffectFilter(), true));
            return Collections.singletonList(action);
        }
        return null;
    }

    @Override
    protected List<RequiredGameTextTriggerAction> getGameTextRequiredAfterTriggers(SwccgGame game, EffectResult effectResult, final PhysicalCard self, int gameTextSourceCardId) {
        Filter winners = Filters.or(Filters.Chewie, Filters.Han, Filters.Leia, Filters.Luke);
        if (TriggerConditions.wonBattle(game, effectResult, winners)
                && GameConditions.isDuringBattleAt(game, Filters.Cloud_City_location)) {
            RequiredGameTextTriggerAction action = new RequiredGameTextTriggerAction(self, gameTextSourceCardId);
            action.setText("Place in Used Pile");
            action.appendEffect(
                    new PlaceCardInUsedPileFromTableEffect(action, self));
            return Collections.singletonList(action);
        }
        return null;
    }
}
