package com.gempukku.swccgo.cards.set1263.light;

import com.gempukku.swccgo.cards.AbstractRepublic;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerGameEffect;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.*;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.TriggerConditions;
import com.gempukku.swccgo.logic.actions.OptionalGameTextTriggerAction;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.decisions.IntegerAwaitingDecision;
import com.gempukku.swccgo.logic.effects.ChooseAndRearrangeRelatedSitesEffect;
import com.gempukku.swccgo.logic.effects.ModifyDestinyEffect;
import com.gempukku.swccgo.logic.effects.PlayoutDecisionEffect;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.timing.EffectResult;
import com.gempukku.swccgo.logic.timing.results.DestinyDrawnResult;

import java.util.List;

/** General Solomahal, Legacy Block 6.3, final master page 8. */
public class Card1263_022 extends AbstractRepublic {
    private static final GameTextActionId REARRANGE = GameTextActionId.LEGACY__GENERAL_SOLOMAHAL__REARRANGE_SITES;
    private static final GameTextActionId ADD_DESTINY = GameTextActionId.OTHER_CARD_ACTION_1;
    private static final Filter FLAGSHIP_SITES = Filters.siteOfStarshipOrVehicle(Persona.BLOCKADE_FLAGSHIP, false);
    private static final Filter BLOCK_FIVE_PALPATINE = Filters.and(Icon.LEGACY_BLOCK_5, Filters.Palpatine);

    public Card1263_022() {
        super(Side.LIGHT, 3, 3, 3, 3, 3, "General Solomahal", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_6_3, Rarity.V);
        setArmor(3);
        setLore("Leader. Scout.");
        setGameText("Once per game, may rearrange all Blockade Flagship sites (all cards at a given site move along with that site). Once per turn, if [Virtual Block 5] Palpatine on table (or imprisoned) and you just drew a battle destiny of 3, may use X Force (limit 3) to add X to that destiny.");
        addIcons(Icon.EPISODE_I, Icon.WARRIOR, Icon.A_NEW_HOPE);
        addKeywords(Keyword.GENERAL, Keyword.LEADER, Keyword.SCOUT);
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(String playerId, SwccgGame game, PhysicalCard self, int gameTextSourceCardId) {
        if (!GameConditions.canSpot(game, self, FLAGSHIP_SITES)
                || !GameConditions.isOncePerGame(game, self, REARRANGE)) {
            return null;
        }
        TopLevelGameTextAction action = new TopLevelGameTextAction(self, gameTextSourceCardId, REARRANGE);
        action.setText("Rearrange all Blockade Flagship sites");
        action.appendUsage(new OncePerGameEffect(action));
        action.appendEffect(new ChooseAndRearrangeRelatedSitesEffect(action, playerId, FLAGSHIP_SITES));
        return List.of(action);
    }

    @Override
    protected List<OptionalGameTextTriggerAction> getGameTextOptionalAfterTriggers(String playerId, SwccgGame game, EffectResult result, PhysicalCard self, int gameTextSourceCardId) {
        if (!TriggerConditions.isBattleDestinyJustDrawnBy(game, result, playerId)
                || !Float.valueOf(3).equals(((DestinyDrawnResult) result).getDestinyDrawValue())
                || !GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, ADD_DESTINY)
                || !(GameConditions.canSpot(game, self, BLOCK_FIVE_PALPATINE)
                || GameConditions.canSpot(game, self, SpotOverride.INCLUDE_CAPTIVE, Filters.and(BLOCK_FIVE_PALPATINE, Filters.imprisoned)))) {
            return null;
        }
        int max = Math.min(3, GameConditions.forceAvailableToUse(game, playerId));
        if (max == 0) {
            return null;
        }
        OptionalGameTextTriggerAction action = new OptionalGameTextTriggerAction(self, gameTextSourceCardId, ADD_DESTINY);
        action.setText("Use Force to add to battle destiny");
        action.appendUsage(new OncePerTurnEffect(action));
        action.appendCost(new PlayoutDecisionEffect(action, playerId, new IntegerAwaitingDecision("Choose amount of Force to use", 1, max, max) {
            @Override
            public void decisionMade(int amount) {
                action.appendCost(new UseForceEffect(action, playerId, amount));
                action.appendEffect(new ModifyDestinyEffect(action, amount));
            }
        }));
        return List.of(action);
    }
}
