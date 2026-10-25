package com.gempukku.swccgo.cards.set1220.light;

import com.gempukku.swccgo.cards.AbstractRebel;
import com.gempukku.swccgo.cards.GameConditions;
import com.gempukku.swccgo.cards.effects.usage.OncePerTurnEffect;
import com.gempukku.swccgo.common.ExpansionSet;
import com.gempukku.swccgo.common.GameTextActionId;
import com.gempukku.swccgo.common.Icon;
import com.gempukku.swccgo.common.Keyword;
import com.gempukku.swccgo.common.Rarity;
import com.gempukku.swccgo.common.Side;
import com.gempukku.swccgo.common.Uniqueness;
import com.gempukku.swccgo.filters.Filter;
import com.gempukku.swccgo.filters.Filters;
import com.gempukku.swccgo.game.PhysicalCard;
import com.gempukku.swccgo.game.SwccgGame;
import com.gempukku.swccgo.logic.actions.TopLevelGameTextAction;
import com.gempukku.swccgo.logic.effects.UseForceEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardFromReserveDeckEffect;
import com.gempukku.swccgo.logic.effects.choose.DeployCardToTargetFromReserveDeckEffect;
import com.gempukku.swccgo.logic.modifiers.ForfeitModifier;
import com.gempukku.swccgo.logic.modifiers.IsPoweredModifier;
import com.gempukku.swccgo.logic.modifiers.Modifier;

import java.util.LinkedList;
import java.util.List;

/**
 * Set: Virtual Block 2
 * Type: Character
 * Subtype: Rebel
 * Title: General Carlist Rieekan
 */
public class Card1220_090 extends AbstractRebel {
    public Card1220_090() {
        super(Side.LIGHT, 1, 3, 2, 2, 5, "General Carlist Rieekan", Uniqueness.UNIQUE, ExpansionSet.VIRTUAL_BLOCK_2, Rarity.V);
        setVirtualSuffix(true);
        setLore("One of the original founders of the Rebel Alliance. Former civilian strategist with the House of Organa. Somber leader of Echo Base.");
        setGameText("Your artillery weapons at same and related sites are powered and forfeit +2. Once per turn, may use 1 Force to deploy one A New Secret Base, Echo Base Operations, Haven (to Hoth), a [Hoth] location, a [Hoth] artillery weapon, or a non-unique [Hoth] Rebel from Reserve Deck; reshuffle.");
        addIcons(Icon.WARRIOR, Icon.HOTH);
        addKeywords(Keyword.GENERAL);
    }

    @Override
    protected List<Modifier> getGameTextWhileActiveInPlayModifiers(SwccgGame game, final PhysicalCard self) {
        Filter artillery = Filters.and(Filters.your(self), Filters.artillery_weapon, Filters.at(Filters.sameOrRelatedSite(self)));
        List<Modifier> modifiers = new LinkedList<Modifier>();
        modifiers.add(new IsPoweredModifier(self, artillery));
        modifiers.add(new ForfeitModifier(self, artillery, 2));
        return modifiers;
    }

    @Override
    protected List<TopLevelGameTextAction> getGameTextTopLevelActions(final String playerId, SwccgGame game, final PhysicalCard self, int gameTextSourceCardId) {
        List<TopLevelGameTextAction> actions = new LinkedList<TopLevelGameTextAction>();
        GameTextActionId gameTextActionId = GameTextActionId.LEGACY__RIEEKAN_V__DEPLOY_FROM_RESERVE;

        Filter mixedFilter = Filters.or(
                Filters.title("A New Secret Base"),
                Filters.Echo_Base_Operations,
                Filters.and(Filters.location, Filters.icon(Icon.HOTH)),
                Filters.and(Filters.artillery_weapon, Filters.icon(Icon.HOTH)),
                Filters.and(Filters.non_unique, Filters.Rebel, Filters.icon(Icon.HOTH)));

        if (GameConditions.isOncePerTurn(game, self, playerId, gameTextSourceCardId, gameTextActionId)
                && GameConditions.canUseForce(game, playerId, 1)
                && GameConditions.canDeployCardFromReserveDeck(game, playerId, self, gameTextActionId)) {

            final TopLevelGameTextAction mixedAction = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            mixedAction.setText("Deploy card from Reserve Deck");
            mixedAction.setActionMsg("Deploy A New Secret Base, Echo Base Operations, a Hoth location, a Hoth artillery weapon, or a non-unique Hoth Rebel from Reserve Deck");
            mixedAction.appendUsage(
                    new OncePerTurnEffect(mixedAction));
            mixedAction.appendCost(
                    new UseForceEffect(mixedAction, playerId, 1));
            mixedAction.appendEffect(
                    new DeployCardFromReserveDeckEffect(mixedAction, mixedFilter, true));
            actions.add(mixedAction);

            final TopLevelGameTextAction havenAction = new TopLevelGameTextAction(self, gameTextSourceCardId, gameTextActionId);
            havenAction.setText("Deploy Haven to Hoth");
            havenAction.setActionMsg("Deploy Haven to Hoth from Reserve Deck");
            havenAction.appendUsage(
                    new OncePerTurnEffect(havenAction));
            havenAction.appendCost(
                    new UseForceEffect(havenAction, playerId, 1));
            havenAction.appendEffect(
                    new DeployCardToTargetFromReserveDeckEffect(havenAction, Filters.Haven, Filters.Hoth_system, true));
            actions.add(havenAction);
        }
        return actions;
    }
}
